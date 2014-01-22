/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

@Named("httpClientHelper")
public class HttpClientHelper {

    private HttpClient client; //NOSONAR : need to mock therefore the final identifier was removed

    private static final String ENCODING = "UTF-8";

    public HttpClientHelper() {
        PoolingClientConnectionManager poolingClientConnectionManager = new PoolingClientConnectionManager();
        client = new DefaultHttpClient(poolingClientConnectionManager);
    }

    public HttpClientRequestResult executeHttpGet(String url, String headerName, String headerValue) {
        HttpClientRequestResult result;
        HttpGet request = new HttpGet(url);

        request = addHeaderToRequest(headerName, headerValue, request);
        request = addDefaultHeaderToRequest(request);

        try {
            HttpResponse response = client.execute(request);
            String responseBody = getResponseBody(response);
            int statusCode = response.getStatusLine().getStatusCode();
            result = new HttpClientRequestResult(responseBody, statusCode);
        } catch (IOException e) {
            throw new RuntimeException(e); //NOSONAR : Wrapping to a non checked exception
        }
        return result;
    }

    public HttpClientRequestResult executeHttpPut(String url, String parameterName, String parameterValue, String headerName, String headerValue) {
        HttpPut request = new HttpPut(url);
        request = addHeaderToRequest(headerName, headerValue, request);
        request = addDefaultHeaderToRequest(request);

        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair(parameterName, parameterValue));

        return executeHttpRequest(request, null, formParams);
    }

    public HttpClientRequestResult executeHttpPut(String url, String body, String headerName, String headerValue) {
        HttpPut request = new HttpPut(url);
        request = addHeaderToRequest(headerName, headerValue, request);
        request = addDefaultHeaderToRequest(request);

        return executeHttpRequest(request, body, null);
    }

    public HttpClientRequestResult executeHttpPost(String url, String body, String headerName, String headerValue){
        HttpPost request = new HttpPost(url);
        request = addHeaderToRequest(headerName, headerValue, request);
        request = addDefaultHeaderToRequest(request);

        return executeHttpRequest(request, body, null);
    }

    public HttpClientRequestResult executeHttpPatch(String url, String body, String headerName, String headerValue) {
        HttpPatch request = new HttpPatch(url);
        request = addHeaderToRequest(headerName, headerValue, request);
        request = addDefaultHeaderToRequest(request);

        return executeHttpRequest(request, body, null);
    }

    private <T extends HttpRequestBase> T addHeaderToRequest(String headerName, String headerValue, T request) {
        if (headerName != null && headerValue != null) {
            request.addHeader(headerName, headerValue);
        }
        return request;
    }
    
    private <T extends HttpRequestBase> T addDefaultHeaderToRequest(T request) {
        request.addHeader("accept", ContentType.APPLICATION_JSON.getMimeType());
        return request;
    }

    private HttpClientRequestResult executeHttpRequest(HttpEntityEnclosingRequestBase request, String body, List<NameValuePair> formParams) {
        try {
            HttpEntityEnclosingRequestBase requestWithEntity = addEntityToRequest(request, body, formParams);
            HttpResponse response = client.execute(requestWithEntity);
            String responseBody = getResponseBody(response);
            int statusCode = response.getStatusLine().getStatusCode();

            return new HttpClientRequestResult(responseBody, statusCode);
        } catch (IOException e) {
            throw new RuntimeException(e); //NOSONAR : Wrapping to a non checked exception
        }
    }

    private HttpEntityEnclosingRequestBase addEntityToRequest(HttpEntityEnclosingRequestBase request, String body, List<NameValuePair> formParams) throws UnsupportedEncodingException {
        if (body == null && formParams != null) {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, ENCODING);
            request.setEntity(formEntity);
        } else if (formParams == null && body != null){
            StringEntity entity = new StringEntity(body, ENCODING);
            request.setEntity(entity);
        }
        return request;
    }

    private String getResponseBody(HttpResponse response) throws IOException {
        BufferedReader rd = null;
        final StringBuffer stringBuffer = new StringBuffer("");

        try {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), ENCODING));
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
        } finally {
            IOUtils.closeQuietly(rd);
        }

        return stringBuffer.toString();
    }
}