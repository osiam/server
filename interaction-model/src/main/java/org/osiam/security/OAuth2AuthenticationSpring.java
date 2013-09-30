package org.osiam.security;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author: Andreas Grau, tarent solutions GmbH, 30.09.13
 * @version: 1.0
 */
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
