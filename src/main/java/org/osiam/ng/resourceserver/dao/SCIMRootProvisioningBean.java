package org.osiam.ng.resourceserver.dao;

import org.osiam.ng.scim.dao.SCIMRootProvisioning;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 07.05.13
 * Time: 10:16
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SCIMRootProvisioningBean implements SCIMRootProvisioning {

    private static final Logger LOGGER = Logger.getLogger(SCIMRootProvisioningBean.class.getName());

    @Inject
    private SCIMUserProvisioningBean scimUserProvisioningBean;

    @Inject
    private SCIMGroupProvisioningBean scimGroupProvisioningBean;

    @Override
    public SCIMSearchResult search(String filter, String sortBy, String sortOrder, int count, int startIndex) {

        int userGroupMaxResult = count / 2;
        SCIMSearchResult userSearchResult = addUserToSearchResult(filter, sortBy, sortOrder, userGroupMaxResult,
                startIndex);
        SCIMSearchResult groupSearchResult = addGroupToSearchResult(filter, sortBy, sortOrder, userGroupMaxResult,
                startIndex);
        SCIMSearchResult scimSearchResult = new SCIMSearchResult(userSearchResult.getResult(), userSearchResult.getTotalResult());
        scimSearchResult.addResult(groupSearchResult.getResult());
        scimSearchResult.addTotalResult(groupSearchResult.getTotalResult());
        return scimSearchResult;
    }

    private SCIMSearchResult addUserToSearchResult(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        try {
            return scimUserProvisioningBean.search(filter, sortBy, sortOrder, count, startIndex);
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Filter " + filter + " not usable on User", e);
            return new SCIMSearchResult(new ArrayList(), 0);
        }
    }

    private SCIMSearchResult addGroupToSearchResult(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        try {
            return scimGroupProvisioningBean.search(filter, sortBy, sortOrder, count, startIndex);
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Filter " + filter + " not usable on Group", e);
            return new SCIMSearchResult(new ArrayList(), 0);
        }
    }
}
