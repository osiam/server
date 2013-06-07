package org.osiam.ng.scim.mvc.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This Controller is used to manage Root URI actions
 * <p/>
 * http://tools.ietf.org/html/draft-ietf-scim-core-schema-00
 * <p/>
 * it is based on the SCIM 2.0 API Specification:
 * <p/>
 * http://tools.ietf.org/html/draft-ietf-scim-api-00
 *
 *
 */
@Controller
@RequestMapping(value = "/")
public class RootController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String searchWithGet() {
        throw new UnsupportedOperationException("We do not support search across resources at the moment. " +
                "Please get in contact with us info@osiam.org and explain your usecase, " +
                "so we can prioritize the implementation of the proper search across all resources.");
    }

    @RequestMapping(value = ".search", method = RequestMethod.POST)
    @ResponseBody
    public String searchWithPost() {
        throw  new UnsupportedOperationException("We do not support search across resources at the moment. " +
                "Please get in contact with us info@osiam.org and explain your usecase, " +
                "so we can prioritize the implementation of the proper search across all resources.");
    }
}