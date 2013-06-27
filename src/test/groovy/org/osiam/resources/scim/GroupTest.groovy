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

package org.osiam.resources.scim

import org.osiam.resources.scim.Group
import org.osiam.resources.scim.MultiValuedAttribute
import spock.lang.Specification

class GroupTest extends Specification {
    def "should be able to generate a group"() {
        given:
        def builder = new Group.Builder().setDisplayName("display").setAny(new Object())
        when:
        def group = builder.build()

        then:
        group.displayName == builder.displayName
        group.members == builder.members
    }

    def "should be able to enrich group members"() {
        given:
        def group = new Group.Builder().setDisplayName("display").setAny(new Object()).build()
        when:
        group.members.add(new MultiValuedAttribute.Builder().build())

        then:
        group.members.size() == 1
    }

    def "members should be a must exist implementation"() {
        given:
        def group = new Group.Builder().setDisplayName("display").setAny(new Object()).build()

        when:
        group.members.add(new MultiValuedAttribute.Builder().build())

        then:
        group.members != null

    }


    def "should contain empty public constructor for json serializing"() {
        when:
        def result = new Group()
        then:
        result
    }

    def "should be able to clone a group"() {
        given:
        def group = new Group.Builder().
                setDisplayName("display").
                setAny(new Object()).
                setId("id").build()
        when:
        def result  = new Group.Builder(group).build()

        then:
        group.displayName == result.displayName
        group.members == result.members
        group.id == result.id
    }

}
