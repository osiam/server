package org.osiam.web.util;

import org.osiam.helper.HttpClientHelper;
import org.osiam.helper.HttpClientRequestResult;
import org.osiam.helper.ObjectMapperWithExtensionConfig;
import org.osiam.web.resource.MeUserRepresentation;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

/**
 * This class using the /me endpoint to get information about the provided access token
 * User: Jochen Todea
 * Date: 26.11.13
 * Time: 14:34
 * Created: with Intellij IDEA
 */
@Component
public class AccessTokenInformationProvider {

    @Inject
    private ResourceServerUriBuilder resourceServerUriBuilder;
    @Inject
    private HttpClientHelper httpClientHelper;
    @Inject
    private ObjectMapperWithExtensionConfig mapper;

    /**
     * Calling resource server /me endpoint for access token information.
     * @param token the valid access token
     * @return the user id from token information
     */
    public String getUserIdFromToken(String token) throws IOException {
        //get the resource server uri
        String uri = resourceServerUriBuilder.buildMeEndpointUri();

        //calling the /me endpoint
        HttpClientRequestResult result = httpClientHelper.executeHttpGet(uri, HttpHeader.AUTHORIZATION, token);

        //Check if the result has an error, possible that access token is invalid
        if (result.getBody().contains("error")) {
            throw new IllegalArgumentException(result.getBody());
        }

        //serialize the json response to a class
        MeUserRepresentation meUserRepresentation = mapper.readValue(result.getBody(), MeUserRepresentation.class);

        return meUserRepresentation.getId();
    }
}