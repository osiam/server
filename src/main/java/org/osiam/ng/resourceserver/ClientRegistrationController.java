package org.osiam.ng.resourceserver;

import org.osiam.ng.scim.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 27.05.13
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/Client")
public class ClientRegistrationController {

    @PersistenceContext
    protected EntityManager em;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object getClient(@PathVariable final String id) {
        return getClientById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Object create(@RequestBody Object client) {
        em.persist(client);
        return client;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable final String id) {
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