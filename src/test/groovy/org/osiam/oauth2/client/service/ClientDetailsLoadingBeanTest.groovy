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

package org.osiam.oauth2.client.service

import spock.lang.Specification

class ClientDetailsLoadingBeanTest extends Specification {
    def underTest = new ClientDetailsLoadingBean()

    def "should return one fake client"(){
        when:
        def result = underTest.loadClientByClientId("client!")
        then:
        result.clientId == "client!"
        result.isScoped()
        result.isSecretRequired()
        result.getAccessTokenValiditySeconds() == 1337
        result.getRefreshTokenValiditySeconds() == 1337

        result.getScope().contains("GET")
        result.getScope().contains("PUT")
        result.getScope().contains("PUT")
        result.getScope().contains("DELETE")
        result.getResourceIds().size() == 0
        result.getAuthorizedGrantTypes().size() == 3
        result.getRegisteredRedirectUri().size() > 1
        !result.getAuthorities()
        !result.getAdditionalInformation()
        result.getClientSecret() == "secret"


    }
}
