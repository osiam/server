package org.osiam.web.resource

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

/**
 * Test for /me User representation.
 * User: Jochen Todea
 * Date: 26.11.13
 * Time: 16:19
 * Created: with Intellij IDEA
 */
class MeUserRepresentationTest extends Specification {

    def"should be deserializable"() {
        given:
        def mapper = new ObjectMapper()

        def meUserAsString = "{\"id\":\"cef9452e-00a9-4cec-a086-d171374ffbef\"," +
                "\"name\":\"Mar Issa\"," +
                "\"first_name\":\"Issa\"," +
                "\"last_name\":\"Mar\"," +
                "\"link\":\"not supported.\"," +
                "\"userName\":\"marissa\"," +
                "\"gender\":\"female\"," +
                "\"email\":\"mari@ssa.ma\"," +
                "\"timezone\":2," +
                "\"locale\":null," +
                "\"verified\":true," +
                "\"updated_time\":\"2011-10-10T00:00:00.000+02:00\"}"

        when:
        def result = mapper.readValue(meUserAsString, MeUserRepresentation)

        then:
        result.getEmail() == "mari@ssa.ma"
        result.getFirst_name() == "Issa"
        result.getLast_name() == "Mar"
        result.getGender() == "female"
        result.getId() == "cef9452e-00a9-4cec-a086-d171374ffbef"
        result.getLink() == "not supported."
        result.getLocale() == null
        result.getName() == "Mar Issa"
        result.getTimezone() == 2
        result.getUserName() == "marissa"
        result.isVerified()
        result.getUpdated_time() == "2011-10-10T00:00:00.000+02:00"
    }

    def "should be serializable"() {
        given:
        def mapper = new ObjectMapper()

        def user = new MeUserRepresentation()
        user.setEmail("mari@ssa.ma")
        user.setFirst_name("Issa")
        user.setLast_name("Mar")
        user.setGender("female")
        user.setId("cef9452e-00a9-4cec-a086-d171374ffbef")
        user.setLink("not supported.")
        user.setLocale(null)
        user.setName("Mar Issa")
        user.setTimezone(2)
        user.setUserName("marissa")
        user.setVerified(true)
        user.setUpdated_time("2011-10-10T00:00:00.000+02:00")

        when:
        def result = mapper.writeValueAsString(user)

        then:
        result == "{\"id\":\"cef9452e-00a9-4cec-a086-d171374ffbef\"," +
                "\"name\":\"Mar Issa\"," +
                "\"first_name\":\"Issa\"," +
                "\"last_name\":\"Mar\"," +
                "\"link\":\"not supported.\"," +
                "\"userName\":\"marissa\"," +
                "\"gender\":\"female\"," +
                "\"email\":\"mari@ssa.ma\"," +
                "\"timezone\":2," +
                "\"locale\":null," +
                "\"verified\":true," +
                "\"updated_time\":\"2011-10-10T00:00:00.000+02:00\"}"
    }
}
