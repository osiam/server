package org.osiam.web.util

import spock.lang.Specification

/**
 * Resource server uri builder test.
 * User: Jochen Todea
 * Date: 25.11.13
 * Time: 12:10
 * Created: with Intellij IDEA
 */
class ResourceServerUriBuilderTest extends Specification {

    def httpScheme = "http"
    def serverHost = "localhost"
    def serverPort = 8080

    def resourceServerUriBuilder = new ResourceServerUriBuilder(httpScheme: httpScheme, serverHost: serverHost,
            serverPort: serverPort)

    def "should return the resource server uri with appended user id"(){
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
}
