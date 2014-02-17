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

package org.osiam.web.resource

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

/**
 * Test for /me User representation.
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
        result.getFirstName() == "Issa"
        result.getLastName() == "Mar"
        result.getGender() == "female"
        result.getId() == "cef9452e-00a9-4cec-a086-d171374ffbef"
        result.getLink() == "not supported."
        result.getLocale() == null
        result.getName() == "Mar Issa"
        result.getTimezone() == 2
        result.getUserName() == "marissa"
        result.isVerified()
        result.getUpdatedTime() == "2011-10-10T00:00:00.000+02:00"
    }

    def "should be serializable"() {
        given:
        def mapper = new ObjectMapper()

        def user = new MeUserRepresentation()
        user.setEmail("mari@ssa.ma")
        user.setFirstName("Issa")
        user.setLastName("Mar")
        user.setGender("female")
        user.setId("cef9452e-00a9-4cec-a086-d171374ffbef")
        user.setLink("not supported.")
        user.setLocale(null)
        user.setName("Mar Issa")
        user.setTimezone(2)
        user.setUserName("marissa")
        user.setVerified(true)
        user.setUpdatedTime("2011-10-10T00:00:00.000+02:00")

        when:
        def result = mapper.writeValueAsString(user)

        then:
        result.contains("\"id\":\"cef9452e-00a9-4cec-a086-d171374ffbef\"")
        result.contains("\"name\":\"Mar Issa\"")
        result.contains("\"first_name\":\"Issa\"")
        result.contains("\"last_name\":\"Mar\"")
        result.contains("\"link\":\"not supported.\"")
        result.contains("\"userName\":\"marissa\"")
        result.contains("\"gender\":\"female\"")
        result.contains("\"email\":\"mari@ssa.ma\"")
        result.contains("\"timezone\":2")
        result.contains("\"locale\":null")
        result.contains("\"verified\":true")
        result.contains("\"updated_time\":\"2011-10-10T00:00:00.000+02:00\"")
    }
}