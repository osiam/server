package org.osiam.web.util

import org.osiam.helper.HttpClientHelper
import org.osiam.helper.HttpClientRequestResult
import org.osiam.helper.ObjectMapperWithExtensionConfig
import org.osiam.web.resource.MeUserRepresentation
import spock.lang.Specification

/**
 * Test for the access token information provider class.
 * User: Jochen Todea
 * Date: 26.11.13
 * Time: 14:42
 * Created: with Intellij IDEA
 */
class AccessTokenInformationProviderTest extends Specification {

    def resourceServerUriBuilder = Mock(ResourceServerUriBuilder)
    def httpClientHelper = Mock(HttpClientHelper)
    def mapper = Mock(ObjectMapperWithExtensionConfig)

    def accessTokenInformationProvider = new AccessTokenInformationProvider(resourceServerUriBuilder: resourceServerUriBuilder,
        httpClientHelper: httpClientHelper, mapper: mapper)

    def "the user id should be provided by retrieving information from the access token"() {
        given:
        def accessToken = "theToken"
        def uri = "/me"
        def requestResult = Mock(HttpClientRequestResult)

        def jsonUserValues = "{some json stuff}"
        def meUserMock = Mock(MeUserRepresentation)

        def meUserId ="1234"

        when:
        def userId = accessTokenInformationProvider.getUserIdFromToken(accessToken)

        then:
        1 * resourceServerUriBuilder.buildMeEndpointUri() >> uri
        1 * httpClientHelper.executeHttpGet(uri, HttpHeader.AUTHORIZATION, accessToken) >> requestResult
        1 * requestResult.getBody() >> jsonUserValues
        1 * mapper.readValue(jsonUserValues, MeUserRepresentation) >> meUserMock
        1 * meUserMock.getId() >> meUserId
        userId == meUserId
    }
}