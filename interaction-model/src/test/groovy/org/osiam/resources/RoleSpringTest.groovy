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

import org.springframework.security.core.GrantedAuthority
import spock.lang.Specification

/**
 * Unit-Tests for {@link RoleSpring}.
 *
 */
class RoleSpringTest extends Specification {
    def roleSpring = new RoleSpring()

    def "a role should implemented grantedAuthority from Spring for authorization purposes"() {
        given:
        roleSpring.setValue("USER")
        when:
        def authority = roleSpring.authority;
        then:
        authority == "ROLE_USER"
        roleSpring instanceof GrantedAuthority
    }

    def "should be able to get the value field"() {
        given:
        roleSpring.setValue("USER")

        when:
        def value = roleSpring.getValue()

        then:
        value == "USER"
    }
}