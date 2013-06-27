package org.osiam.ng.resourceserver.dao

import org.osiam.resources.scim.Resource
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: phil
 * Date: 5/16/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
class SCIMSearchResultTest extends Specification {

    def "should contain total result"(){
        when:
          def a = new SCIMSearchResult(null, 2342)
        then:
        !a.result
        a.totalResult == 2342
    }

    def "should contain result"(){
        given:
        def result = []
        when:
        def a = new SCIMSearchResult(result, 2342)
        then:
        a.result == result
        a.totalResult == 2342
    }

    def "should add result and total result"() {
        given:
        def result = ["first"]
        when:
        def a = new SCIMSearchResult(result, 2342)
        a.addTotalResult(2342)
        a.addResult(["second"])
        then:
        a.result == ["first", "second"]
        a.totalResult == 4684
    }

    def "should get a schema"() {
        given:
        def resource = Mock(Resource)
        def listMock = Mock(List)
        def a = new SCIMSearchResult(listMock, 2342)

        def set = ["schema1", "schema2"] as Set
        listMock.size() >> 10
        listMock.get(0) >> resource
        resource.getSchemas() >> set

        when:
        def result = a.getSchemas()

        then:
        result == ["schema1", "schema2"] as Set
    }
}
