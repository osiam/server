package org.osiam.web.util

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.osiam.helper.HttpClientHelper
import org.osiam.resources.helper.UserDeserializer
import org.osiam.resources.scim.User
import org.osiam.web.controller.RegisterController
import org.spockframework.util.Assert
import spock.lang.Shared
import spock.lang.Specification

import javax.mail.Message
import javax.mail.internet.MimeMessage
import javax.servlet.ServletContext
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * User: Igor
 * Date: 19.11.13
 * Time: 09:13
 * To change this template use File | Settings | File Templates.
 */
class MailSenderTest extends Specification {
        def "Sends a test-mail"() {
            given:
            def mailSender = Spy(MailSender)

            when:
            mailSender.sendMail("donald.duck@example.org", "uncle.scroogle@example.org", "need money", "please please please!");

            then:
            1 * mailSender.transportMail(_) >> { MimeMessage message ->
                Assert.that(message.getFrom()[0].toString().equals("donald.duck@example.org"), "from dont match!" )
                Assert.that(message.getRecipients(Message.RecipientType.TO)[0].toString().equals("uncle.scroogle@example.org"), "to dont match!" )
                Assert.that(message.getSubject().equals("need money"), "subject dont match!")
                Assert.that("please please please!".equals(message.getContent()), "content dont match")
            }

        }
}
