package org.osiam.web.util

import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.User
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * Test for MailSender class.
 * @author Jochen Todea
 */
class MailSenderBeanTest extends Specification {

    def springMailSenderMock = Mock(MailSender)
    def contextMock = Mock(ServletContext)

    def inputStream = new ByteArrayInputStream("the email content".bytes)

    def mailSenderBean = new MailSenderBean(mailSender: springMailSenderMock)

    def "Sends a test-mail"() {
        given:
        def content = new ByteArrayInputStream("please please please! \$FIRSTNAME \$LASTNAME".bytes)
        def contentVars = ["\$FIRSTNAME": "donald", "\$LASTNAME": "duck"]

        when:
        mailSenderBean.sendMail("donald.duck@example.org", "uncle.scroogle@example.org", "need money", content, contentVars)

        then:
        1 * springMailSenderMock.send(_)
    }

    def "should construct a proper message with given parameters"() {
        when:
        SimpleMailMessage message = mailSenderBean.getMessage("from@address.de", "to@address", "subject", "This is the mail content.")

        then:
        message.getFrom().equals("from@address.de")
        message.getTo().size() == 1
        message.getTo()[0].equals("to@address")
        message.getSubject().equals("subject")
        message.getText().equals("This is the mail content.")
        message.getSentDate() != null
    }

    def "should replace the placeholder in stream content and return as string"() {
        given:
        def content = new ByteArrayInputStream("please please please! \$FIRSTNAME \$LASTNAME".bytes)
        def contentVars = ["\$FIRSTNAME": "donald", "\$LASTNAME": "duck"]

        when:
        String contentAsString = mailSenderBean.getMailContentWithReplacementsAsString(content, contentVars)

        then:
        contentAsString.equals("please please please! donald duck")
    }

    def "getting primary email from user"() {
        given:
        def thePrimaryMail = "primary@mail.com"

        def theEmail = new MultiValuedAttribute.Builder().setPrimary(true).setValue(thePrimaryMail).build()
        def user = new User.Builder("theMan").setEmails([theEmail] as List).build()

        when:
        def email = mailSenderBean.extractPrimaryEmail(user)

        then:
        email == thePrimaryMail
    }

    def "should return null if no primary email was found"() {
        given:
        def thePrimaryMail = "primary@mail.com"

        def theEmail = new MultiValuedAttribute.Builder().setPrimary(false).setValue(thePrimaryMail).build()
        def user = new User.Builder("theMan").setEmails([theEmail] as List).build()

        when:
        def email = mailSenderBean.extractPrimaryEmail(user)

        then:
        email == null
    }

    def "should not throw exception if users emails are not present"() {
        given:
        def user = new User.Builder("theMan").build()

        when:
        def email = mailSenderBean.extractPrimaryEmail(user)

        then:
        email == null
    }

    def "should read the email content from default path if user defined path is null"() {
        when:
        def result = mailSenderBean.getEmailContentAsStream("defaultPath", null, contextMock)

        then:
        1 * contextMock.getResourceAsStream("defaultPath") >> inputStream
        result == inputStream
    }

    def "should read the email content from default path if user defined path is empty"() {
        when:
        def result = mailSenderBean.getEmailContentAsStream("defaultPath", "", contextMock)

        then:
        1 * contextMock.getResourceAsStream("defaultPath") >> inputStream
        result == inputStream
    }

    def "should read the email content from user defined path if it is not null"() {
        given:
        def url = this.getClass().getResource("/test-content.txt")

        when:
        def result = mailSenderBean.getEmailContentAsStream("defaultPath", url.getFile(), contextMock)

        then:
        def fileAsString = getStringFromStream(result)
        fileAsString == "Just a test!"
    }

    def getStringFromStream(result) {
        def builder = new StringBuilder()
        int ch
        while((ch = result.read()) != -1){
            builder.append((char)ch)
        }

        return builder.toString()
    }
}