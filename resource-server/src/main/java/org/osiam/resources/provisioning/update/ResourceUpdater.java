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

package org.osiam.resources.provisioning.update;

import java.util.Set;

import org.osiam.resources.scim.Resource;
import org.osiam.storage.entities.ResourceEntity;
import org.springframework.stereotype.Service;

@Service
public abstract class ResourceUpdater {

    public void update(Resource resource, ResourceEntity resourceEntity) {

        if(resource.getMeta() != null && resource.getMeta().getAttributes() != null) {
            removeAttributes(resource.getMeta().getAttributes(), resourceEntity);
        }

        if(resource.getExternalId() != null && !resource.getExternalId().isEmpty()) {
            resourceEntity.setExternalId(resource.getExternalId());
        }

    }

    private void removeAttributes(Set<String> attributes, ResourceEntity resourceEntity) {
        for (String attribute : attributes) {
            if(attribute.equalsIgnoreCase("externalId")) {
                resourceEntity.setExternalId(null);
            }
        }
    }

}