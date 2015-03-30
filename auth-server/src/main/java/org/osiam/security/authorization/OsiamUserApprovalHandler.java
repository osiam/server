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

package org.osiam.security.authorization;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.osiam.resources.scim.User;
import org.osiam.security.authentication.OsiamClientDetails;
import org.osiam.security.authentication.OsiamClientDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;

/**
 * Osiam user approval handler extends the default user approval handler from spring. It will add an implicit approval
 * if configured in the client, so that the user is never asked to approve the client. Additionally an expiry period can
 * be configured if the implicit is not desired and the user need to approve once. After that he will be asked again
 * only if the period expires
 */
@Named("userApprovalHandler")
public class OsiamUserApprovalHandler extends DefaultUserApprovalHandler {

    private static final int SECONDS = 1000;

    @Inject
    private OsiamClientDetailsService osiamClientDetailsService;

    /**
     * Is called if OsiamUserApprovalHandler.isApproved() returns false and AccessConfirmation is done by the user. Than
     * it will save the approve date to be able to check it as long as user accepts approval. So the user is not
     * bothered every time to approve the client.
     *
     * @param authorizationRequest
     *            spring authorizationRequest
     * @param userAuthentication
     *            spring userAuthentication
     * @return the authorizationRequest
     */
    @Override
    public AuthorizationRequest updateBeforeApproval(final AuthorizationRequest authorizationRequest,
            final Authentication userAuthentication) {
        // check if "user_oauth_approval" is in the authorizationRequests approvalParameters and the (size != 0)
        // updates user expiry date only when authorization made by user
        if (authorizationRequest.getApprovalParameters().containsKey("user_oauth_approval")
                && authorizationRequest.getApprovalParameters().get("user_oauth_approval").equals("true")) {

            final OsiamClientDetails client = getClientDetails(authorizationRequest);
            final String UUID = ((User)userAuthentication.getPrincipal()).getId();
            final Date date = new Date(System.currentTimeMillis() + client.getValidityInSeconds() * SECONDS);
            osiamClientDetailsService.updateExpiryDate(authorizationRequest.getClientId(), UUID, date);
        }
        return super.updateBeforeApproval(authorizationRequest, userAuthentication);
    }

    /**
     * Checks if the client is configured to not ask the user for approval or if the date to ask again expires.
     *
     * @param authorizationRequest
     *            spring authorizationRequest
     * @param userAuthentication
     *            spring userAuthentication
     * @return whether user approved the client or not
     */
    @Override
    public boolean isApproved(final AuthorizationRequest authorizationRequest, final Authentication userAuthentication) {
    	// asks for authorization after authentication if:
		// client is not implicit, user is new or session expiry date of user already reached.
        final OsiamClientDetails client = getClientDetails(authorizationRequest);
        final String UUID = ((User)userAuthentication.getPrincipal()).getId();

        if (userAuthentication.isAuthenticated() && client.isImplicit()) {
        	return true;
        } else if (userAuthentication.isAuthenticated()
				&& client.getExpiryDates().get(UUID) != null
				&& client.getExpiryDates().get(UUID).compareTo(new Date()) >= 0) {
        	return true;
        }
        return false;
    }

    private OsiamClientDetails getClientDetails(final AuthorizationRequest authorizationRequest) {
        return osiamClientDetailsService.loadClientByClientId(authorizationRequest.getClientId());
    }

}