package org.osiam.web.util

import org.osiam.resources.scim.Email
import org.osiam.resources.scim.User

import spock.lang.Specification

import com.google.common.base.Optional

class RegistrationHelperTest extends Specification {

    def 'getting primary email from user'() {
        given:
        def thePrimaryMail = 'primary@mail.com'

        Email theEmail = new Email.Builder().setPrimary(true).setValue(thePrimaryMail).build()
        User user = new User.Builder('theMan').setEmails([theEmail] as List).build()

        when:
        Optional<String> email = RegistrationHelper.extractSendToEmail(user)

        then:
        email.isPresent()
        email.get() == thePrimaryMail
    }

    def 'should return not null if no primary email was found'() {
        given:
        def thePrimaryMail = 'primary@mail.com'

        Email theEmail = new Email.Builder().setPrimary(false).setValue(thePrimaryMail).build()
        User user = new User.Builder('theMan').setEmails([theEmail] as List).build()

        when:
        Optional<String> email = RegistrationHelper.extractSendToEmail(user)

        then:
        email.isPresent()
        email.get() == 'primary@mail.com'
    }

    def 'should not throw exception if users emails are not present'() {
        given:
        User user = new User.Builder('theMan').build()

        when:
        Optional<String> email = RegistrationHelper.extractSendToEmail(user)

        then:
        !email.isPresent()
    }
    
    def 'should replace old primary email'() {
        given:
        def newPrimaryMail = 'newprimary@mail.com'
        Email oldPrimaryEmail = new Email.Builder().setPrimary(true).setValue('primary@mail.com').build()
        User user = new User.Builder('theMan').setEmails([oldPrimaryEmail] as List).build()
        
        when:
        List<Email> emails = RegistrationHelper.replaceOldPrimaryMail(newPrimaryMail, user.emails)
        
        then:
        emails.find { it.value == newPrimaryMail }
    }
    
    def 'should create a link'() {
        given:
        def linkPrefix = 'http://www.example.com/'
        def userId = 'cef9452e-00a9-4cec-a086-d171374febef'
        def parameterName = 'irrelevant'
        def parameter = 'parameter'
        
        when:
        def link = RegistrationHelper.createLinkForEmail(linkPrefix, userId, parameterName, parameter)
        
        then:
        link.contains(linkPrefix)
        link.contains(userId)
        link.contains(parameterName)
        link.contains(parameter)
    }
}
