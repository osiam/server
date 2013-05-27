package org.osiam.ng.resourceserver.dao

import org.osiam.ng.scim.exceptions.ResourceNotFoundException
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.Query

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 27.05.13
 * Time: 12:03
 * To change this template use File | Settings | File Templates.
 */
class ClientDaoSpec extends Specification {

    def em = Mock(EntityManager)
    def clientDao = new ClientDao(em: em)

    def "should be able to get an registered client"() {
        given:
        def queryMock = Mock(Query)
        def resultList = ["hanz"]
        em.createNamedQuery("getById") >> queryMock
        queryMock.getResultList() >> resultList

        when:
        def result = clientDao.getClient("f47ac10b-58cc-4372-a567-0e02b2c3d479")

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
        clientDao.getClient("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        def e = thrown(ResourceNotFoundException)
        e.getMessage() == "Resource f47ac10b-58cc-4372-a567-0e02b2c3d479 not found."
    }

    def "should be able to create a new client"() {
        when:
        def result = clientDao.create("hanz")

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
        clientDao.delete("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        1 * em.remove("hanz")
    }
}