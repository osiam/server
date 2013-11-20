package org.osiam.web.util

import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.User
import org.spockframework.util.Assert
import spock.lang.Specification

import javax.mail.Message
import javax.mail.internet.MimeMessage

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
        def content = new ByteArrayInputStream("please please please! \$FIRSTNAME \$LASTNAME".bytes)

        def contentVars = ["\$FIRSTNAME": "donald", "\$LASTNAME": "duck"]

        when:
        mailSender.sendMail("donald.duck@example.org", "uncle.scroogle@example.org", "need money", content, contentVars);

        then:
        1 * mailSender.transportMail(_) >> { MimeMessage message ->
            Assert.that(message.getFrom()[0].toString().equals("donald.duck@example.org"), "from dont match!" )
            Assert.that(message.getRecipients(Message.RecipientType.TO)[0].toString().equals("uncle.scroogle@example.org"), "to dont match!" )
            Assert.that(message.getSubject().equals("need money"), "subject dont match!")
            Assert.that("please please please! donald duck".equals(message.getContent()), "content dont match")
        }

    }

    def "getting primary email from user"() {
        given:
        def mailSender = new MailSender()
        def thePrimaryMail = "primary@mail.com"

        def theEmail = new MultiValuedAttribute.Builder().setPrimary(true).setValue(thePrimaryMail).build()
        def user = new User.Builder("theMan").setEmails([theEmail] as List).build()

        when:
        def email = mailSender.extractPrimaryEmail(user)

        then:
        email == thePrimaryMail
    }
}
