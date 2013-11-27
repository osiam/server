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

package org.osiam.helper

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import spock.lang.Specification

/**
 *  Tests for HttpClientHelper.
 *
 * @author: Andreas Grau, tarent solutions GmbH, 30.09.13
 * @version: 1.0
 */
class HttpClientHelperTest extends Specification {

    def httpClientMock = Mock(HttpClient)
    def httpResponseMock = Mock(HttpResponse)
    def httpClientHelper = new HttpClientHelper(client: httpClientMock)

    def "the helper constructor should work"() {
        when: "a new helper instance is created"
        HttpClientHelper helper = new HttpClientHelper()

        then: "no exception should be thrown"
        notThrown(Exception)

        and: "the object shouldn't be null"
        helper != null
    }

    def "should be able to execute the http get method and getting response"() {
        given:
        def httpEntityMock = Mock(HttpEntity)
        def content = "The response content"
        def statusLineMock = Mock(StatusLine)

        when:
        def result = httpClientHelper.executeHttpGet("http://localhost:8080/test")

        then:
        1 * httpClientMock.execute(_) >> httpResponseMock
        1 * httpResponseMock.getStatusLine() >> statusLineMock
        1 * statusLineMock.getStatusCode() >> 200
        1 * httpResponseMock.getEntity() >> httpEntityMock
        1 * httpEntityMock.getContent() >> new ByteArrayInputStream(content.getBytes("UTF-8"))
        result.body == content
        result.statusCode == 200
    }

    def "should wrap IOException from httpClientHelper.executeHttpGet to RuntimeException if"() {
        when:
        httpClientHelper.executeHttpGet("http://localhost:8080/test")

        then:
        1 * httpClientMock.execute(_) >> {throw new IOException()}
        thrown(RuntimeException)
    }

    def "should be able to execute the http put method for updating"() {
        given:
        def httpEntityMock = Mock(HttpEntity)
        def content = "The response content"
        def statusLineMock = Mock(StatusLine)

        when:
        def result = httpClientHelper.executeHttpPut("http://localhost:8080/test", "paramName", "paramValue")

        then:
        1 * httpClientMock.execute(_) >> httpResponseMock
        1 * httpResponseMock.getStatusLine() >> statusLineMock
        1 * statusLineMock.getStatusCode() >> 200
        1 * httpResponseMock.getEntity() >> httpEntityMock
        1 * httpEntityMock.getContent() >> new ByteArrayInputStream(content.getBytes("UTF-8"))
        result.body == content
        result.statusCode == 200
    }

    def "should wrap IOException from httpClientHelper.executeHttpPut to RuntimeException if"() {
        when:
        httpClientHelper.executeHttpPut("http://localhost:8080/test", "paramName", "paramValue")

        then:
        1 * httpClientMock.execute(_) >> {throw new IOException()}
        thrown(RuntimeException)
    }
}
