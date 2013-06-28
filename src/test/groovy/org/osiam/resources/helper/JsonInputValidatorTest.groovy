package org.osiam.resources.helper

import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 27.06.13
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
class JsonInputValidatorTest extends Specification {

    def jsonInputValidator = new JsonInputValidator()
    def httpServletRequestMock = Mock(HttpServletRequest)
    def readerMock = Mock(BufferedReader)

    def "should validate user without errors if json is valid"(){
        given:
        def userJson = '{"userName":"Chaos666","password":"123"}'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [userJson, null]

        when:
        def user = jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        user.userName == 'Chaos666'
    }

    def "should throw exception if the mandatory userName is not present"(){
        given:
        def userJson = ""
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [userJson, null]

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "The user name is mandatory and MUST NOT be null"
    }

    def "invalid json structure for user should throw exception"(){
        given:
        def userJson = '{"schemas":["urn:scim:schemas:core:1.0"],"userName":"Chaos666","password":"123"'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [userJson, null]

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "The JSON structure is incorrect"
    }

/*    def "invalid attributes in json for user should throw exception"(){
        given:
        def userJson = '{"schemas":["urn:scim:schemas:core:1.0"],"userName":"Chaos666","word":"123"}'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [userJson, null]

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == 'Unrecognized field "word"'
    }

    def "invalid value for attribute in json for user should throw exception"(){
        given:
        def userJson = '{"schemas":["urn:scim:schemas:core:1.0"],"userName":"Chaos666","password":"123","active":"123"}'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [userJson, null]

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == 'Can not construct instance of java.lang.Boolean from String value \'123\''
    }*/

    def "should validate group without errors if json is valid"(){
        given:
        def groupJson = '{"displayName":"Chaos999"}'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [groupJson, null]

        when:
        def group = jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        group.displayName == 'Chaos999'
    }

    def "should throw exception if the mandatory displayName is not present"(){
        given:
        def groupJson = ''
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [groupJson, null]

        when:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == 'The display name is mandatory and MUST NOT be null.'
    }

    def "invalid json structure for group should throw exception"(){
        given:
        def groupJson = '{"displayName":"Chaos999"'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [groupJson, null]

        when:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "The JSON structure is incorrect"
    }

/*    def "invalid attributes in json for group should throw exception"(){
        given:
        def groupJson = '{"displayName":"Chaos999","word":"123"}'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [groupJson, null]

        when:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == 'Unrecognized field "word"'
    }

    def "invalid value for attribute in json for group should throw exception"(){
        given:
        def groupJson = '{"displayName":"Chaos999","members":"not a set"}'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [groupJson, null]

        when:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == 'Can not construct instance of java.util.HashSet from String value \'not a set\''
    }*/
}