package org.osiam.security;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class OAuth2AuthenticationSpring {

    private AuthenticationSpring authenticationSpring;

    private AuthorizationRequestSpring authorizationRequestSpring;

    public AuthenticationSpring getAuthenticationSpring() {
        return authenticationSpring;
    }

    public void setAuthenticationSpring(AuthenticationSpring authenticationSpring) {
        this.authenticationSpring = authenticationSpring;
    }

    public AuthorizationRequestSpring getAuthorizationRequestSpring() {
        return authorizationRequestSpring;
    }

    public void setAuthorizationRequestSpring(AuthorizationRequestSpring authorizationRequestSpring) {
        this.authorizationRequestSpring = authorizationRequestSpring;
    }
}
