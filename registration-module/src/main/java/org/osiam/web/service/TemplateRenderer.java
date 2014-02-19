package org.osiam.web.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.osiam.resources.scim.User;
import org.osiam.web.exception.OsiamException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

@Service
public class TemplateRenderer {

    @Inject
    private SpringTemplateEngine templateEngine;
    
    @Inject
    private ServletContext servletContext;

    public String renderTemplate(String templateName, User user, Map<String, String> attributes) {
        final Context ctx = new Context();
        ctx.setVariable("user", user);
        ctx.setVariables(attributes);
        return this.templateEngine.process(getCompleteTemplateFileName(templateName, user), ctx);
    }

    private String getCompleteTemplateFileName(String templateNamePrefix, User user) {
        
        Optional<String> templateFilePath = findTemplate(templateNamePrefix, user.getLocale(), false);
        if (templateFilePath.isPresent()) {
            return templateFilePath.get();
        }
        
        templateFilePath = findTemplate(templateNamePrefix, false);
        if (templateFilePath.isPresent()) {
            return templateFilePath.get();
        }
        
        templateFilePath = findTemplate(templateNamePrefix, user.getLocale(), true);
        if (templateFilePath.isPresent()) {
            return templateFilePath.get();
        }
        
        templateFilePath = findTemplate(templateNamePrefix, true);
        if (templateFilePath.isPresent()) {
            return templateFilePath.get();
        }
        
        throw new OsiamException("Could not find the " + templateNamePrefix + " template.");
    }
    
    private Optional<String> findTemplate(String templateNamePrefix, boolean isDefault) {
        StringBuffer templateName = new StringBuffer(templateNamePrefix);
        if(isDefault) {
            templateName.append("-default");
        }
        return templateFilePath(templateName.toString());
    }
    
    private Optional<String> findTemplate(String templateNamePrefix, String locale, boolean isDefault) {
        if (!Strings.isNullOrEmpty(locale)) {
            StringBuffer templateName = new StringBuffer(templateNamePrefix);
            if(isDefault) {
                templateName.append("-default");
            }
            templateName.append("-" + locale);
            return templateFilePath(templateName.toString());
        }
        return Optional.absent();
    }
    
    private Optional<String> templateFilePath(String templateName) {
        templateName += ".html";
        String pathString = "WEB-INF/classes/template/mail/" + templateName;
        Path path = Paths.get(servletContext.getRealPath(pathString));
        if (Files.exists(path)) {
            return Optional.of(templateName);
        }
        return Optional.absent();
    }
}