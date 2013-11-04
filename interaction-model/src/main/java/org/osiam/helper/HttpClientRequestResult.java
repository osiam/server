package org.osiam.helper;

public class HttpClientRequestResult {

    private final String body;
    private final int statusCode;

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
