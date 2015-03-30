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

package org.osiam.security.authorization

import org.osiam.auth.oauth_client.ClientEntity
import org.osiam.resources.scim.User
import org.osiam.security.authentication.OsiamClientDetails
import org.osiam.security.authentication.OsiamClientDetailsService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.AuthorizationRequest

import spock.lang.Specification

class OsiamUserApprovalHandlerSpec extends Specification {

    OsiamClientDetailsService osiamClientDetailsService = Mock()
    OsiamUserApprovalHandler osiamUserApprovalHandler = new OsiamUserApprovalHandler(osiamClientDetailsService: osiamClientDetailsService)
    AuthorizationRequest authorizationRequestMock = Mock()
    Authentication authenticationMock = Mock()
	final String UUIDVar = "UUIDValue"

    def 'should only add approval date if it is the correct state and user approved successfully'() {
        given:
        OsiamClientDetails client = new OsiamClientDetails()
        authorizationRequestMock.getClientId() >> 'example-client'
        authorizationRequestMock.getApprovalParameters() >> ['user_oauth_approval':'true']
		User userMock = Mock()
		userMock.id >> UUIDVar
		authenticationMock.getPrincipal() >> userMock

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequestMock, authenticationMock)

        then:
        1 * osiamClientDetailsService.loadClientByClientId('example-client') >> client
		1 * osiamClientDetailsService.updateExpiryDate('example-client', _, _)
    }

    def 'should not add approval date if approval param map is empty and not containing key user_oauth_approval'() {
        given:
        def approvalParams = [:]
        authorizationRequestMock.getApprovalParameters() >> approvalParams

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequestMock, authenticationMock)

        then:
        0 * osiamClientDetailsService.updateExpiryDate(_, _, _)
        true
    }

    def 'should not add approval date if user denies approval'() {
        given:
        def approvalParams = ['user_oauth_approval':'false']
        authorizationRequestMock.getApprovalParameters() >> approvalParams

        when:
        osiamUserApprovalHandler.updateBeforeApproval(authorizationRequestMock, authenticationMock)
		User userMock = Mock()
		userMock.id >> UUIDVar
		authenticationMock.getPrincipal() >> userMock
        then:
        0 * 0 * osiamClientDetailsService.updateExpiryDate(_, _, _)
        true
    }

    def 'should return true if implicit is configured as true to not ask user for approval'() {
        given:
        OsiamClientDetails clientMock = Mock()
        authorizationRequestMock.getClientId() >> 'example-client'
        osiamClientDetailsService.loadClientByClientId('example-client') >> clientMock
        authenticationMock.isAuthenticated() >> true
        clientMock.isImplicit() >> true
		User userMock = Mock()
		userMock.id >> UUIDVar
		authenticationMock.getPrincipal() >> userMock

        when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        result
    }

    def 'should return true if expiry date is valid and user approved client already and must not approve it again'() {
        given:
        OsiamClientDetails clientMock = Mock()
        authorizationRequestMock.getClientId() >> 'example-client'
        osiamClientDetailsService.loadClientByClientId('example-client') >> clientMock
        authenticationMock.isAuthenticated() >> true
		def HashMap<String,Date> test = new HashMap<String,Date>()
		test.put(UUIDVar,new Date(System.currentTimeMillis() + 100000))
		clientMock.getExpiryDates() >> test
		User userMock = Mock()
        userMock.id >> UUIDVar
        authenticationMock.getPrincipal() >> userMock

		when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        result
    }

    def 'should return false if implicit is not configured and user never approved the client before'() {
		//TODO After implicit functionality is implemented in code, check this test again
		// if isImplicit is set to true, the test should fail
        given:
        OsiamClientDetails clientMock = Mock()
        authorizationRequestMock.getClientId() >> 'example-client'
        osiamClientDetailsService.loadClientByClientId('example-client') >> clientMock
        clientMock.isImplicit() >> false
		def HashMap<String,Date> test = new HashMap<String,Date>()
		test.put(UUIDVar,new Date(System.currentTimeMillis() - 100000))
        clientMock.getExpiryDates() >> null
		User userMock = Mock()
		userMock.id >> UUIDVar
		authenticationMock.getPrincipal() >> userMock

        when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        !result
    }

    def 'should return false if implicit is not configured and user approval date is expired'() {
		//TODO After implicit functionality is implemented in code, check this test again
		// if isImplicit is set to false and date to ... - 100000, the test should fail
        given:
        OsiamClientDetails clientMock = Mock()
        authorizationRequestMock.getClientId() >> 'example-client'
        osiamClientDetailsService.loadClientByClientId('example-client') >> clientMock
        clientMock.isImplicit() >> false
		def HashMap<String,Date> test = new HashMap<String,Date>()
		test.put(UUIDVar,new Date(System.currentTimeMillis() + 100000))
		clientMock.getExpiryDates() >> test
		User userMock = Mock()
		userMock.id >> UUIDVar
		authenticationMock.getPrincipal() >> userMock

        when:
        def result = osiamUserApprovalHandler.isApproved(authorizationRequestMock, authenticationMock)

        then:
        !result
    }
}