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

import org.joda.time.MutableDateTime
import spock.lang.Specification

/**
 * Unit-Tests for {@link ClientSpring}.
 *
 * @author: Andreas Grau, tarent solutions GmbH, 27.09.13
 * @version: 1.0
 */
class ClientSpringTest extends Specification {

    def clientSpring = new ClientSpring()

    def "resource ids should be empty"() {
        when:
        def ids = clientSpring.getResourceIds()
        then:
        ids.empty
    }


    def "isSecretRequired should be true"() {
        when:
        def result = clientSpring.isSecretRequired()
        then:
        result
    }

    def "isScoped should be true"() {
        when:
        def result = clientSpring.isScoped()
        then:
        result
    }

    def "getRegisteredRedirectUri should return a set which contains redirect_uri"() {
        given:
        clientSpring.setRedirectUri("should_i_stay_or_should_i_go_now")
        when:
        def result = clientSpring.getRegisteredRedirectUri()
        then:
        result == [clientSpring.getRedirectUri()] as Set
    }

    def "getAuthorities should be empty"() {
        when:
        def result = clientSpring.getAuthorities()
        then:
        result.empty
    }


    def "getAdditionalInformation should be empty"() {
        when:
        def result = clientSpring.getAdditionalInformation()
        then:
        !result
    }

    def "getClientId should work"() {
        given: "a client id"
        def clientId = "client-id"

        when:
        clientSpring.setId(clientId)

        then:
        clientSpring.getClientId() == clientId
    }

    def "getId should work"() {
        given: "a client id"
        def clientId = "client-id"

        when:
        clientSpring.setId(clientId)

        then:
        clientSpring.getId() == clientId
    }

    def "getClientSecret should work"() {
        given: "a client secret"
        def clientSecret = "client-secret"

        when:
        clientSpring.setClientSecret(clientSecret)

        then:
        clientSpring.getClientSecret() == clientSecret
    }

    def "getScope should work"() {
        given: "a Set of scopes"
        def clientScope = ["scope1", "scope2"] as Set

        when:
        clientSpring.setScope(clientScope)

        then:
        clientSpring.getScope() == clientScope
    }

    def "getAuthorizedGrantTypes should work"() {
        given: "a Set of grant types"
        def grantTypes = ["grantType1", "grantType2"] as Set

        when:
        clientSpring.setGrants(grantTypes)

        then:
        clientSpring.getAuthorizedGrantTypes() == grantTypes
    }

    def "getAccessTokenValiditySeconds should work"() {
        given: "access token validity seconds"
        def validitySeconds = 60

        when:
        clientSpring.setAccessTokenValiditySeconds(validitySeconds)

        then:
        clientSpring.getAccessTokenValiditySeconds() == validitySeconds
    }

    def "getRefreshTokenValiditySeconds should work"() {
        given: "refresh token validity seconds"
        def validitySeconds = 99

        when:
        clientSpring.setRefreshTokenValiditySeconds(validitySeconds)

        then:
        clientSpring.getRefreshTokenValiditySeconds() == validitySeconds
    }

    def "getGrants should work"() {
        given: "a Set of grant types"
        def grantTypes = ["grantType1", "grantType2"] as Set

        when:
        clientSpring.setGrants(grantTypes)

        then:
        clientSpring.getGrants() == grantTypes
    }

    def "getExpiry should work"() {
        given: "an expiry Date"
        def expiryDate = MutableDateTime.now().toDate()

        when:
        clientSpring.setExpiry(expiryDate)

        then:
        clientSpring.getExpiry() == expiryDate
    }

    def "isImplicit should work"() {
        given: "an expiry Date"
        def implicit = true as boolean

        when:
        clientSpring.setImplicit(implicit)

        then:
        clientSpring.isImplicit() == implicit
    }

    def "getValidityInSeconds should work"() {
        given: "validity seconds"
        def validitySeconds = 333

        when:
        clientSpring.setValidityInSeconds(validitySeconds)

        then:
        clientSpring.getValidityInSeconds() == validitySeconds
    }

    def "should not throw null pointer exception if expiry is null"() {
        given:
        clientSpring.setExpiry(null)

        when:
        def result = clientSpring.getExpiry()

        then:
        notThrown(NullPointerException)
        result == null
    }

}
