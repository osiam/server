package org.osiam.ng.resourceserver.dao

import org.osiam.ng.resourceserver.entities.ClientEntity
import org.osiam.ng.scim.exceptions.ResourceNotFoundException
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.Query

class ClientDaoSpec extends Specification {

    def em = Mock(EntityManager)
    def clientDao = new ClientDao(em: em)

    def resultList = [new ClientEntity()]

    def "should be able to get an registered client"() {
        given:
        def queryMock = Mock(Query)


        when:
        def result = clientDao.getClient("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        1 * em.createNamedQuery("getClientById") >> queryMock
        1 * queryMock.getResultList() >> resultList
        result == resultList.first()

    }

    def "should throw exception if no result was found"() {
        given:
        def queryMock = Mock(Query)
        def resultList = []
        em.createNamedQuery("getClientById") >> queryMock
        queryMock.getResultList() >> resultList

        when:
        clientDao.getClient("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        def e = thrown(ResourceNotFoundException)
        e.getMessage() == "Resource f47ac10b-58cc-4372-a567-0e02b2c3d479 not found."
    }

    def "should be able to create a new client"() {
        given:
        ClientEntity clientEntity = new ClientEntity()
        when:
        def result = clientDao.create(clientEntity)

        then:
        result == clientEntity
        1 * em.persist(clientEntity)
    }

    def "should be able to delete a client"() {
        given:
        def queryMock = Mock(Query)
        em.createNamedQuery("getClientById") >> queryMock
        queryMock.getResultList() >> resultList

        when:
        clientDao.delete("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        1 * em.remove(resultList.first())
    }
}