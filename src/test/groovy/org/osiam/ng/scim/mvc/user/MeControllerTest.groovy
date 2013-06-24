package org.osiam.ng.scim.mvc.user

import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.osiam.ng.resourceserver.entities.EmailEntity
import org.osiam.ng.resourceserver.entities.MetaEntity
import org.osiam.ng.resourceserver.entities.NameEntity
import org.osiam.ng.resourceserver.entities.UserEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.InMemoryTokenStore
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class MeControllerTest extends Specification {
    InMemoryTokenStore tokenStore = Mock(InMemoryTokenStore)
    def underTest = new MeController(inMemoryTokenStore: tokenStore)
    OAuth2Authentication authentication = Mock(OAuth2Authentication)
    HttpServletRequest request = Mock(HttpServletRequest)
    Authentication userAuthentication = Mock(Authentication)
    def name = new NameEntity(familyName: "Prefect", givenName: "Fnord", formatted: "Fnord Prefect")
    def user = new UserEntity(active: true, emails: [new EmailEntity(primary: true, value: "test@test.de")],
            name: name, id: UUID.randomUUID(), meta: new MetaEntity(GregorianCalendar.getInstance()),
            locale: "de_DE", username: "fpref")
    DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime()

    def setup() {
        authentication.getUserAuthentication() >> userAuthentication


    }

    def "should return correct facebook representation"() {

        when:

        def result = underTest.getInformation(request)
        then:
        1 * request.getParameter("access_token") >> "access_token"
        1 * tokenStore.readAuthentication("access_token") >> authentication
        1 * userAuthentication.getPrincipal() >> user
        result.email == "test@test.de"
        result.first_name == user.getName().getGivenName()
        result.last_name == user.getName().getFamilyName()
        result.gender == "female"
        result.link == "not supported."
        result.locale == "de_DE"
        result.name == user.getName().getFormatted()
        result.timezone == 2
        result.updated_time == dateTimeFormatter.print(user.getMeta().getLastModified().time)
        result.username == "fpref"
        result.id == user.getId().toString()
        result.isVerified()
    }

    def "should throw exception when no access_token got submitted"() {
        given:
        request.getParameter("access_token") >> null
        when:
        underTest.getInformation(request)
        then:
        def e = thrown(IllegalArgumentException)
        e.message == "No access_token submitted!"

    }

    def "should get access_token in bearer format"() {
        when:
        def result = underTest.getInformation(request)
        then:
        1 * request.getParameter("access_token") >> null
        1 * request.getHeader("Authorization") >> "Bearer access_token"
        1 * tokenStore.readAuthentication("access_token") >> authentication
        1 * userAuthentication.getPrincipal() >> user
        result

    }


    def "should throw exception if principal is not an UserEntity"() {
        when:
        underTest.getInformation(request)
        then:
        1 * request.getParameter("access_token") >> null
        1 * request.getHeader("Authorization") >> "Bearer access_token"
        1 * tokenStore.readAuthentication("access_token") >> authentication
        1 * userAuthentication.getPrincipal() >> new Object()
        def e = thrown(IllegalArgumentException)
        e.message == "User was not authenticated with OSIAM."
    }

    def "should throw exception if no primary email got submitted"() {
        given:
        def user = new UserEntity(active: true, emails: null,
                name: name, id: UUID.randomUUID(), meta: new MetaEntity(GregorianCalendar.getInstance()),
                locale: "de_DE", username: "fpref")
        when:
        underTest.getInformation(request)
        then:
        1 * request.getParameter("access_token") >> null
        1 * request.getHeader("Authorization") >> "Bearer access_token"
        1 * tokenStore.readAuthentication("access_token") >> authentication
        1 * userAuthentication.getPrincipal() >> user
        def e = thrown(IllegalArgumentException)
        e.message == "Unable to generate facebook credentials, no primary email submitted."
    }

    def "should throw exception if no name got submitted"() {
        given:
        def user = new UserEntity(active: true, emails: [new EmailEntity(primary: true, value: "test@test.de")],
                name: null, id: UUID.randomUUID(), meta: new MetaEntity(GregorianCalendar.getInstance()),
                locale: "de_DE", username: "fpref")

        when:
        underTest.getInformation(request)
        then:
        1 * request.getParameter("access_token") >> null
        1 * request.getHeader("Authorization") >> "Bearer access_token"
        1 * tokenStore.readAuthentication("access_token") >> authentication
        1 * userAuthentication.getPrincipal() >> user
        def e = thrown(IllegalArgumentException)
        e.message == "Unable to generate facebook credentials, no name submitted."
    }

}
