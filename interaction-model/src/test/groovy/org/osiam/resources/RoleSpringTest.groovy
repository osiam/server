package org.osiam.resources

import org.springframework.security.core.GrantedAuthority
import spock.lang.Specification

/**
 *
 *
 * @author: Andreas Grau, tarent solutions GmbH, 27.09.13
 * @version: 1.0
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
}
