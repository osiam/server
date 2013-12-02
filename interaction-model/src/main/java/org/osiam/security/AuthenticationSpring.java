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
import org.springframework.security.core.Authentication;

import java.util.Collection;

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
    public void setAuthenticated(boolean isAuthenticated) {
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
