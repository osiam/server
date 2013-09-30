package org.osiam.security;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.osiam.resources.RoleSpring;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.AuthorizationRequest;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author: Andreas Grau, tarent solutions GmbH, 30.09.13
 * @version: 1.0
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class AuthorizationRequestSpring implements AuthorizationRequest {

    private Map<String, String> authorizationParameters;
    private Map<String, String> approvalParameters;
    private String clientId;
    private Set<String> scope;
    private Set<String> resourceIds;
    private Collection<? extends RoleSpring> authorities;
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
    //TODO: Check Jackson serialization
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


}
