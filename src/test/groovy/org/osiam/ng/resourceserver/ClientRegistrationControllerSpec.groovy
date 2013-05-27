package org.osiam.ng.resourceserver

import org.osiam.ng.resourceserver.dao.ClientDao
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 27.05.13
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
class ClientRegistrationControllerSpec extends Specification {

    def clientDao = Mock(ClientDao)
    def clientRegistrationController = new ClientRegistrationController(clientDao: clientDao)

    def "should contain a method to GET a client"(){
        given:
        Method method = ClientRegistrationController.class.getDeclaredMethod("getClient", String)

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        clientRegistrationController.getClient("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        mapping.value() == ["/{id}"]
        mapping.method() == [RequestMethod.GET]
        body
        1 * clientDao.getClient("f47ac10b-58cc-4372-a567-0e02b2c3d479")
    }

    def "should contain a method to POST a client"(){
        given:
        Method method = ClientRegistrationController.class.getDeclaredMethod("create", Object)

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        clientRegistrationController.create("hanz")

        then:
        mapping.method() == [RequestMethod.POST]
        body
        defaultStatus.value() == HttpStatus.CREATED
        1 * clientDao.create("hanz")
    }

    def "should contain a method to DELETE a client"(){
        given:
        Method method = ClientRegistrationController.class.getDeclaredMethod("delete", String)

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        clientRegistrationController.delete("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        mapping.method() == [RequestMethod.DELETE]
        defaultStatus.value() == HttpStatus.OK
        1 * clientDao.delete("f47ac10b-58cc-4372-a567-0e02b2c3d479")
    }
}