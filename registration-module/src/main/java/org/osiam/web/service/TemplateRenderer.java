package org.osiam.web.service;

import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.osiam.resources.scim.User;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service
public class TemplateRenderer {
    
    @Inject
    private SpringTemplateEngine templateEngine;
    
    public String renderTemplate(String templateName, Locale locale, User user, Map<String, String> attributes) {
        final Context ctx = new Context(locale);
        ctx.setVariable("user", user);
        ctx.setVariables(attributes);
        return this.templateEngine.process(templateName, ctx);
    }
}