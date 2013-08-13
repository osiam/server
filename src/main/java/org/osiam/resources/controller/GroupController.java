/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.resources.controller;

import org.osiam.resources.helper.JsonInputValidator;
import org.osiam.resources.helper.SCIMSearchResult;
import org.osiam.resources.provisioning.SCIMGroupProvisioning;
import org.osiam.resources.helper.JsonResponseEnrichHelper;
import org.osiam.resources.helper.RequestParamHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriTemplate;
import org.osiam.resources.scim.Group;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Controller
@RequestMapping(value = "/Groups")
/**
 * HTTP Api for groups. You can create, delete, replace, update and search groups.
 */
public class GroupController {

    @Inject
    private SCIMGroupProvisioning scimGroupProvisioning;

    @Inject
    private JsonInputValidator jsonInputValidator;

    private RequestParamHelper requestParamHelper = new RequestParamHelper();
    private JsonResponseEnrichHelper jsonResponseEnrichHelper = new JsonResponseEnrichHelper();

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Group create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Group group = jsonInputValidator.validateJsonGroup(request);
        Group createdGroup = scimGroupProvisioning.create(group);
        setLocationUriWithNewId(request, response, createdGroup.getId());
        return createdGroup;
    }

    private void setLocationUriWithNewId(HttpServletRequest request, HttpServletResponse response, String id) {
        String requestUrl = request.getRequestURL().toString();
        URI uri = new UriTemplate("{requestUrl}{internalId}").expand(requestUrl + "/", id);
        response.setHeader("Location", uri.toASCIIString());
    }

    private void setLocation(HttpServletRequest request, HttpServletResponse response) {
        String requestUrl = request.getRequestURL().toString();
        response.setHeader("Location", requestUrl);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET) // NOSONAR - duplicate literals unnecessary
    @ResponseBody
    public Group get(@PathVariable final String id) {
        return scimGroupProvisioning.getById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE) // NOSONAR - duplicate literals unnecessary
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable final String id) {
        scimGroupProvisioning.delete(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT) // NOSONAR - duplicate literals unnecessary
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Group replace(@PathVariable final String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Group group = jsonInputValidator.validateJsonGroup(request);
        Group createdGroup = scimGroupProvisioning.replace(id, group);
        setLocation(request, response);
        return createdGroup;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH) // NOSONAR - duplicate literals unnecessary
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Group update(@PathVariable final String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Group group = jsonInputValidator.validateJsonGroup(request);
        Group createdGroup = scimGroupProvisioning.update(id, group);
        setLocation(request, response);
        return createdGroup;
    }

    @RequestMapping(method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String searchWithGet(HttpServletRequest request) {
        Map<String,Object> parameterMap = requestParamHelper.getRequestParameterValues(request);
        SCIMSearchResult scimSearchResult = scimGroupProvisioning.search((String)parameterMap.get("filter"), (String)parameterMap.get("sortBy"), (String)parameterMap.get("sortOrder"),
                (int)parameterMap.get("count"), (int)parameterMap.get("startIndex"));

        return jsonResponseEnrichHelper.getJsonFromSearchResult(scimSearchResult, parameterMap, scimSearchResult.getSchemas());
    }

    @RequestMapping(value = "/.search", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String searchWithPost(HttpServletRequest request) {
        Map<String,Object> parameterMap = requestParamHelper.getRequestParameterValues(request);
        SCIMSearchResult scimSearchResult = scimGroupProvisioning.search((String)parameterMap.get("filter"), (String)parameterMap.get("sortBy"), (String)parameterMap.get("sortOrder"),
                (int)parameterMap.get("count"), (int)parameterMap.get("startIndex"));

        return jsonResponseEnrichHelper.getJsonFromSearchResult(scimSearchResult, parameterMap, scimSearchResult.getSchemas());
    }
}
