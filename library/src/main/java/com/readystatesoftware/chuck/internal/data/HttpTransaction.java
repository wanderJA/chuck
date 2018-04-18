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
package com.readystatesoftware.chuck.internal.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;

import com.google.gson.reflect.TypeToken;
import com.readystatesoftware.chuck.internal.support.FormatUtils;
import com.readystatesoftware.chuck.internal.support.JsonConvertor;

import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.annotation.Index;
import okhttp3.Headers;

@Entity
public class HttpTransaction {

    public enum Status {
        Requested,
        Complete,
        Failed
    }
    @PrimaryKey(autoGenerate = true)
    private Long _id;
    @ColumnInfo
    private long requestDate;
    @ColumnInfo
    private long responseDate;
    @ColumnInfo
    private Long tookMs;

    @ColumnInfo
    private String protocol;
    @ColumnInfo
    private String method;
    @ColumnInfo
    private String url;
    @ColumnInfo
    private String host;
    @ColumnInfo
    private String path;
    @ColumnInfo
    private String scheme;

    @ColumnInfo
    private Long requestContentLength;
    @ColumnInfo
    private String requestContentType;
    @ColumnInfo
    private String requestHeaders;
    @ColumnInfo
    private String requestBody;
    @ColumnInfo
    private int requestBodyIsPlainText = 1;

    @ColumnInfo
    private Integer responseCode;
    @ColumnInfo
    private String responseMessage;
    @ColumnInfo
    private String error;
    @ColumnInfo
    private Long responseContentLength;
    @ColumnInfo
    private String responseContentType;
    @ColumnInfo
    private String responseHeaders;
    @ColumnInfo
    private String responseBody;
    @ColumnInfo
    private int responseBodyIsPlainText = 1;

    public Long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    public long getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(long requestDate) {
        this.requestDate = requestDate;
    }

    public long getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(long responseDate) {
        this.responseDate = responseDate;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getFormattedRequestBody() {
        return formatBody(requestBody, requestContentType);
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public boolean requestBodyIsPlainText() {
        return requestBodyIsPlainText == 1;
    }

    public void setRequestBodyIsPlainText(boolean requestBodyIsPlainText) {
        this.requestBodyIsPlainText = requestBodyIsPlainText ? 1 : 0;
    }

    public Long getRequestContentLength() {
        return requestContentLength;
    }

    public void setRequestContentLength(Long requestContentLength) {
        this.requestContentLength = requestContentLength;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getFormattedResponseBody() {
        return formatBody(responseBody, responseContentType);
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public boolean responseBodyIsPlainText() {
        return responseBodyIsPlainText == 1;
    }

    public void setResponseBodyIsPlainText(boolean responseBodyIsPlainText) {
        this.responseBodyIsPlainText = responseBodyIsPlainText ? 1 : 0;
    }

    public int getResponseBodyIsPlainText() {
        return responseBodyIsPlainText;
    }

    public void setResponseBodyIsPlainText(int responseBodyIsPlainText) {
        this.responseBodyIsPlainText = responseBodyIsPlainText;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public Long getResponseContentLength() {
        return responseContentLength;
    }

    public void setResponseContentLength(Long responseContentLength) {
        this.responseContentLength = responseContentLength;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Long getTookMs() {
        return tookMs;
    }

    public void setTookMs(Long tookMs) {
        this.tookMs = tookMs;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        Uri uri = Uri.parse(url);
        host = uri.getHost();
        path = uri.getPath() + ((uri.getQuery() != null) ? "?" + uri.getQuery() : "");
        scheme = uri.getScheme();
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getScheme() {
        return scheme;
    }

    public void setRequestHeaders(Headers headers) {
        requestHeaders = JsonConvertor.getInstance().toJson(toHttpHeaderList(headers));
    }

    public List<HttpHeader> getRequestHeadersList() {
        return JsonConvertor.getInstance().fromJson(requestHeaders,
                new TypeToken<List<HttpHeader>>() {
                }.getType());
    }

    public String getRequestHeadersString(boolean withMarkup) {
        return FormatUtils.formatHeaders(getRequestHeadersList(), withMarkup);
    }

    public void setResponseHeaders(Headers headers) {
        setResponseHeaders(toHttpHeaderList(headers));
    }

    public void setResponseHeaders(List<HttpHeader> headers) {
        responseHeaders = JsonConvertor.getInstance().toJson(headers);
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public int getRequestBodyIsPlainText() {
        return requestBodyIsPlainText;
    }

    public void setRequestBodyIsPlainText(int requestBodyIsPlainText) {
        this.requestBodyIsPlainText = requestBodyIsPlainText;
    }

    public String getResponseHeaders() {
        return responseHeaders;
    }

    public List<HttpHeader> getResponseHeadersList() {
        return JsonConvertor.getInstance().fromJson(responseHeaders,
                new TypeToken<List<HttpHeader>>() {
                }.getType());
    }

    public String getResponseHeadersString(boolean withMarkup) {
        return FormatUtils.formatHeaders(getResponseHeadersList(), withMarkup);
    }

    public Status getStatus() {
        if (error != null) {
            return Status.Failed;
        } else if (responseCode == null) {
            return Status.Requested;
        } else {
            return Status.Complete;
        }
    }

    public String getRequestStartTimeString() {
        return FormatUtils.formatTimeDate(requestDate);
    }

    public String getRequestDateString() {
        return FormatUtils.formatDate(requestDate);
    }

    public String getResponseDateString() {
        return FormatUtils.formatDate(responseDate);
    }

    public String getDurationString() {
        return (tookMs != null) ? +tookMs + " ms" : null;
    }

    public String getRequestSizeString() {
        return formatBytes((requestContentLength != null) ? requestContentLength : 0);
    }

    public String getResponseSizeString() {
        return (responseContentLength != null) ? formatBytes(responseContentLength) : null;
    }

    public String getTotalSizeString() {
        long reqBytes = (requestContentLength != null) ? requestContentLength : 0;
        long resBytes = (responseContentLength != null) ? responseContentLength : 0;
        return formatBytes(reqBytes + resBytes);
    }

    public String getResponseSummaryText() {
        switch (getStatus()) {
            case Failed:
                return error;
            case Requested:
                return null;
            default:
                return String.valueOf(responseCode) + " " + responseMessage;
        }
    }

    public String getNotificationText() {
        switch (getStatus()) {
            case Failed:
                return " ! ! !  " + path;
            case Requested:
                return " . . .  " + path;
            default:
                return String.valueOf(responseCode) + " " + path;
        }
    }

    public boolean isSsl() {
        return scheme.toLowerCase().equals("https");
    }

    private List<HttpHeader> toHttpHeaderList(Headers headers) {
        List<HttpHeader> httpHeaders = new ArrayList<>();
        for (int i = 0, count = headers.size(); i < count; i++) {
            httpHeaders.add(new HttpHeader(headers.name(i), headers.value(i)));
        }
        return httpHeaders;
    }

    private String formatBody(String body, String contentType) {
        if (contentType != null && contentType.toLowerCase().contains("json")) {
            return FormatUtils.formatJson(body);
        } else if (contentType != null && contentType.toLowerCase().contains("xml")) {
            return FormatUtils.formatXml(body);
        } else {
            return body;
        }
    }

    private String formatBytes(long bytes) {
        return FormatUtils.formatByteCount(bytes, true);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getRequestHeaders(String requestHeaders) {
        return this.requestHeaders;
    }

    public int isRequestBodyIsPlainText() {
        return requestBodyIsPlainText;
    }

    public void setResponseHeaders(String responseHeaders) {
        this.responseHeaders = responseHeaders;
    }
}
