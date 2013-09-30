package org.osiam.resources

import spock.lang.Specification

/**
 *
 *
 * @author: Andreas Grau, tarent solutions GmbH, 27.09.13
 * @version: 1.0
 */
class ClientSpringTest extends Specification {
    def clientSpring = new ClientSpring()

    def "resource ids should be empty"() {
        when:
        def ids = clientSpring.getResourceIds()
        then:
        ids.empty
    }


    def "isSecretRequired should be true"() {
        when:
        def result = clientSpring.isSecretRequired()
        then:
        result
    }

    def "isScoped should be true"() {
        when:
        def result = clientSpring.isScoped()
        then:
        result
    }

    def "getRegisteredRedirectUri should return a set which contains redirect_uri"() {
        given:
        clientSpring.setRedirectUri("should_i_stay_or_should_i_go_now")
        when:
        def result = clientSpring.getRegisteredRedirectUri()
        then:
        result == [clientSpring.getRedirectUri()] as Set
    }

    def "getAuthorities should be empty"() {
        when:
        def result = clientSpring.getAuthorities()
        then:
        result.empty
    }


    def "getAdditionalInformation should be empty"() {
        when:
        def result = clientSpring.getAdditionalInformation()
        then:
        !result
    }

}
