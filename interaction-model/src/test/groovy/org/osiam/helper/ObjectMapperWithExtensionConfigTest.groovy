package org.osiam.helper

import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.resources.scim.User
import spock.lang.Specification

/**
 * Test for ObjectMapperWithExtensionConfig class.
 * User: Jochen Todea
 * Date: 26.11.13
 * Time: 15:42
 * Created: with Intellij IDEA
 */
class ObjectMapperWithExtensionConfigTest extends Specification {

    def "try to deserialize and serialize a scim user with extensions"() {
        given:
        Extension extension = new Extension("urn")
        extension.addOrUpdateField("field1", "value1")
        User user = new User.Builder("userName").addExtensions(["urn":extension]as Map).build()

        def mapper = new ObjectMapperWithExtensionConfig()

        when:
        def userAsString = mapper.writeValueAsString(user)

        then:
        def userObject = mapper.readValue(userAsString, User)
        userObject.getExtension("urn").getField("field1", ExtensionFieldType.STRING).equals("value1")
    }
}