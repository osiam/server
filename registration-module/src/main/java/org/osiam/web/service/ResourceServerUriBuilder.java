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

package org.osiam.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class to build the uri for the resource server.
 */
@Component
public class ResourceServerUriBuilder {

    @Value("${osiam.server.port}")
    private int serverPort;
    @Value("${osiam.server.host}")
    private String serverHost;
    @Value("${osiam.server.http.scheme}")
    private String httpScheme;

    /**
     * Method to build the resource server uri and appending the user id if it is not null or empty.
     * @param userId the user id to be appended to the resource server uri
     * @return the resource server uri including the user id
     */
    public String buildUsersUriWithUserId(String userId){
        StringBuilder stringBuilder = buildResourceServerBaseUri();
        stringBuilder.append("/Users/")
                .append(userId != null ? userId : "");

        return stringBuilder.toString();
    }

    /**
     * Method to build the resource server uri for /me endpoint
     * @return the resource server uri
     */
    public String buildMeEndpointUri() {
        StringBuilder stringBuilder = buildResourceServerBaseUri();
        stringBuilder.append("/me");
        return stringBuilder.toString();
    }

    private StringBuilder buildResourceServerBaseUri() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(httpScheme)
                .append("://")
                .append(serverHost)
                .append(":")
                .append(serverPort)
                .append("/osiam-resource-server");

        return stringBuilder;
    }
}