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
    def httpClientHelper = new HttpClientHelper(client: httpClientMock, response: httpResponseMock)

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
        result == content
        httpClientHelper.getStatusCode() == 200
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
        def statusLineMock = Mock(StatusLine)

        when:
        httpClientHelper.executeHttpPut("http://localhost:8080/test", "paramName", "paramValue")

        then:
        1 * httpClientMock.execute(_) >> httpResponseMock
        1 * httpResponseMock.getStatusLine() >> statusLineMock
        1 * statusLineMock.getStatusCode() >> 200
        httpClientHelper.getStatusCode() == 200
    }

    def "should wrap IOException from httpClientHelper.executeHttpPut to RuntimeException if"() {
        when:
        httpClientHelper.executeHttpPut("http://localhost:8080/test", "paramName", "paramValue")

        then:
        1 * httpClientMock.execute(_) >> {throw new IOException()}
        thrown(RuntimeException)
    }
}
