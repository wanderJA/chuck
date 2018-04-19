/*
 * Copyright (C) 2017 Jeff Gilfelt.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.readystatesoftware.chuck.internal.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.readystatesoftware.chuck.R;
import com.readystatesoftware.chuck.internal.data.HttpTransaction;
import com.readystatesoftware.chuck.internal.room.RoomUtils;
import com.readystatesoftware.chuck.internal.room.TransactionDao;
import com.readystatesoftware.chuck.internal.support.NotificationHelper;
import com.readystatesoftware.chuck.internal.support.SQLiteUtils;
import com.readystatesoftware.chuck.internal.support.ThreadUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class TransactionListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private String currentFilter;
    private OnListFragmentInteractionListener listener;
    private TransactionAdapter adapter;
    private Context mContext;
    private TransactionDao transactionDao;
    private RecyclerView mRecyclerView;
    private boolean loading;
    private LinearLayoutManager layoutManager;
    private int pageNum;
    private int pageSize = 500;

    public TransactionListFragment() {
    }

    public static TransactionListFragment newInstance() {
        return new TransactionListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chuck_fragment_transaction_list, container, false);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            layoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!loading) {
                        int itemCount = layoutManager.getItemCount();
                        if (itemCount < pageSize) {
                            return;
                        }
                        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                        if (lastVisibleItemPosition > itemCount - 50) {
                            pageNum++;
                            loadDate(true);
                        }
                    }
                }
            });
            adapter = new TransactionAdapter(getContext(), listener);
            mRecyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadDate(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            listener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        mContext = context;
        transactionDao = RoomUtils.getInstance().getTransaction(getContext());
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chuck_main, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear) {
            ThreadUtils.getSingleDB().execute(new Runnable() {
                @Override
                public void run() {
                    transactionDao.deleteAll();
                }
            });
            adapter.clear();
            NotificationHelper.clearBuffer();
            return true;
        } else if (item.getItemId() == R.id.browse_sql) {
            SQLiteUtils.browseDatabase(getContext());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateItem(HttpTransaction httpTransaction) {
        if (httpTransaction != null) {
            int i = 0;
            for (HttpTransaction transaction : adapter.getData()) {
                if (transaction.getId() == httpTransaction.getId()) {
                    transaction.copy(httpTransaction);
                    adapter.notifyItemChanged(i);
                    return;
                }
                i++;
            }
            adapter.add(httpTransaction);
            if (!mRecyclerView.isComputingLayout()) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        }
    }

    private void loadDate(final boolean isLoadMore) {
        loading = true;
        ThreadUtils.getSingleDB().execute(new Runnable() {
            @Override
            public void run() {
                final List<HttpTransaction> list;
                if (!TextUtils.isEmpty(currentFilter)) {
                    if (TextUtils.isDigitsOnly(currentFilter)) {
                        //responseCode
                        list = transactionDao.findResponse(currentFilter);
                    } else {
                        list = transactionDao.findPath("%" + currentFilter + "%");
                    }
                } else {
                    list = transactionDao.getPage(pageNum * pageSize, pageSize);
                }
                ThreadUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isLoadMore) {
                            adapter.addData(list);
                        } else {
                            adapter.setData(list);
                        }
                        loading = false;
                    }
                });
            }
        });


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        currentFilter = newText;
        loadDate(false);
        return true;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(HttpTransaction item);
    }
}
