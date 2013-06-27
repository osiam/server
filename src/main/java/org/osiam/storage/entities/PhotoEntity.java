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

import org.osiam.resources.scim.MultiValuedAttribute;

import javax.persistence.*;

/**
 * Photos Entity
 */
@Entity(name = "scim_photo")
public class PhotoEntity extends MultiValueAttributeEntitySkeleton implements ChildOfMultiValueAttributeWithType, HasUser {

    @Column
    @Enumerated(EnumType.STRING)
    private CanonicalPhotoTypes type;

    @ManyToOne
    private UserEntity user;


    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        if(value != null && isValueIncorrect(value)) {
            throw new IllegalArgumentException("The photo value  MUST be a URL which points to an " +
                    "JPEG, GIF, PNG file.");
        }
        this.value = value;
    }

    private boolean isValueIncorrect(String value) {
        return !value.endsWith("(?i)\\.[jpg|jpeg|png|gif]");
    }

    public String getType() {
        if(type != null) {
            return type.toString();
        }
        return null;
    }

    public void setType(String type) {
        if(type != null) {
            this.type = CanonicalPhotoTypes.valueOf(type);
        }
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public MultiValuedAttribute toScim() {
        return new MultiValuedAttribute.Builder().
                setType(getType()).
                setValue(getValue()).
                build();
    }

    public static PhotoEntity fromScim(MultiValuedAttribute multiValuedAttribute) {
        PhotoEntity photoEntity = new PhotoEntity();
        photoEntity.setType(multiValuedAttribute.getType());
        photoEntity.setValue(String.valueOf(multiValuedAttribute.getValue()));
        return photoEntity;
    }

    private enum CanonicalPhotoTypes {
        photo, thumbnail
    }
}