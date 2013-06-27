package org.osiam.spring;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.endpoint.AbstractEndpoint;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
/**
 * This is the endpoint for facebook connect. Instead of json Facebook uses key&value pairs:
 * "access_token=xxxx&expires=0000"
 * However the normal controller does deliver the access_token in json so we needed to create a second Controller for
 * that use case.
 *
 */
public class FbTokenEndpoint {

    private WebResponseExceptionTranslator providerExceptionHandler = new DefaultWebResponseExceptionTranslator();
    @Inject
    private TokenGranter tokenGranter;
    @Inject
    private ClientDetailsService clientDetailsService;
    @Inject
    private AuthorizationRequestManager authorizationRequestManager;
    @Inject
    private AuthorizationRequestManager defaultAuthorizationRequestManager;
    private TokenEndpoint tokenEndpoint = new TokenEndpoint();

    @RequestMapping(value = "/fb/oauth/access_token")
    @ResponseBody
    public String access_token(Principal principal,
                               @RequestParam(value = "grant_type", defaultValue = "authorization_code")
                               String grantType, @RequestParam Map<String, String> parameters) {


        tokenEndpoint.setAuthorizationRequestManager(authorizationRequestManager);
        tokenEndpoint.setClientDetailsService(clientDetailsService);
        tokenEndpoint.setProviderExceptionHandler(providerExceptionHandler);
        tokenEndpoint.setTokenGranter(tokenGranter);

        ResponseEntity<OAuth2AccessToken> accessToken = tokenEndpoint.getAccessToken(principal, grantType, parameters);
        return "access_token=" + accessToken.getBody().getValue() + "&expires=" + accessToken.getBody().getExpiresIn();

    }


}
