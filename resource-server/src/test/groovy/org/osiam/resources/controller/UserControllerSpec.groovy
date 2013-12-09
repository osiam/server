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

package org.osiam.resources.controller

import org.osiam.resources.helper.AttributesRemovalHelper
import org.osiam.resources.helper.JsonInputValidator
import org.osiam.resources.helper.RequestParamHelper
import org.osiam.resources.provisioning.SCIMUserProvisioning
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.Name
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.resources.scim.User
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.MetaEntity
import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.UserEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Method

class UserControllerSpec extends Specification {

    def requestParamHelper = Mock(RequestParamHelper)
    def jsonInputValidator = Mock(JsonInputValidator)
    def attributesRemovalHelper = Mock(AttributesRemovalHelper)
    def underTest = new UserController(requestParamHelper: requestParamHelper,
    jsonInputValidator: jsonInputValidator, attributesRemovalHelper: attributesRemovalHelper)
    def provisioning = Mock(SCIMUserProvisioning)
    def httpServletRequest = Mock(HttpServletRequest)
    def httpServletResponse = Mock(HttpServletResponse)

    User user = new User.Builder("test").setActive(true)
    .setDisplayName("display")
    .setLocale("locale")
    .setName(new Name.Builder().build())
    .setNickName("nickname")
    .setPassword("password")
    .setPreferredLanguage("prefereedLanguage")
    .setProfileUrl("profileUrl")
    .setTimezone("time")
    .setTitle("title")
    .setUserType("userType")
    .setExternalId("externalid")
    .setId("id")
    .setMeta(new Meta.Builder().build())
    .build()

    NameEntity nameEntity = new NameEntity(familyName: "Prefect", givenName: "Fnord", formatted: "Fnord Prefect")
    UserEntity userEntity = new UserEntity(active: true, emails: [
        new EmailEntity(primary: true, value: "test@test.de")
    ],
    name: nameEntity, id: UUID.randomUUID(), meta: new MetaEntity(GregorianCalendar.getInstance()),
    locale: "de_DE", userName: "fpref")

    def setup() {
        underTest.setScimUserProvisioning(provisioning)
    }

    def "should return a cloned user based on a user found by provisioning on getUser"() {
        when:
        def result = underTest.getUser("one")
        then:
        1 * provisioning.getById("one") >> user
        validateUser(result, false)
    }

    def "should contain a method to GET a user"() {
        given:
        Method method = UserController.class.getDeclaredMethod("getUser", String)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        then:
        mapping.value() == ["/{id}"]
        mapping.method() == [RequestMethod.GET]
        body
    }

