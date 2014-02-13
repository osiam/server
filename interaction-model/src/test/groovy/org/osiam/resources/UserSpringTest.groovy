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

package org.osiam.resources

import spock.lang.Specification

/**
 * Unit-Tests for {@link UserSpring}.
 *
 */
class UserSpringTest extends Specification {

    def userSpring = new UserSpring()

    def "should inherit UserDetails"() {
        given:
        def roles = [new RoleSpring()] as Set

        when:
        userSpring.setUsername("username")
        userSpring.setRoles(roles)
        userSpring.setPassword("thePassword")
        userSpring.setActive(true)

        then:
        userSpring.getAuthorities() == roles
        userSpring.getPassword() == "thePassword"
        userSpring.getUsername() == "username"
        userSpring.isAccountNonExpired()
        userSpring.isAccountNonLocked()
        userSpring.isCredentialsNonExpired()
        userSpring.isEnabled()
    }

    def "should be able to set users roles"() {
        given:
        def roles = [new RoleSpring()] as Set

        when:
        userSpring.setRoles(roles)

        then:
        userSpring.getRoles() == roles
    }

    def "should be able to set users id"() {
        when:
        userSpring.setId("someUUID")

        then:
        userSpring.getId() == "someUUID"
    }

    def "should be able to set users active flag"() {
        when:
        userSpring.setActive(true)

        then:
        userSpring.isActive()
    }
}