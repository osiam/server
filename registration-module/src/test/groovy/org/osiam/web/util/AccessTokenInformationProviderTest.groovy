package org.osiam.web.util

import spock.lang.Specification

/**
 * Test for the access token information provider class.
 * User: Jochen Todea
 * Date: 26.11.13
 * Time: 14:42
 * Created: with Intellij IDEA
 */
class AccessTokenInformationProviderTest extends Specification {

    def accessTokenInformationProvider = new AccessTokenInformationProvider()

    def "the user id should be provided by retrieving information from the access token"() {
        given:
        def accessToken = "theToken"

        when:
        def userId = accessTokenInformationProvider.getUserIdFromToken(accessToken)

        then:
        userId == null
    }
}