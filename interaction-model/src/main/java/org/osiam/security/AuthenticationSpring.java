package org.osiam.security;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.osiam.resources.RoleSpring;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * @author: Andreas Grau, tarent solutions GmbH, 30.09.13
 * @version: 1.0
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class AuthenticationSpring implements Authentication {

    private static final long serialVersionUID = 8575148481619626424L;

    private Collection<? extends RoleSpring> authorities;
    private Object credentials;
    private Object details;
    private Object principal;
    private boolean authenticated;
    private String name;

    @Override
    public Collection<? extends RoleSpring> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setAuthorities(Collection<? extends RoleSpring> authorities) {
        this.authorities = authorities;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    public void setName(String name) {
        this.name = name;
    }
}
