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

package org.osiam.storage.entities;

import org.hibernate.annotations.Type;
import org.osiam.resources.scim.MultiValuedAttribute;

import javax.persistence.*;
import java.util.GregorianCalendar;
import java.util.UUID;

@Entity(name = "scim_id")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({@NamedQuery(name = "getById", query = "SELECT i FROM scim_id i WHERE i.id= :id")})
public abstract class InternalIdSkeleton implements ChildOfMultiValueAttribute{

    @Type(type = "pg-uuid")
    @Column(unique = true, nullable = false)
    protected UUID id;

    @Id
    @GeneratedValue
    protected long internal_id;

    @Column(unique = true)
    protected String externalId;

    @ManyToOne(cascade = CascadeType.ALL)
    protected MetaEntity meta = new MetaEntity(GregorianCalendar.getInstance());


    public UUID getId() {
        return id;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    /**
     * @return the external id
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @param externalId the external id
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public abstract String getDisplayName();

    public MultiValuedAttribute toMultiValueScim() {
        return new MultiValuedAttribute.Builder().setDisplay(getDisplayName()).setValue(id.toString()).build();
    }

    @Override
    @Transient
    public String getValue() {
        return id.toString();
    }

    @Override
    public void setValue(String value) {
        id = UUID.fromString(value);
    }

    public MetaEntity getMeta() {
        return meta;
    }

    public void setMeta(MetaEntity meta) {
        this.meta = meta;
    }

    public abstract <T> T toScim();
}
