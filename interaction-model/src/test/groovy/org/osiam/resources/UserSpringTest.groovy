package org.osiam.resources

import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification

/**
 *
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
        then:
        userSpring instanceof UserDetails
        userSpring.getAuthorities() == roles
        //not correctly set yet
        userSpring.isAccountNonExpired()
        userSpring.isAccountNonLocked()
        userSpring.isCredentialsNonExpired()
        userSpring.isEnabled()
    }
}
