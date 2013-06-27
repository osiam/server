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



package org.osiam.ng.resourceserver

import org.osiam.resources.helper.SingularFilterChain
import spock.lang.Specification

class SingularFilterChainTest extends Specification{


    def "should parse equals (eq)"(){
        when:
        def result = new SingularFilterChain("userName eq \"bjensen\"").buildCriterion()
        then:
        result.propertyName == 'userName'
        result.op == "="
        result.value == "bjensen"



    }

    def "should parse without \""(){
        when:
        def result = new SingularFilterChain("userName eq 1").buildCriterion()
        then:
        result.propertyName == 'userName'
        result.op == "="
        result.value == 1

    }

    def "should parse contains (co)"(){
        when:
        def result = new SingularFilterChain("name.familyName co \"O'Malley\"").buildCriterion()
        then:
        result.propertyName == 'name.familyName'
        result.op == " like "
        result.value == "%O'Malley%"
    }

    def "should parse starts with (sw)"(){
        when:
        def result = new SingularFilterChain("userName sw \"L\"").buildCriterion()
        then:
        result.propertyName == 'userName'
        result.op == " like "
        result.value == "L%"

    }

    def "should parse present (pr)"(){
        when:
        def result = new SingularFilterChain("title pr").buildCriterion()
        then:
        result.propertyName == 'title'
    }

    def "should parse greater than (gt)"(){
        when:
        def result = new SingularFilterChain("meta.lastModified gt \"2011-05-13T04:42:34Z\"").buildCriterion()
        then:
        result.propertyName == 'meta.lastModified'
        result.op == ">"
        result.value

    }

    def "should parse greater than or equal (ge)"(){
        when:
        def result = new SingularFilterChain("meta.lastModified ge \"2011-05-13T04:42:34Z\"").buildCriterion()
        then:
        result.propertyName == 'meta.lastModified'
        result.op == ">="
        result.value

    }

    def "should parse less than (lt)"(){
        when:
        def result = new SingularFilterChain("meta.lastModified lt \"2011-05-13T04:42:34Z\"").buildCriterion()
        then:
        result.propertyName == 'meta.lastModified'
        result.op == "<"
        result.value

    }

    def "should parse less than or equal (le)"(){
        when:
        def result = new SingularFilterChain("meta.lastModified le \"2011-05-13T04:42:34Z\"").buildCriterion()
        then:
        result.propertyName == 'meta.lastModified'
        result.op == "<="
        result.value instanceof Date

    }

    def "should parse iso time"(){
        when:
        def result = new SingularFilterChain("meta.lastModified le \"2011-05-13T04:42:34+02\"").buildCriterion()
        then:
        result.propertyName == 'meta.lastModified'
        result.op == "<="
        result.value instanceof Date

    }


    def "should throw exception if no constraint matches"(){
        when:
        new SingularFilterChain("userName xx \"bjensen\"")

        then:
        def exception = thrown(IllegalArgumentException)
        exception.getMessage() == "userName xx \"bjensen\"" + " is not a SingularFilterChain."
    }
}