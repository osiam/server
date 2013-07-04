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

    public SingularFilterChain(String chain) {
        Matcher matcher = SINGULAR_CHAIN_PATTERN.matcher(chain);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(chain + " is not a SingularFilterChain.");
        }
        this.key = matcher.group(1).trim();
        this.constraint = Constraints.fromString.get(matcher.group(2)); // NOSONAR - no need to make constant for number

        this.value = castToOriginValue(matcher.group(3).trim()); // NOSONAR - no need to make constant for number

    }

    private Object castToOriginValue(String group) {

        if (isNumber(group)) {
            return Long.valueOf(group);
        }
        return getStringOrDate(group);
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
        if (group.matches("[0-9]+")) {
            return true;
        }
        return false;
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

        private final String constraint;


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
