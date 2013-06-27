package org.osiam.ng.resourceserver.dao;

import org.osiam.ng.resourceserver.entities.ClientEntity;
import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public class ClientDao {

    @PersistenceContext
    protected EntityManager em;

    public ClientEntity getClient(String id) {
        return getClientById(id);
    }

    public ClientEntity create(ClientEntity client) {
        em.persist(client);
        return client;
    }

    public void delete(String id) {
        em.remove(getClientById(id));
    }

    private ClientEntity getClientById(String id) {
        Query query = em.createNamedQuery("getClientById");
        query.setParameter("id", UUID.fromString(id));
        List result = query.getResultList();
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Resource " + id + " not found.");
        }
        return (ClientEntity) result.get(0);
    }
}