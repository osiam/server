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

package org.osiam.web.service

import javax.servlet.ServletContext

import org.osiam.resources.scim.User
import org.osiam.web.exception.OsiamException
import org.springframework.web.context.support.ServletContextAttributeExporter;
import org.thymeleaf.spring4.SpringTemplateEngine
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

import spock.lang.Specification

class TemplateRendererTest extends Specification {

    ServletContext context = Mock();
    
    TemplateRenderer template = new TemplateRenderer(servletContext: context)
    
    def 'could not find template by irrelevant name and throw exception'() {
        given:
        User user = new User.Builder('username').build()
        def templateName = 'irrelevant'
        
        when:
        def content = template.renderTemplate(templateName, user, [:])
        
        then:
        context.getRealPath(_) >> 'irrelevant'
        thrown(OsiamException)
    }
}