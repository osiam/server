package org.osiam.web.util;

import org.springframework.beans.factory.annotation.Value;

/**
 * Class to provide the registration extension urn.
 * User: Jochen Todea
 * Date: 25.11.13
 * Time: 12:33
 * Created: with Intellij IDEA
 */
public class RegistrationExtensionUrnProvider {

    @Value("${osiam.internal.scim.extension.urn}")
    private String internalScimExtensionUrn;

    /**
     * Returns the registration extension urn as String.
     * @return the registration extension urn
     */
    public String getExtensionUrn() {
        return internalScimExtensionUrn;
    }
}
