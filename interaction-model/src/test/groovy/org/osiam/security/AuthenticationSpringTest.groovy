package org.osiam.security

import org.osiam.resources.RoleSpring
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jochen
 * Date: 14.10.13
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
class AuthenticationSpringTest extends Specification {

    def authenticationSpring = new AuthenticationSpring()

    def "should inherit springs Authentication"() {
        given:
        def roles = [new RoleSpring()] as Set

        when:
        authenticationSpring.setAuthenticated(true)
        authenticationSpring.setAuthorities(roles)
        authenticationSpring.setCredentials("theCredentials")
        authenticationSpring.setDetails("theDetails")
        authenticationSpring.setName("name")
        authenticationSpring.setPrincipal("thePrincipal")

        then:
        authenticationSpring.isAuthenticated()
        authenticationSpring.getAuthorities() == roles
        authenticationSpring.getCredentials() == "theCredentials"
        authenticationSpring.getDetails() == "theDetails"
        authenticationSpring.getName() == "name"
        authenticationSpring.getPrincipal() == "thePrincipal"
    }
}
