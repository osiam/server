package org.osiam.ng.resourceserver.dao;

import org.osiam.ng.scim.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 27.05.13
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional
public class ClientDao {

    @PersistenceContext
    protected EntityManager em;

    public Object getClient(String id) {
        return getClientById(id);
    }

    public Object create(Object client) {
        em.persist(client);
        return client;
    }

    public void delete(String id) {
        em.remove(getClientById(id));
    }

    private Object getClientById(String id) {
        Query query = em.createNamedQuery("getById");
        query.setParameter("id", UUID.fromString(id));
        List result = query.getResultList();
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Resource " + id + " not found.");
        }
        return result.get(0);
    }
}