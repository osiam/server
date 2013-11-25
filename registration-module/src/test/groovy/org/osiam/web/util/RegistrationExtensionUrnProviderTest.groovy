package org.osiam.web.util

import spock.lang.Specification

/**
 * Registration extension urn provider test..
 * User: Jochen Todea
 * Date: 25.11.13
 * Time: 12:40
 * Created: with Intellij IDEA
 */
class RegistrationExtensionUrnProviderTest extends Specification {

    def regExtUrn = "some:unr:stuff"

    RegistrationExtensionUrnProvider registrationExtensionUrnProvider =
        new RegistrationExtensionUrnProvider(internalScimExtensionUrn: regExtUrn)

    def "should return the extension urn for registration purpose"() {
        when:
        def urn = registrationExtensionUrnProvider.getExtensionUrn()

        then:
        urn == regExtUrn
    }
}
