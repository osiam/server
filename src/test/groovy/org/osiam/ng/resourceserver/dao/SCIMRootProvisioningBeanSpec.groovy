package org.osiam.ng.resourceserver.dao

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 13.05.13
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
class SCIMRootProvisioningBeanSpec extends Specification {

    def scimUserProvisioningBean = Mock(SCIMUserProvisioningBean)
    def scimGroupProvisioningBean = Mock(SCIMGroupProvisioningBean)
    def scimRootProvisioningBean = new SCIMRootProvisioningBean(scimUserProvisioningBean: scimUserProvisioningBean, scimGroupProvisioningBean: scimGroupProvisioningBean)

    def "should call dao search on search"() {
        when:
        scimRootProvisioningBean.search("anyFilter", "userName", "ascending", 100, 1)

        then:
        1 * scimUserProvisioningBean.search("anyFilter", "userName", "ascending", 50, 1) >> []
        1 * scimGroupProvisioningBean.search("anyFilter", "userName", "ascending", 50, 1) >> []
    }

    def "should ignore SearchException on UserDAO"() {
        when:
        scimRootProvisioningBean.search("anyFilter", "userName", "ascending", 100, 1)

        then:
        1 * scimUserProvisioningBean.search("anyFilter", "userName", "ascending", 50, 1) >> { throw new Exception("moep") }
        1 * scimGroupProvisioningBean.search("anyFilter", "userName", "ascending", 50, 1) >> []
    }

    def "should ignore SearchException on GroupDAO"() {
        when:
        scimRootProvisioningBean.search("anyFilter", "userName", "ascending", 100, 1)

        then:
        1 * scimUserProvisioningBean.search("anyFilter", "userName", "ascending", 50, 1) >> []
        1 * scimGroupProvisioningBean.search("anyFilter", "userName", "ascending", 50, 1) >> { throw new Exception("moep") }
    }
}
