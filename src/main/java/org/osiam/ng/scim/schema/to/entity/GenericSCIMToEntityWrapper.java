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

package org.osiam.ng.scim.schema.to.entity;

import scim.schema.v2.Resource;

import java.lang.reflect.Field;
import java.util.*;

/**
 * This class has the purpose to make it easier to write own provisiong classes with the purpose to map SCIM Classes to
 * Entity-Classes.
 * <p/>
 * How ever this class is in a very early state and not very stable -- therefor you have to follow some rules:
 * <p/>
 * 1. the names of the fields in your entity must be equal to the names used in the ScimSchema
 * 2. if your Entity is an implementation of a multi value object it must implement ChildOfMultiValueAttribute
 * 3. the lists of your entity must be initialized
 */
public class GenericSCIMToEntityWrapper {


    private final Mode mode;
    private final SCIMEntities scimEntities;
    private final For target;
    private Object entity;
    private Resource user;

    public GenericSCIMToEntityWrapper(For target,Resource user, Object entity, Mode mode, SCIMEntities scimEntities) {
        this.target = target;
        this.user = user;
        this.entity = entity;
        this.mode = mode;
        this.scimEntities = scimEntities;
    }

    public void setFields() throws IllegalAccessException, InstantiationException {
        GetFieldsOfInputAndTarget fields = new GetFieldsOfInputAndTarget(user.getClass(), entity.getClass());
        EntityListFieldWrapper entityListFieldWrapper = new EntityListFieldWrapper(entity, mode);
        EntityFieldWrapper entityFieldWrapper = new EntityFieldWrapper(entity, mode);
        Set<String> doNotUpdateThem = deleteAttributes(fields.getTargetFields(), entityFieldWrapper);

        for (Map.Entry<String, Field> e : fields.getInputFields().entrySet()) {
            Field field = fields.getInputFields().get(e.getKey());
            field.setAccessible(true);
            if (!target.READ_ONLY_FIELD_SET.contains(e.getKey()) && !doNotUpdateThem.contains(e.getKey())) {
                Object userValue = field.get(user);
                SCIMEntities.Entity attributes = scimEntities.fromString(e.getKey());
                if (attributes == null) {
                    entityFieldWrapper
                            .updateSingleField(fields.getTargetFields().get(e.getKey()), userValue, e.getKey());
                } else {
                    entityListFieldWrapper.set(userValue, attributes, fields.getTargetFields().get(e.getKey()));
                }
            }
        }
    }

    private Set<String> deleteAttributes(Map<String, Field> entityFields, EntityFieldWrapper entityFieldWrapper)
            throws IllegalAccessException {
        Set<String> doNotUpdateThem = new HashSet<>();
        if (mode == Mode.PATCH && user.getMeta() != null) {
            for (String s : user.getMeta().getAttributes()) {
                String key = s.toLowerCase();
                if (!target.NOT_DELETABLE.contains(key) && !target.READ_ONLY_FIELD_SET.contains(key)) {
                    deleteAttribute(entityFields, entityFieldWrapper, doNotUpdateThem, key);
                }
            }
        }
        return doNotUpdateThem;
    }

    private void deleteAttribute(Map<String, Field> entityFields, EntityFieldWrapper entityFieldWrapper,
                                 Set<String> doNotUpdateThem, String key) throws IllegalAccessException {
        //may use pattern instead ..
        if (key.contains(".")) {
            deletePartsOfAComplexAttribute(entityFields, doNotUpdateThem, key);
        } else {
            deleteSimpleAttribute(entityFields, entityFieldWrapper, doNotUpdateThem, key);
        }
    }

    private void deleteSimpleAttribute(Map<String, Field> entityFields, EntityFieldWrapper entityFieldWrapper,
                                       Set<String> doNotUpdateThem, String key) throws IllegalAccessException {
        Field entityField = entityFields.get(key);
        if (entityField == null) {
            throw new IllegalArgumentException("Field " + key + " is unknown.");
        }

        entityField.setAccessible(true);
        if (scimEntities.fromString(key) != null) {

            ((Collection) entityField.get(entity)).clear();
        } else {
            setEntityFieldToNull(entityFieldWrapper, entityField);
        }
        doNotUpdateThem.add(key);
    }

    public void setEntityFieldToNull(EntityFieldWrapper entityFieldWrapper, Field entityField)
            throws IllegalAccessException {
        entityFieldWrapper.updateSimpleField(entityField, null);
    }

    private void deletePartsOfAComplexAttribute(Map<String, Field> entityFields, Set<String> doNotUpdateThem,
                                                String key) throws IllegalAccessException {
        String[] complexMethod = key.split("\\.");

        int lastElement = complexMethod.length - 1;
        Object object = entity;
        GetComplexEntityFields lastEntityFields = null;
        for (int i = 0; i < lastElement; i++) {
            if (target.READ_ONLY_FIELD_SET.contains(complexMethod[i])) {
                return;
            }
            lastEntityFields = new GetComplexEntityFields(entityFields, complexMethod[i], object).invoke();
        }
        assert lastEntityFields != null;
        lastEntityFields.nullValue(complexMethod[lastElement]);
        doNotUpdateThem.add(complexMethod[0]);
    }

    public enum Mode {
        PATCH, PUT
    }

    public enum For{
        USER(new String[]{"id", "meta", "groups"}, new String[]{"username"}),

        GROUP(new String[]{"id", "meta"}, new String[]{"displayname"});

        final Set<String> READ_ONLY_FIELD_SET;
        final Set<String> NOT_DELETABLE;

        For(String[] readOnly, String[] notDeleteable) {
            READ_ONLY_FIELD_SET = new HashSet<>(Arrays.asList(readOnly));
            NOT_DELETABLE = new HashSet<>(Arrays.asList(notDeleteable));
        }
    }

    private class GetComplexEntityFields {
        private Map<String, Field> entityFields;
        private String key;
        private Object lastObjectOfField;

        public GetComplexEntityFields(Map<String, Field> entityFields, String key, Object lastObjectOfField) {
            this.entityFields = entityFields;
            this.key = key;
            this.lastObjectOfField = lastObjectOfField;
        }

        public GetComplexEntityFields invoke() throws IllegalAccessException {
            Field field = entityFields.get(key);
            field.setAccessible(true);
            lastObjectOfField = field.get(lastObjectOfField);
            Class<?> declaringClass = field.getType();
            entityFields = (new GetFieldsOfInputAndTarget()).getFieldsAsNormalizedMap(declaringClass);
            return this;
        }

        public void nullValue(String s) throws IllegalAccessException {
            Field field = entityFields.get(s);
            if (field == null) {
                throw new IllegalArgumentException("Field " + s + " is unknown.");
            } else if (lastObjectOfField == null) {
                throw new IllegalArgumentException("Field " + key + " is unknown.");
            }
            field.setAccessible(true);
            field.set(lastObjectOfField, null);
        }
    }
}