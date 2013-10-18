package org.osiam.helper;

public class HttpClientRequestResult {

    final private String body;
    final private int statusCode;

    public HttpClientRequestResult(String body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
