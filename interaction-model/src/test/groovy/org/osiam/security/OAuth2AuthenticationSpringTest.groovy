package org.osiam.security

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jochen
 * Date: 14.10.13
 * Time: 13:14
 * To change this template use File | Settings | File Templates.
 */
class OAuth2AuthenticationSpringTest extends Specification {

    def oAuth2AuthenticationSpring = new OAuth2AuthenticationSpring()

    def "should be able to set AuthenticationSpring"() {
        given:
        def authenticationSpringMock = Mock(AuthenticationSpring)

        when:
        oAuth2AuthenticationSpring.setAuthenticationSpring(authenticationSpringMock)

        then:
        oAuth2AuthenticationSpring.getAuthenticationSpring() == authenticationSpringMock
    }

    def "should be able to set AuthorizationRequestSpring"() {
        given:
        def authorizationRequestSpring = Mock(AuthorizationRequestSpring)

        when:
        oAuth2AuthenticationSpring.setAuthorizationRequestSpring(authorizationRequestSpring)

        then:
        oAuth2AuthenticationSpring.getAuthorizationRequestSpring() == authorizationRequestSpring
    }
}
