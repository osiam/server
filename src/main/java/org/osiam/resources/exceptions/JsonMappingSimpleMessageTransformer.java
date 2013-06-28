package org.osiam.resources.exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 28.06.13
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
public class JsonMappingSimpleMessageTransformer implements ErrorMessageTransformer {

    private static Pattern pattern = Pattern.compile("^([^']*'[^']*')");

    @Override
    public String transform(String message) {
        if (message == null)
            return null;
        Matcher matcher = pattern.matcher(message);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return message;
    }
}