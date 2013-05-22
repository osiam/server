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

package org.osiam.oauth2.client.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * This class is used by clientAuthenticationManager in Spring to authenticate a client when trading an auth_code to an
 * access_token.
 */
@Component("clientDetails")
public class ClientDetailsLoadingBean implements ClientDetailsService {

    @Override
    public ClientDetails loadClientByClientId(final String clientId) {
        //TODO implement DAO to get real client ...
        return new ClientDetails() {

            private static final long serialVersionUID = -3542902414837379208L;

            @Override
            public String getClientId() {
                return clientId;
            }

            @Override
            public Set<String> getResourceIds() {
                //                resources.add("oauth2res");
                return new HashSet<>();
            }

            @Override
            public boolean isSecretRequired() {
                return true;
            }

            @Override
            public String getClientSecret() {
                return "secret";
            }

            @Override
            public boolean isScoped() {
                return true;
            }

            @Override
            public Set<String> getScope() {
                Set<String> scopes = new HashSet<>();
                scopes.add("GET");
                scopes.add("PUT");
                scopes.add("POST");
                scopes.add("PATCH");
                scopes.add("DELETE");
                return scopes;
            }

            @Override
            public Set<String> getAuthorizedGrantTypes() {
                Set<String> grants = new HashSet<>();
                grants.add("authorization_code");
                grants.add("implicit");
                grants.add("refresh-token");
                return grants;
            }

            @Override
            public Set<String> getRegisteredRedirectUri() {

                return new HashSet<>(Arrays.asList(
                        "http://localhost:8080/oauth2-client/accessToken",
                        "http://ong01-devel00.lan.tarent.de:8080/oauth2-client/accessToken",
                        "http://ong01-devel01.lan.tarent.de:8080/oauth2-client/accessToken",
                        "http://ong01-devel02.lan.tarent.de:8080/oauth2-client/accessToken",
                        "http://ong01-devel03.lan.tarent.de:8080/oauth2-client/accessToken",
                        "http://ong01-devel04.lan.tarent.de:8080/oauth2-client/accessToken",
                        "http://ong01-devel05.lan.tarent.de:8080/oauth2-client/accessToken",
                        "http://ong01-systemtest.lan.tarent.de:5000/oauth2",
                        "http://ong01-devel10.lan.tarent.de:5000/oauth2",
                        "http://ong01-devel00.lan.tarent.de:5000/oauth2",
                        "http://ong01-devel11.lan.tarent.de:5000/oauth2",
                        "http://ong01-devel12.lan.tarent.de:5000/oauth2",
                        "http://ong01-devel13.lan.tarent.de:5000/oauth2",
                        "http://ong01-devel14.lan.tarent.de:5000/oauth2",
                        "http://ong01-systemtest.lan.tarent.de:8080/oauth2-client/accessToken",
                        "http://ong00-systemtest:8080/oauth2-client/accessToken",
                        "http://localhost:5000/oauth2",
                        "http://ong01-exclient.lan.tarent.de:5000/oauth2",
                        "http://127.0.0.1:5000/oauth2"));
            }

            @Override
            public Collection<GrantedAuthority> getAuthorities() {
                return new ArrayList<>();

            }

            @Override
            public Integer getAccessTokenValiditySeconds() {
                return 1337;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Integer getRefreshTokenValiditySeconds() {
                return 1337;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Map<String, Object> getAdditionalInformation() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }


}
