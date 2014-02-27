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

package org.osiam.web.service

import org.osiam.helper.HttpClientHelper
import org.osiam.helper.HttpClientRequestResult
import org.osiam.helper.ObjectMapperWithExtensionConfig
import org.osiam.web.resource.MeUserRepresentation
import org.osiam.web.service.AccessTokenInformationProvider;
import org.osiam.web.service.ResourceServerUriBuilder;
import org.osiam.web.util.HttpHeader;
import org.springframework.http.HttpStatus

import spock.lang.Specification

/**
 * Test for the access token information provider class.
 */
class AccessTokenInformationProviderTest extends Specification {

    def resourceServerUriBuilder = Mock(ResourceServerUriBuilder)
    def httpClientHelper = Mock(HttpClientHelper)
    def mapper = Mock(ObjectMapperWithExtensionConfig)

    def accessTokenInformationProvider = new AccessTokenInformationProvider(resourceServerUriBuilder: resourceServerUriBuilder,
        httpClientHelper: httpClientHelper, mapper: mapper)

    def "the user id should be provided by retrieving information from the access token"() {
        given:
        def accessToken = "theToken"
        def uri = "/me"
        def requestResult = Mock(HttpClientRequestResult)

        def jsonUserValues = "{some json stuff}"
        def meUserMock = Mock(MeUserRepresentation)

        def meUserId ="1234"

        when:
        def userId = accessTokenInformationProvider.getUserIdFromToken(accessToken)

        then:
        1 * resourceServerUriBuilder.buildMeEndpointUri() >> uri
        1 * httpClientHelper.executeHttpGet(uri, HttpHeader.AUTHORIZATION, accessToken) >> requestResult
        2 * requestResult.getBody() >> jsonUserValues
        1 * mapper.readValue(jsonUserValues, MeUserRepresentation) >> meUserMock
        1 * meUserMock.getId() >> meUserId
        userId == meUserId
    }

    def "should throw IllegalArgumentException and append the error message if it was impossible to get token information"() {
        given:
        def accessToken = "invalidToken"
        def uri = "/me"
        def requestResult = new HttpClientRequestResult("{\"error\":\"unauthorized\"}", HttpStatus.UNAUTHORIZED.value())

        when:
        accessTokenInformationProvider.getUserIdFromToken(accessToken)

        then:
        1 * resourceServerUriBuilder.buildMeEndpointUri() >> uri
        1 * httpClientHelper.executeHttpGet(uri, HttpHeader.AUTHORIZATION, accessToken) >> requestResult
        def exception = thrown(IllegalArgumentException)
        exception.getMessage() == "{\"error\":\"unauthorized\"}"
    }
}