    def "should contain a method to POST a user"() {
        given:
        Method method = UserController.class.getDeclaredMethod("create", HttpServletRequest, HttpServletResponse)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.POST]
        body
        defaultStatus.value() == HttpStatus.CREATED
    }

    def "should contain a method to DELETE a user"() {
        given:
        Method method = UserController.class.getDeclaredMethod("delete", String)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.DELETE]
        defaultStatus.value() == HttpStatus.OK
    }

    def "should call provisioning on DELETE"() {
        when:
        underTest.delete("id")
        then:
        1 * provisioning.delete("id")
    }



    def "should contain a method to PUT a user"() {
        given:
        Method method = UserController.class.getDeclaredMethod("replace", String, HttpServletRequest, HttpServletResponse)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.PUT]
        body
        defaultStatus.value() == HttpStatus.OK
    }

    def "should contain a method to PATCH a user"() {
        given:
        Method method = UserController.class.getDeclaredMethod("update", String, HttpServletRequest, HttpServletResponse)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.PATCH]
        body
        defaultStatus.value() == HttpStatus.OK
    }

    def validateUser(User result, boolean locationChanged) {
        assert result != user
        assert user.password != null
        assert result.password == null
        assert result.active == user.active
        assert result.addresses.empty
        assert result.displayName == user.displayName
        assert result.emails.empty
        assert result.entitlements.empty
        assert result.groups.empty
        assert result.ims.empty
        assert result.locale == user.locale
        assert result.name == user.name
        assert result.nickName == user.nickName
        assert result.phoneNumbers.empty
        assert result.photos.empty
        assert result.preferredLanguage == user.preferredLanguage
        assert result.profileUrl == user.profileUrl
        assert result.roles.empty
        assert result.timezone == user.timezone
        assert result.title == user.title
        assert result.userType == user.userType
        assert result.x509Certificates.empty
        assert result.userName == user.userName
        assert result.id == user.id
        assert result.externalId == user.externalId
        if(!locationChanged){
            assert result.meta == user.meta
        }else{
            result.meta.attributes  == user.meta.attributes
            result.meta.created == user.meta.created
            result.meta.lastModified == user.meta.lastModified
            result.meta.resourceType == user.meta.resourceType
            result.meta.version == user.meta.version
            result.meta.location != user.meta.location
        }
        true
    }

    def "should create the user and add the location header"() {
        given:
        httpServletRequest.getRequestURL() >> new StringBuffer("http://host:port/deployment/User")
        def uri = new URI("http://host:port/deployment/User/id")
        jsonInputValidator.validateJsonUser(httpServletRequest) >> user

        when:
        def result = underTest.create(httpServletRequest, httpServletResponse)

        then:
        1 * provisioning.create(user) >> user
        1 * httpServletResponse.setHeader("Location", uri.toASCIIString())
        validateUser(result, true)
    }

    def "should replace an user and set location header"() {
        given:
        def id = UUID.randomUUID().toString()
        jsonInputValidator.validateJsonUser(httpServletRequest) >> user

        when:
        def result = underTest.replace(id, httpServletRequest, httpServletResponse)

        then:
        1 * provisioning.replace(id, user) >> user
        1 * httpServletRequest.getRequestURL() >> new StringBuffer("http://localhorst/horst/" + id)
        1 * httpServletResponse.setHeader("Location", "http://localhorst/horst/" + id)
        validateUser(result, true)
    }

    def "should update an user and set location header"() {
        given:
        def id = UUID.randomUUID().toString()
        jsonInputValidator.validateJsonUser(httpServletRequest) >> user

        when:
        def result = underTest.update(id, httpServletRequest, httpServletResponse)

        then:
        1 * provisioning.update(id, user) >> user
        1 * httpServletRequest.getRequestURL() >> new StringBuffer("http://localhorst/horst/yo")
        1 * httpServletResponse.setHeader("Location", "http://localhorst/horst/yo")
        validateUser(result, true)
    }

    def "should be able to search a user on /User URI with GET method"() {
        given:
        Method method = UserController.class.getDeclaredMethod("searchWithGet", HttpServletRequest)
        def servletRequestMock = Mock(HttpServletRequest)
        def map = Mock(Map)
        requestParamHelper.getRequestParameterValues(servletRequestMock) >> map

        map.get("filter") >> "filter"
        map.get("sortBy") >> "sortBy"
        map.get("sortOrder") >> "sortOrder"
        map.get("count") >> 10
        map.get("startIndex") >> 1

        def scimSearchResultMock = Mock(SCIMSearchResult)
        def set = ["schemas"] as Set
        provisioning.search("filter", "sortBy", "sortOrder", 10, 1) >> scimSearchResultMock
        scimSearchResultMock.getSchemas() >> set

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        underTest.searchWithGet(servletRequestMock)

        then:
        mapping.value() == []
        mapping.method() == [RequestMethod.GET]
        body
        1 * attributesRemovalHelper.removeSpecifiedAttributes(scimSearchResultMock, map)
    }

    def "should be able to search a user on /User/.search URI with POST method"() {
        given:
        Method method = UserController.class.getDeclaredMethod("searchWithPost", HttpServletRequest)
        def servletRequestMock = Mock(HttpServletRequest)
        def map = Mock(Map)
        requestParamHelper.getRequestParameterValues(servletRequestMock) >> map

        map.get("filter") >> "filter"
        map.get("sortBy") >> "sortBy"
        map.get("sortOrder") >> "sortOrder"
        map.get("count") >> 10
        map.get("startIndex") >> 1

        def scimSearchResultMock = Mock(SCIMSearchResult)
        def set = ["schemas"] as Set
        provisioning.search("filter", "sortBy", "sortOrder", 10, 1) >> scimSearchResultMock
        scimSearchResultMock.getSchemas() >> set

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        underTest.searchWithPost(servletRequestMock)

        then:
        mapping.value() == ["/.search"]
        mapping.method() == [RequestMethod.POST]
        body
        1 * attributesRemovalHelper.removeSpecifiedAttributes(scimSearchResultMock, map)
    }
}