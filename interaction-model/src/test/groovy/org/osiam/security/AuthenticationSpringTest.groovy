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
