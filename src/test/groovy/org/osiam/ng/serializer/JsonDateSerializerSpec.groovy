package org.osiam.ng.serializer

import org.codehaus.jackson.JsonGenerator
import org.codehaus.jackson.map.SerializerProvider
import org.osiam.resources.helper.JsonDateSerializer
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 23.05.13
 * Time: 11:53
 * To change this template use File | Settings | File Templates.
 */
class JsonDateSerializerSpec extends Specification {

    def jsonDateSerializer = new JsonDateSerializer()
    def jsonGeneratorMock = Mock(JsonGenerator)
    def serializerProviderMock = Mock(SerializerProvider)

    def "should serialize dates to ISO value"() {
        when:
        jsonDateSerializer.serialize(GregorianCalendar.getInstance().getTime(), jsonGeneratorMock, serializerProviderMock)

        then:
        1 * jsonGeneratorMock.writeString(_)
    }
}
