package org.osiam.ng.scim.dao;

import org.osiam.ng.resourceserver.dao.SCIMSearchResult;
import scim.schema.v2.Resource;

import java.util.List;

/**
 * This interface has the purpose to provide SCIM root URI functionality
 */
public interface SCIMRootProvisioning<T extends Resource> {

    /**
     * This method provide a search across booth, users and groups.
     *
     * @param filter
     *              the filter expression.
     * @param sortBy
     *              the field name which is used to sort by
     * @param sortOrder
     *              the sort order. Allowed: "ascending" and "descending". Default is "ascending"
     * @param count
     *              the maximum returned results per page. Default: 100
     * @param startIndex
     *              the value to start from for paging. Default: 1
     * @return the search results
     */
    SCIMSearchResult<T> search(String filter, String sortBy, String sortOrder, int count, int startIndex);
}