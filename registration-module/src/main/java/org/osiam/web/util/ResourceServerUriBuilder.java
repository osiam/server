package org.osiam.web.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class to build the uri for the resource server.
 * User: Jochen Todea
 * Date: 25.11.13
 * Time: 11:46
 * Created: with Intellij IDEA
 */
@Component
public class ResourceServerUriBuilder {

    @Value("${osiam.server.port}")
    private int serverPort;
    @Value("${osiam.server.host}")
    private String serverHost;
    @Value("${osiam.server.http.scheme}")
    private String httpScheme;


    /**
     * Method to build the resource server uri and appending the user id if it is not null or empty.
     * @param userId the user id to be appended to the resource server uri
     * @return the resource server uri including the user id
     */
    public String buildUsersUriWithUserId(String userId){
        StringBuilder stringBuilder = buildResourceServerBaseUri();
        stringBuilder.append("/Users/")
                .append(userId != null ? userId : "");

        return stringBuilder.toString();
    }

    /**
     * Method to build the resource server uri for /me endpoint
     * @return the resource server uri
     */
    public String buildMeEndpointUri() {
        StringBuilder stringBuilder = buildResourceServerBaseUri();
        stringBuilder.append("/me");
        return stringBuilder.toString();
    }

    private StringBuilder buildResourceServerBaseUri() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(httpScheme)
                .append("://")
                .append(serverHost)
                .append(":")
                .append(serverPort)
                .append("/osiam-resource-server");

        return stringBuilder;
    }
}
