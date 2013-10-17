package org.osiam.helper;

public class HttpClientHelperResult {

    final private String body;
    final private int statusCode;

    public HttpClientHelperResult(String body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }
}
