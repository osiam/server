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
        def result = httpClientHelper.executeHttpGet("http://localhost:8080/test", null, null)

        then:
        1 * httpClientMock.execute(_) >> httpResponseMock
        1 * httpResponseMock.getStatusLine() >> statusLineMock
        1 * statusLineMock.getStatusCode() >> 200
        1 * httpResponseMock.getEntity() >> httpEntityMock
        1 * httpEntityMock.getContent() >> new ByteArrayInputStream(content.getBytes("UTF-8"))
        result.body == content
        result.statusCode == 200
    }

    def "should be able to execute the http get method with header and getting response"() {
        given:
        def httpEntityMock = Mock(HttpEntity)
        def content = "The response content"
        def statusLineMock = Mock(StatusLine)

        when:
        def result = httpClientHelper.executeHttpGet("http://localhost:8080/test", "headerName", "headerValue")

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
        httpClientHelper.executeHttpGet("http://localhost:8080/test", null, null)

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
        def result = httpClientHelper.executeHttpPut("http://localhost:8080/test", "paramName", "paramValue", null, null)

        then:
        1 * httpClientMock.execute(_) >> httpResponseMock
        1 * httpResponseMock.getStatusLine() >> statusLineMock
        1 * statusLineMock.getStatusCode() >> 200
        1 * httpResponseMock.getEntity() >> httpEntityMock
        1 * httpEntityMock.getContent() >> new ByteArrayInputStream(content.getBytes("UTF-8"))
        result.body == content
        result.statusCode == 200
    }

    def "should be able to execute the http put method with header and parameter for updating"() {
        given:
        def httpEntityMock = Mock(HttpEntity)
        def content = "The response content"
        def statusLineMock = Mock(StatusLine)

        when:
        def result = httpClientHelper.executeHttpPut("http://localhost:8080/test", "paramName", "paramValue", "headerName", "headerValue")

        then:
        1 * httpClientMock.execute(_) >> httpResponseMock
        1 * httpResponseMock.getStatusLine() >> statusLineMock
        1 * statusLineMock.getStatusCode() >> 200
        1 * httpResponseMock.getEntity() >> httpEntityMock
        1 * httpEntityMock.getContent() >> new ByteArrayInputStream(content.getBytes("UTF-8"))
        result.body == content
        result.statusCode == 200
    }

    def "should be able to execute the http put method with header and body for updating"() {
        given:
        def httpEntityMock = Mock(HttpEntity)
        def content = "The response content"
        def statusLineMock = Mock(StatusLine)

        when:
        def result = httpClientHelper.executeHttpPut("http://localhost:8080/test", "theBody", "headerName", "headerValue")

        then:
        1 * httpClientMock.execute(_) >> httpResponseMock
        1 * httpResponseMock.getStatusLine() >> statusLineMock
        1 * statusLineMock.getStatusCode() >> 200
        1 * httpResponseMock.getEntity() >> httpEntityMock
        1 * httpEntityMock.getContent() >> new ByteArrayInputStream(content.getBytes("UTF-8"))
        result.body == content
        result.statusCode == 200
    }

    def "should be able to execute the http post method with header for creating"() {
        given:
        def httpEntityMock = Mock(HttpEntity)
        def content = "The response content"
        def statusLineMock = Mock(StatusLine)

        when:
        def result = httpClientHelper.executeHttpPost("http://localhost:8080/test", "theBody", "headerName", "headerValue")

        then:
        1 * httpClientMock.execute(_) >> httpResponseMock
        1 * httpResponseMock.getStatusLine() >> statusLineMock
        1 * statusLineMock.getStatusCode() >> 200
        1 * httpResponseMock.getEntity() >> httpEntityMock
        1 * httpEntityMock.getContent() >> new ByteArrayInputStream(content.getBytes("UTF-8"))
        result.body == content
        result.statusCode == 200
    }

    def "should wrap IOException from httpClientHelper.executeHttpPut to RuntimeException"() {
        when:
        httpClientHelper.executeHttpPut("http://localhost:8080/test", "paramName", "paramValue", null, null)

        then:
        1 * httpClientMock.execute(_) >> {throw new IOException()}
        thrown(RuntimeException)
    }

    def "should be able to execute the http patch method with header, body and uri parameters"() {
        given:
        def httpEntityMock = Mock(HttpEntity)
        def content = "The response content"
        def statusLineMock = Mock(StatusLine)

        when:
        def result = httpClientHelper.executeHttpPatch("http://localhost:8080/test", "theBody", "headerName", "headerValue")

        then:
        1 * httpClientMock.execute(_) >> httpResponseMock
        1 * httpResponseMock.getStatusLine() >> statusLineMock
        1 * statusLineMock.getStatusCode() >> 200
        1 * httpResponseMock.getEntity() >> httpEntityMock
        1 * httpEntityMock.getContent() >> new ByteArrayInputStream(content.getBytes("UTF-8"))
        result.body == content
        result.statusCode == 200
    }
}
