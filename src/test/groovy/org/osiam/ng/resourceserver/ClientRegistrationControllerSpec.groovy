package org.osiam.ng.resourceserver

import org.osiam.ng.scim.exceptions.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.Query
import java.lang.reflect.Method

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 27.05.13
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
class ClientRegistrationControllerSpec extends Specification {

    def em = Mock(EntityManager)
    def clientRegistrationController = new ClientRegistrationController(em: em)

    def "should contain a method to GET a client"(){
        given:
        Method method = ClientRegistrationController.class.getDeclaredMethod("getClient", String)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        then:
        mapping.value() == ["/{id}"]
        mapping.method() == [RequestMethod.GET]
        body

    }

    def "should contain a method to POST a client"(){
        given:
        Method method = ClientRegistrationController.class.getDeclaredMethod("create", Object)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.POST]
        body
        defaultStatus.value() == HttpStatus.CREATED
    }

    def "should contain a method to DELETE a client"(){
        given:
        Method method = ClientRegistrationController.class.getDeclaredMethod("delete", String)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.DELETE]
        defaultStatus.value() == HttpStatus.OK
    }

    def "should be able to get an registered client"() {
        given:
        def queryMock = Mock(Query)
        def resultList = ["hanz"]
        em.createNamedQuery("getById") >> queryMock
        queryMock.getResultList() >> resultList

        when:
        def result = clientRegistrationController.getClient("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        result == "hanz"
    }

    def "should throw exception if no result was found"() {
        given:
        def queryMock = Mock(Query)
        def resultList = []
        em.createNamedQuery("getById") >> queryMock
        queryMock.getResultList() >> resultList

        when:
        clientRegistrationController.getClient("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        def e = thrown(ResourceNotFoundException)
        e.getMessage() == "Resource f47ac10b-58cc-4372-a567-0e02b2c3d479 not found."
    }

    def "should be able to create a new client"() {
        when:
        def result = clientRegistrationController.create("hanz")

        then:
        result == "hanz"
        1 * em.persist("hanz")
    }

    def "should be able to delete a client"() {
        given:
        def queryMock = Mock(Query)
        def resultList = ["hanz"]
        em.createNamedQuery("getById") >> queryMock
        queryMock.getResultList() >> resultList

        when:
        clientRegistrationController.delete("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        1 * em.remove("hanz")
    }
}
