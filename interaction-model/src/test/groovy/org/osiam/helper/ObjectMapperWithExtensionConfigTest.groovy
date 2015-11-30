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

package org.osiam.helper

import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.resources.scim.User
import spock.lang.Specification

/**
 * Test for ObjectMapperWithExtensionConfig class.
 */
class ObjectMapperWithExtensionConfigTest extends Specification {

    def "try to deserialize and serialize a scim user with extensions"() {
        given:
        Extension.Builder builder = new Extension.Builder("urn");
        builder.setField("field1", "value1")
        def extension = builder.build();
        User user = new User.Builder("userName").addExtensions([extension]as Set).build()

        def mapper = new ObjectMapperWithExtensionConfig()

        when:
        def userAsString = mapper.writeValueAsString(user)

        then:
        def userObject = mapper.readValue(userAsString, User)
        userObject.getExtension("urn").getField("field1", ExtensionFieldType.STRING).equals("value1")
    }
}
