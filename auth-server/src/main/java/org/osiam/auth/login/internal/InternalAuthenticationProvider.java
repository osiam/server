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

package org.osiam.auth.login.internal;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.osiam.auth.login.ResourceServerConnector;
import org.osiam.resources.scim.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class InternalAuthenticationProvider implements AuthenticationProvider {

    @Inject
    private ResourceServerConnector resourceServerConnector;

    @Inject
    private ShaPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        Preconditions.checkArgument(authentication instanceof InternalAuthentication,
                "InternalAuthenticationProvider only supports InternalAuthentication.");

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        if (Strings.isNullOrEmpty(username)) {
            throw new BadCredentialsException("InternalAuthenticationProvider: Empty Username");
        }

        if (Strings.isNullOrEmpty(password)) {
            throw new BadCredentialsException("InternalAuthenticationProvider: Empty Password");
        }

        // Determine username
        User user = resourceServerConnector.getUserByUsername(username);

        if (user == null) {
            throw new BadCredentialsException("The user with the username '" + username + "' not exists!");
        }

        String hashedPassword = passwordEncoder.encodePassword(password, user.getId());

        if (resourceServerConnector.searchUserByUserNameAndPassword(username, hashedPassword) == null) {
            throw new BadCredentialsException("Bad credentials");
        }

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        User authUser = new User.Builder(username).setId(user.getId()).build();

        return new InternalAuthentication(authUser, password, grantedAuths);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return InternalAuthentication.class.isAssignableFrom(authentication);
    }

}
