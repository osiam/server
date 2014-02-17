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

package org.osiam.security

import org.osiam.resources.RoleSpring
import spock.lang.Specification

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