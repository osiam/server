package org.osiam.ng.scim.exceptions

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 22.03.13
 * Time: 09:21
 * To change this template use File | Settings | File Templates.
 */
class ResourceExistsExceptionSpec extends Specification {

    def "should contain given message"(){
        when:
        def result = new ResourceExistsException("danger")
        then:
        result.message == "danger"
    }
}
