package org.osiam.resources.exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class was written to transform enum error messages from:
 * <p/>
 * No enum constant org.osiam.storage.entities.(\w+)Entity.[\w]+.(\w+)
 * <p/>
 * to
 * <p/>
 * \1 is not a valid \2 type
 */
public class TypeErrorMessageTransformer implements ErrorMessageTransformer {
    private static Pattern pattern =
            Pattern.compile("No enum constant org\\.osiam\\.storage\\.entities\\.(\\w+)Entity\\.[\\w]+.(\\w+)");

    @Override
    public String transform(String message) {
        if (message == null)
            return null;
        Matcher matcher = pattern.matcher(message);
        if (matcher.matches()) {
            return matcher.group(2) + " is not a valid " + matcher.group(1) + " type";
        }
        return message;
    }
}
