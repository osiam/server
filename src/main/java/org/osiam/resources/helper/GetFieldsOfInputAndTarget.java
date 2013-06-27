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

package org.osiam.resources.helper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class GetFieldsOfInputAndTarget {
    private final Map<String, Field> inputFields;
    private final Map<String, Field> targetFields;

    public GetFieldsOfInputAndTarget(){
        this.inputFields = null;
        this.targetFields = null;
    }


    public GetFieldsOfInputAndTarget(Class<?> inputClass, Class<?> targetClass) {
        this.inputFields = getFieldsAsNormalizedMap(inputClass);
        this.targetFields = getFieldsAsNormalizedMap(targetClass);

    }

    public Map<String, Field> getFieldsAsNormalizedMap(Class<?> clazz) {
        Map<String, Field> fields = new HashMap<>();
        if (clazz != null) {
            for (Field f : clazz.getDeclaredFields()) {
                fields.put(f.getName().toLowerCase(), f);
            }
            fields.putAll(getFieldsAsNormalizedMap(clazz.getSuperclass()));
        }
        return fields;
    }

    public Map<String, Field> getInputFields() {
        return inputFields;
    }

    public Map<String, Field> getTargetFields() {
        return targetFields;
    }
}