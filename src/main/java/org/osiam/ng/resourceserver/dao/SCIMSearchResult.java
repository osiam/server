package org.osiam.ng.resourceserver.dao;

import scim.schema.v2.Resource;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: phil
 * Date: 5/16/13
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class SCIMSearchResult<T> {
    private List<T> result;
    private long totalResult;

    public SCIMSearchResult(List<T> list, long totalResult) {
        this.result = list;
        this.totalResult = totalResult;

    }

    public void addResult(List<T> result) {
        this.result.addAll(result);
    }

    public void addTotalResult(long totalResult) {
        this.totalResult += totalResult;
    }

    public List<T> getResult() {
        return result;
    }

    public long getTotalResult() {
        return totalResult;
    }

    public Set<String> getSchemas() {
        Set<String> schemas = null;
        if (this.result.size() != 0) {
            Resource resource = (Resource) result.get(0);
            schemas = resource.getSchemas();
        }
        return schemas;
    }
}
