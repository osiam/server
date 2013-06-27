package org.osiam.ng.resourceserver

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.osiam.ng.resourceserver.dao.SCIMSearchResult
import org.osiam.ng.scim.dao.SCIMUserProvisioning
import org.osiam.resources.helper.JsonResponseEnrichHelper
import org.osiam.resources.helper.RequestParamHelper
import org.osiam.resources.controller.UserController
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.User
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 23.05.13
 * Time: 12:48
 * To change this template use File | Settings | File Templates.
 */
class IsoDateFormatSpec extends Specification{

    def provisioning = Mock(SCIMUserProvisioning)
    def userController = new UserController(scimUserProvisioning: provisioning, requestParamHelper: new RequestParamHelper(),
            jsonResponseEnrichHelper: new JsonResponseEnrichHelper())
    def servletRequestMock = Mock(HttpServletRequest)

    def "should return the date fields in iso format"() {
        given:
        def actualDate = GregorianCalendar.getInstance().getTime()
        def dateTimeFormatter = ISODateTimeFormat.dateTime();
        def date = new DateTime(actualDate)
        def created = dateTimeFormatter.print(date)

        def user = new User.Builder("username").setMeta(new Meta.Builder(actualDate, null).build()).build()
        def scimSearchResult = new SCIMSearchResult([user], 23)

        when:
        def result = userController.searchWithGet(servletRequestMock)

        then:
        2 * servletRequestMock.getParameter("attributes") >> "meta.created"
        1 * provisioning.search(_, _, _, _, _) >> scimSearchResult

        result == '{"totalResults":23,"itemsPerPage":100,"startIndex":0,"schemas":"urn:scim:schemas:core:1.0","Resources":[{"meta":{"created":"' + created + '"}}]}'
    }
}