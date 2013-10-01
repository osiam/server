package org.osiam.helper

import org.apache.http.Header
import org.apache.http.HeaderIterator
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.ProtocolVersion
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.params.HttpParams
import spock.lang.Specification

/**
 *  Tests for HttpClientHelper.
 *
 * @author: Andreas Grau, tarent solutions GmbH, 30.09.13
 * @version: 1.0
 */
class HttpClientHelperTest extends Specification {

    def "the helper constructor should work"() {
        when: "a new helper instance is created"
        HttpClientHelper helper = new HttpClientHelper()

        then: "no exception should be thrown"
        notThrown(Exception)

        and: "the object shouldn't be null"
        helper != null
    }

}
