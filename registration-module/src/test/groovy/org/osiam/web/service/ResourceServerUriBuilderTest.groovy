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

import org.osiam.web.service.ResourceServerUriBuilder;

import spock.lang.Specification

/**
 * Resource server uri builder test.
 */
class ResourceServerUriBuilderTest extends Specification {

    def httpScheme = "http"
    def serverHost = "localhost"
    def serverPort = 8080

    def resourceServerUriBuilder = new ResourceServerUriBuilder(httpScheme: httpScheme, serverHost: serverHost,
            serverPort: serverPort)

    def "should return the resource server uri to /Users with appended user id"(){
        when:
        def uri = resourceServerUriBuilder.buildUsersUriWithUserId("theUserId")

        then:
        uri == "http://localhost:8080/osiam-resource-server/Users/theUserId"
    }

    def "should not throw an exception and append an empty string for user id if it is null"() {
        when:
        def uri = resourceServerUriBuilder.buildUsersUriWithUserId(null)

        then:
        uri == "http://localhost:8080/osiam-resource-server/Users/"
    }

    def "should return the /me uri"(){
        when:
        def uri = resourceServerUriBuilder.buildMeEndpointUri()

        then:
        uri == "http://localhost:8080/osiam-resource-server/me"
    }
}