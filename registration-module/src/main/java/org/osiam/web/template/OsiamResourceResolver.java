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

package org.osiam.web.template;

import java.io.InputStream;
import java.util.Locale;

import org.osiam.web.exception.OsiamException;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.Validate;

import com.google.common.base.Strings;

/**
 * Osiam resource resolver for thymeleaf template engine to receive input stream of requested template
 * 
 */
public class OsiamResourceResolver implements IResourceResolver {

    public static final String NAME = "OSIAM-RESOLVER";

    private String locale;

    public OsiamResourceResolver(String locale) {
        super();
        setLocale(locale);
    }

    private void setLocale(String locale) {
        if (!Strings.isNullOrEmpty(locale)) {
            this.locale = new Locale(locale).toLanguageTag();
            if (locale.equals("und")) { // Undetermined
                locale = null;
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters,
            String resourceName) {

        Validate.notNull(resourceName, "Resource name cannot be null");
        return getTemplateInputStream(resourceName);
    }

    private InputStream getTemplateInputStream(String templateNamePrefix) {
        String noLocale = null;
        boolean defaultTemplate = true;

        InputStream templateFilePath = getTemplateInputStream(templateNamePrefix, locale, !defaultTemplate);
        if (templateFilePath != null) {
            return templateFilePath;
        }

        templateFilePath = getTemplateInputStream(templateNamePrefix, noLocale, !defaultTemplate);
        if (templateFilePath != null) {
            return templateFilePath;
        }

        templateFilePath = getTemplateInputStream(templateNamePrefix, locale, defaultTemplate);
        if (templateFilePath != null) {
            return templateFilePath;
        }

        templateFilePath = getTemplateInputStream(templateNamePrefix, noLocale, defaultTemplate);
        if (templateFilePath != null) {
            return templateFilePath;
        }

        throw new OsiamException("Could not find the " + templateNamePrefix + " template.");
    }

    private InputStream getTemplateInputStream(String templateNamePrefix, String locale, boolean isDefault) {
        StringBuffer templateName = new StringBuffer(templateNamePrefix);
        if (isDefault) {
            templateName.append("-default");
        }
        if (!Strings.isNullOrEmpty(locale)) {
            templateName.append("-" + locale);
        }
        templateName.append(".html");
        return ClassLoaderUtils.getClassLoader(OsiamResourceResolver.class).getResourceAsStream(
                templateName.toString());
    }
}
