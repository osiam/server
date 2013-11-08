package org.osiam.resources

import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification

/**
 * Unit-Tests for {@link UserSpring}.
 *
 * @author: Andreas Grau, tarent solutions GmbH, 27.09.13
 * @version: 1.0
 */
class UserSpringTest extends Specification {

    def userSpring = new UserSpring()

    def "should inherit UserDetails"() {
        given:
        def roles = [new RoleSpring()] as Set

        when:
        userSpring.setUserName("username")
        userSpring.setRoles(roles)
        userSpring.setPassword("thePassword")
        userSpring.setActive(true)

        then:
        userSpring instanceof UserDetails
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

    def "should be able to set users userName"() {
        when:
        userSpring.setUserName("userName")

        then:
        userSpring.getUserName() == "userName"
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
        userSpring.getActive()
    }
}