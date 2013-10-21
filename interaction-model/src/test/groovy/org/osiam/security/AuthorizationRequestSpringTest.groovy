package org.osiam.security

import org.osiam.resources.RoleSpring
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jochen
 * Date: 14.10.13
 * Time: 13:01
 * To change this template use File | Settings | File Templates.
 */
class AuthorizationRequestSpringTest extends Specification {

    def authorizationRequestSpring = new AuthorizationRequestSpring()

    def "should inherit AuthorizationRequest"(){
        given:
        def roles = [new RoleSpring()] as Collection

        when:
        authorizationRequestSpring.setAuthorities(roles)
        authorizationRequestSpring.setApprovalParameters(["key":"value"])
        authorizationRequestSpring.setApproved(true)
        authorizationRequestSpring.setAuthorizationParameters(["key":"value"])
        authorizationRequestSpring.setClientId("ClientId")
        authorizationRequestSpring.setDenied(false)
        authorizationRequestSpring.setRedirectUri("redirectURI")
        authorizationRequestSpring.setResourceIds(["resourceIds"] as Set)
        authorizationRequestSpring.setResponseTypes(["responseTypes"] as Set)
        authorizationRequestSpring.setScope(["scopes"] as Set)
        authorizationRequestSpring.setState("state")

        then:
        authorizationRequestSpring.getAuthorities() == roles
        authorizationRequestSpring.getApprovalParameters() == ["key":"value"]
        authorizationRequestSpring.isApproved()
        authorizationRequestSpring.getAuthorizationParameters() == ["key":"value"]
        authorizationRequestSpring.getClientId() == "ClientId"
        !authorizationRequestSpring.isDenied()
        authorizationRequestSpring.getRedirectUri() == "redirectURI"
        authorizationRequestSpring.getResourceIds() == ["resourceIds"] as Set
        authorizationRequestSpring.getResponseTypes() == ["responseTypes"] as Set
        authorizationRequestSpring.getScope() == ["scopes"] as Set
        authorizationRequestSpring.getState() == "state"
    }
}