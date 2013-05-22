package org.osiam.ng.scim.mvc.user;

import org.osiam.ng.resourceserver.dao.SCIMSearchResult;
import org.osiam.ng.scim.dao.SCIMRootProvisioning;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scim.schema.v2.Resource;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Inject
    private SCIMRootProvisioning scimRootProvisioning;

    private RequestParamHelper requestParamHelper = new RequestParamHelper();
    private JsonResponseEnrichHelper jsonResponseEnrichHelper = new JsonResponseEnrichHelper();

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String searchWithGet(HttpServletRequest request) {
        Map<String,Object> parameterMap = requestParamHelper.getRequestParameterValues(request);
       SCIMSearchResult scimSearchResult = scimRootProvisioning.search((String)parameterMap.get("filter"), (String)parameterMap.get("sortBy"), (String)parameterMap.get("sortOrder"),
                (int)parameterMap.get("count"), (int)parameterMap.get("startIndex"));

        return jsonResponseEnrichHelper.getJsonFromSearchResult(scimSearchResult, parameterMap, scimSearchResult.getSchemas());
    }

    @RequestMapping(value = ".search", method = RequestMethod.POST)
    @ResponseBody
    public String searchWithPost(HttpServletRequest request) {
        Map<String,Object> parameterMap = requestParamHelper.getRequestParameterValues(request);
        SCIMSearchResult scimSearchResult = scimRootProvisioning.search((String)parameterMap.get("filter"), (String)parameterMap.get("sortBy"), (String)parameterMap.get("sortOrder"),
                (int)parameterMap.get("count"), (int)parameterMap.get("startIndex"));

        return jsonResponseEnrichHelper.getJsonFromSearchResult(scimSearchResult, parameterMap, scimSearchResult.getSchemas());
    }


}
