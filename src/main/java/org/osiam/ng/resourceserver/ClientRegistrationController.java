package org.osiam.ng.resourceserver;

import org.osiam.ng.resourceserver.dao.ClientDao;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

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

    @Inject
    private ClientDao clientDao;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object getClient(@PathVariable final String id) {
        return clientDao.getClient(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Object create(@RequestBody Object client) {
        return clientDao.create(client);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable final String id) {
        clientDao.delete(id);
    }
}