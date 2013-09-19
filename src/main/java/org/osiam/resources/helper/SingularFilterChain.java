/*
 * Copyright 2013
 *     tarent AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osiam.resources.helper;


import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.ImEntity;
import org.osiam.storage.entities.PhoneNumberEntity;
import org.osiam.storage.entities.PhotoEntity;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingularFilterChain implements FilterChain {
    static final Pattern SINGULAR_CHAIN_PATTERN =
            Pattern.compile("(\\S+) (" + Constraints.createOrConstraints() + ")[ ]??([\\S ]*?)");
    private final String key;
    private final Constraints constraint;
    private final Object value;

    public SingularFilterChain(String chain, Class clazz) {
        Matcher matcher = SINGULAR_CHAIN_PATTERN.matcher(chain);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(chain + " is not a SingularFilterChain.");
        }
        this.key = matcher.group(1).trim();
        this.constraint = Constraints.fromString.get(matcher.group(2)); // NOSONAR - no need to make constant for number

        this.value = castToOriginValue(matcher.group(3).trim(), clazz); // NOSONAR - no need to make constant for number

    }

    private Object castToOriginValue(String group, Class clazz) {

        if (isNumber(group)) {
            return Long.valueOf(group);
        }
        return getBooleanOrMultivalue(group, clazz);
    }

    private Object getBooleanOrMultivalue(String group, Class clazz) {
        List<String> split = splitKey();
        List<Field> fields = getAllFieldsIncludingSuperclass(clazz, new ArrayList<Field>());
        Field field = getSingleField(split, fields);
        String className = getClassName(field);

        switch (className) {
            case "Boolean":
                if(group.equalsIgnoreCase("true") || group.equalsIgnoreCase("false")) {
                    return Boolean.valueOf(group);
                }
                throw new IllegalArgumentException("Value of Field " + key + " mismatch!");
            case "EmailEntity":
                if(split.get(1).equals("type")) {
                    return EmailEntity.CanonicalEmailTypes.valueOf(group);
                } else if (split.get(1).equals("primary")) {
                    if(group.equalsIgnoreCase("true") || group.equalsIgnoreCase("false")) {
                        return Boolean.valueOf(group);
                    }
                    throw new IllegalArgumentException("Value of Field " + key + " mismatch!");
                }
                break;
            case "PhotoEntity":
                if(split.get(1).equals("type")) {
                    return PhotoEntity.CanonicalPhotoTypes.valueOf(group);
                }
                break;
            case "ImEntity":
                if(split.get(1).equals("type")) {
                    return ImEntity.CanonicalImTypes.valueOf(group);
                }
                break;
            case "PhoneNumberEntity":
                if(split.get(1).equals("type")) {
                    return PhoneNumberEntity.CanonicalPhoneNumberTypes.valueOf(group);
                }
                break;
            case "AddressEntity":
                if(split.get(1).equals("primary")) {
                    if(group.equalsIgnoreCase("true") || group.equalsIgnoreCase("false")) {
                        return Boolean.valueOf(group);
                    }
                    throw new IllegalArgumentException("Value of Field " + key + " mismatch!");
                }
                break;
        }
        return getStringOrDate(group);
    }

    /* Method to get the simple class name even if the field is a Set.
     * In case it is a Set, the generic simple class name is returned.
     */
    private String getClassName(Field field) {
        if (field.getType().getSimpleName().equals("Set")) {
            ParameterizedType fieldGenericType = (ParameterizedType) field.getGenericType();
            Class<?> fieldGenericClass = (Class<?>) fieldGenericType.getActualTypeArguments()[0];
            return fieldGenericClass.getSimpleName();
        } else {
            return field.getType().getSimpleName();
        }
    }

    private Field getSingleField(List<String> split, List<Field> fields) {
        Field field = null;
        for (Field f : fields) {
            if (f.getName().equals(split.get(0))) {
                field = f;
                break;
            }
        }
        return field;
    }

    private List<String> splitKey() {
        List<String> split;
        if(key.contains(".")) {
            split = Arrays.asList(key.split("\\."));
        } else {
            split = new ArrayList<>();
            split.add(key);
        }
        return split;
    }

    private List<Field> getAllFieldsIncludingSuperclass(Class clazz, List<Field> fields) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSimpleName().equals("InternalIdSkeleton")) {
            return fields;
        }
        return getAllFieldsIncludingSuperclass(clazz.getSuperclass(), fields);
    }

    private Object getStringOrDate(String group) {
        String result = group.replace("\"", "");
        try {
            return tryToGetDate(result);
        } catch (IllegalArgumentException e) {
            return result;
        }
    }

    private Object tryToGetDate(String result) {
        DateTime time = ISODateTimeFormat.dateTimeParser().parseDateTime(result);
        return time.toDate();
    }

    private boolean isNumber(String group) {
        return group.matches("[0-9]+");
    }

    @Override
    public Criterion buildCriterion() {
        switch (constraint) {
            case CONTAINS:
                return Restrictions.like(key, "%" + value + "%");
            case STARTS_WITH:
                return Restrictions.like(key, value + "%");
            case EQUALS:
                return Restrictions.eq(key, value);
            case GREATER_EQUALS:
                return Restrictions.ge(key, value);
            case GREATER_THAN:
                return Restrictions.gt(key, value);
            case LESS_EQUALS:
                return Restrictions.le(key, value);
            case LESS_THAN:
                return Restrictions.lt(key, value);
            case PRESENT:
                return Restrictions.isNotNull(key);
            default:
                throw new IllegalArgumentException("Unknown constraint.");
        }
    }


    public enum Constraints {
        EQUALS("eq"),
        CONTAINS("co"),
        STARTS_WITH("sw"),
        PRESENT("pr"),
        GREATER_THAN("gt"),
        GREATER_EQUALS("ge"),
        LESS_THAN("lt"),
        LESS_EQUALS("le");
        private static Map<String, Constraints> fromString = new ConcurrentHashMap<>();

        static {
            for (final Constraints k : values()) {
                fromString.put(k.constraint, k);
            }
        }

        private final String constraint; // NOSONAR - is not singular because it is used in the static block


        Constraints(String constraint) {
            this.constraint = constraint;
        }

        static String createOrConstraints() {
            StringBuilder sb = new StringBuilder();
            for (Constraints k : values()) {
                if (sb.length() != 0) {
                    sb.append("|");
                }
                sb.append(k.constraint);
            }
            return sb.toString();

        }


    }
}
