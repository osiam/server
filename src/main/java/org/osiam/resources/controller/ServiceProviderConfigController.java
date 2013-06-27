package org.osiam.resources.controller;


import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.osiam.resources.scim.Constants;

import java.util.Set;

@Controller
@RequestMapping(value = "/ServiceProviderConfig")
public class ServiceProviderConfigController {
    @RequestMapping
    @ResponseBody
    public ServiceProviderConfig getConfig() {
        return ServiceProviderConfig.instance;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    public static class ServiceProviderConfig {
        public static final ServiceProviderConfig instance = new ServiceProviderConfig();
        public  Set<String> schemas = Constants.CORE_SCHEMAS;
        public final Supported patch = new Supported(true);
        public final Supported bulk = new BulkSupported(false);
        public final Supported filter = new FilterSupported(true, Constants.MAX_RESULT);
        public final Supported changePassword = new Supported(false);
        public final Supported sort = new Supported(true);
        public final Supported etag = new Supported(false);
        public final Supported xmlDataFormat = new Supported(false);
        public final AuthenticationSchemes authenticationSchemes = new AuthenticationSchemes(
                new AuthenticationSchemes.AuthenticationScheme("Oauth2 Bearer",
                        "OAuth2 Bearer access token is used for authorization.", "http://tools.ietf.org/html/rfc6749",
                        "http://oauth.net/2/"));

        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        public static class Supported {
            public final boolean supported;

            public Supported(boolean b) {
                supported = b;
            }
        }

        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        public static class FilterSupported extends Supported{
            public final Integer maxResults;

            public FilterSupported(boolean b, Integer maxresults) {
                super(b);
                this.maxResults = maxresults;
            }
        }


        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        public static class BulkSupported extends Supported {
            public final Integer maxOperations;
            public final Integer maxPayloadSize;

            public BulkSupported(boolean b) {
                super(b);
                this.maxOperations = null;
                this.maxPayloadSize = null;
            }

        }

        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        static class AuthenticationSchemes {
            public AuthenticationScheme[] authenticationSchemes;

            public AuthenticationSchemes(AuthenticationScheme... authenticationScheme) {
                this.authenticationSchemes = authenticationScheme;

            }

            @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
            public static class AuthenticationScheme {
                public final String name;
                public final String description;
                public final String specUrl;
                public final String documentationUrl;

                AuthenticationScheme(String name, String description, String specUrl, String documentationUrl) {
                    this.name = name;
                    this.description = description;
                    this.specUrl = specUrl;
                    this.documentationUrl = documentationUrl;
                }
            }
        }
    }
}
