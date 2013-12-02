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

package org.osiam.security;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.osiam.resources.RoleSpring;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.AuthorizationRequest;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class AuthorizationRequestSpring implements AuthorizationRequest {

    private Map<String, String> authorizationParameters;
    private Map<String, String> approvalParameters;
    private String clientId;
    private Set<String> scope;
    private Set<String> resourceIds;
    private Collection<? super RoleSpring> authorities;
    private boolean approved;
    private boolean denied;
    private String state;
    private String redirectUri;
    private Set<String> responseTypes;

    @Override
    public Map<String, String> getAuthorizationParameters() {
        return authorizationParameters;
    }

    @Override
    public Map<String, String> getApprovalParameters() {
        return approvalParameters;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    @Override
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return (Collection<GrantedAuthority>) authorities;
    }

    @Override
    public boolean isApproved() {
        return approved;
    }

    @Override
    public boolean isDenied() {
        return denied;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public String getRedirectUri() {
        return redirectUri;
    }

    @Override
    public Set<String> getResponseTypes() {
        return responseTypes;
    }

    public void setAuthorizationParameters(Map<String, String> authorizationParameters) {
        this.authorizationParameters = authorizationParameters;
    }

    public void setApprovalParameters(Map<String, String> approvalParameters) {
        this.approvalParameters = approvalParameters;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public void setResourceIds(Set<String> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public void setAuthorities(Collection<? super RoleSpring> authorities) {
        this.authorities = authorities;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public void setDenied(boolean denied) {
        this.denied = denied;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setResponseTypes(Set<String> responseTypes) {
        this.responseTypes = responseTypes;
    }
}