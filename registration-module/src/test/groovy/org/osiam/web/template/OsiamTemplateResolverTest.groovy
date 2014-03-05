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

package org.osiam.web.template

import org.apache.commons.io.IOUtils;
import org.osiam.resources.scim.User
import org.osiam.web.exception.OsiamException
import org.osiam.web.template.OsiamResourceResolver;

import spock.lang.Specification

class OsiamTemplateResolverTest extends Specification {

    def 'could not find template by irrelevant name and throw exception'() {
        given:
        OsiamResourceResolver resourceResolver = new OsiamResourceResolver()
        def templateName = 'irrelevant'

        when:
        resourceResolver.getResourceAsStream(null, templateName)

        then:
        thrown(OsiamException)
    }
    
    def 'could find default registration template by existing template and irrelevant locale'() {
        given:
        OsiamResourceResolver resourceResolver = new OsiamResourceResolver('irrelevant')
        def templateName = 'registration-module/template/mail/registration'
        
        when:
        InputStream content = resourceResolver.getResourceAsStream(null, templateName)
        
        then:
        content != null
        String templateContent = IOUtils.toString(content, "UTF-8");
        templateContent.contains('your account has been created.')
    }
    
    def 'could find default registration template by existing template and de locale'() {
        given:
        OsiamResourceResolver resourceResolver = new OsiamResourceResolver('de')
        def templateName = 'registration-module/template/mail/registration'
        
        when:
        InputStream content = resourceResolver.getResourceAsStream(null, templateName)
        
        then:
        content != null
        String templateContent = IOUtils.toString(content, "UTF-8");
        templateContent.contains('ihr Account wurde erstellt.')
    }
    
    def 'could find default changeemail template by existing template and irrelevant locale'() {
        given:
        OsiamResourceResolver resourceResolver = new OsiamResourceResolver('irrelevant')
        def templateName = 'registration-module/template/mail/changeemail'
        
        when:
        InputStream content = resourceResolver.getResourceAsStream(null, templateName)
        
        then:
        content != null
        String templateContent = IOUtils.toString(content, "UTF-8");
        templateContent.contains('to change your e-mail address, please click the link below:')
    }
    
    def 'could find default changeemail template by existing template and de locale'() {
        given:
        OsiamResourceResolver resourceResolver = new OsiamResourceResolver('de')
        def templateName = 'registration-module/template/mail/changeemail'
        
        when:
        InputStream content = resourceResolver.getResourceAsStream(null, templateName)
        
        then:
        content != null
        String templateContent = IOUtils.toString(content, "UTF-8");
        templateContent.contains('um Ihre Email-Adresse zu ändern, klicken Sie bitte auf den nachfolgenden Link:')
    }
    
    def 'could find default lostpassword template by existing template and irrelevant locale'() {
        given:
        OsiamResourceResolver resourceResolver = new OsiamResourceResolver('irrelevant')
        def templateName = 'registration-module/template/mail/lostpassword'
        
        when:
        InputStream content = resourceResolver.getResourceAsStream(null, templateName)
        
        then:
        content != null
        String templateContent = IOUtils.toString(content, "UTF-8");
        templateContent.contains('to reset your password, please click the link below:')
    }
    
    def 'could find default lostpassword template by existing template and de locale'() {
        given:
        OsiamResourceResolver resourceResolver = new OsiamResourceResolver('de')
        def templateName = 'registration-module/template/mail/lostpassword'
        
        when:
        InputStream content = resourceResolver.getResourceAsStream(null, templateName)
        
        then:
        content != null
        String templateContent = IOUtils.toString(content, "UTF-8");
        templateContent.contains('um ihr Passwort zurückzusetzen, klicken Sie bitte auf den nachfolgenden Link:')
    }
    
    def 'could find default changeemailinfo template by existing template and irrelevant locale'() {
        given:
        OsiamResourceResolver resourceResolver = new OsiamResourceResolver('irrelevant')
        def templateName = 'registration-module/template/mail/changeemailinfo'
        
        when:
        InputStream content = resourceResolver.getResourceAsStream(null, templateName)
        
        then:
        content != null
        String templateContent = IOUtils.toString(content, "UTF-8");
        templateContent.contains('your e-mail address has been changed successfully.')
    }
    
    def 'could find default changeemailinfo template by existing template and de locale'() {
        given:
        OsiamResourceResolver resourceResolver = new OsiamResourceResolver('de')
        def templateName = 'registration-module/template/mail/changeemailinfo'
        
        when:
        InputStream content = resourceResolver.getResourceAsStream(null, templateName)
        
        then:
        content != null
        String templateContent = IOUtils.toString(content, "UTF-8");
        templateContent.contains('Ihre Email-Adresse wurde erfolgreich geändert.')
    }
}