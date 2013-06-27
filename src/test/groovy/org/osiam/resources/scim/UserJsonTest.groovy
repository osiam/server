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

package org.osiam.resources.scim

import org.codehaus.jackson.map.ObjectMapper
import org.osiam.resources.scim.Address
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.Name
import org.osiam.resources.scim.User
import spock.lang.Specification

class UserJsonTest extends Specification {
    def mapper = new ObjectMapper()



    def "should create flat list on json output"() {
        given:
        def builder = new User.Builder("test").setActive(true)
                .setAny(["ha"] as Set)
                .setDisplayName("display")
                .setAddresses([new Address.Builder().setCountry("de").setFormatted("moep").setPostalCode("53111").build()])
                .setEmails([new MultiValuedAttribute.Builder().setValue("moep").build()])
                .setPhoneNumbers([new MultiValuedAttribute.Builder().setValue("moep").build()])
                .setIms([new MultiValuedAttribute.Builder().setValue("moep").build()])
                .setGroups([new MultiValuedAttribute.Builder().setValue("moep").build()])
                .setPhotos([new MultiValuedAttribute.Builder().setValue("moep").build()])
                .setLocale("locale")
                .setName(new Name.Builder().build())
                .setNickName("nickname")
                .setPassword("password")
                .setPreferredLanguage("prefereedLanguage")
                .setProfileUrl("profileUrl")
                .setTimezone("time")
                .setTitle("title")
                .setUserType("userType")
                .setExternalId("externalid").setId("id").build()

        when:
        def json = mapper.writeValueAsString(builder)
        then:
        json == """{"id":"id","schemas":["urn:scim:schemas:core:1.0"],"externalId":"externalid","userName":"test","name":{},"displayName":"display","nickName":"nickname","profileUrl":"profileUrl","title":"title","userType":"userType","preferredLanguage":"prefereedLanguage","locale":"locale","timezone":"time","active":true,"password":"password","emails":[{"value":"moep"}],"phoneNumbers":[{"value":"moep"}],"ims":[{"value":"moep"}],"photos":[{"value":"moep"}],"addresses":[{"formatted":"moep","postalCode":"53111","country":"de"}],"groups":[{"value":"moep"}],"any":["ha"]}"""
    }


}
