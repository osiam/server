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

import java.io.IOException;

import javax.inject.Inject;

import org.osiam.helper.HttpClientHelper;
import org.osiam.helper.HttpClientRequestResult;
import org.osiam.helper.ObjectMapperWithExtensionConfig;
import org.osiam.web.resource.MeUserRepresentation;
import org.osiam.web.util.HttpHeader;
import org.springframework.stereotype.Service;

/**
 * This class using the /me endpoint to get information about the provided access token
 */
@Service
public class AccessTokenInformationProvider {

    @Inject
    private ResourceServerUriBuilder resourceServerUriBuilder;
    @Inject
    private HttpClientHelper httpClientHelper;
    @Inject
    private ObjectMapperWithExtensionConfig mapper;

    /**
     * Calling resource server /me endpoint for access token information.
     * @param token the valid access token
     * @return the user id from token information
     */
    public String getUserIdFromToken(String token) throws IOException {
        //get the resource server uri
        String uri = resourceServerUriBuilder.buildMeEndpointUri();

        //calling the /me endpoint
        HttpClientRequestResult result = httpClientHelper.executeHttpGet(uri, HttpHeader.AUTHORIZATION, token);

        //Check if the result has an error, possible that access token is invalid
        if (result.getBody().contains("error")) {
            throw new IllegalArgumentException(result.getBody());
        }

        //serialize the json response to a class
        MeUserRepresentation meUserRepresentation = mapper.readValue(result.getBody(), MeUserRepresentation.class);

        return meUserRepresentation.getId();
    }
}