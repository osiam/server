package org.osiam.ng.scim.mvc.user

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 07.05.13
 * Time: 09:48
 * To change this template use File | Settings | File Templates.
 */
class RootControllerSpec extends Specification{

    def underTest = new RootController()

    def "should throw Unsupported exception on / URI with GET method" () {
        given:
        Method method = RootController.class.getDeclaredMethod("searchWithGet")

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        underTest.searchWithGet()

        then:
        mapping.value() == []
        mapping.method() == [RequestMethod.GET]
        body
        def e = thrown(UnsupportedOperationException)
        e.getMessage() == "We do not support search across resources at the moment. " +
                "Please get in contact with us info@osiam.org and explain your usecase, " +
                "so we can prioritize the implementation of the proper search across all resources."
    }

    def "should throw Unsupported exception on /.search URI with POST method" () {
        given:
        Method method = RootController.class.getDeclaredMethod("searchWithPost")

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        underTest.searchWithPost()

        then:
        mapping.value() == [".search"]
        mapping.method() == [RequestMethod.POST]
        body
        def e = thrown(UnsupportedOperationException)
        e.getMessage() == "We do not support search across resources at the moment. " +
                "Please get in contact with us info@osiam.org and explain your usecase, " +
                "so we can prioritize the implementation of the proper search across all resources."
    }
}