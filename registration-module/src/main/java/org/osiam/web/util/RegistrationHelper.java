package org.osiam.web.util;

import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.User;

import com.google.common.base.Optional;

public class RegistrationHelper {

    public static Optional<String> extractSendToEmail(User user) {
        for (Email email : user.getEmails()) {
            if (email.isPrimary()) {
                return Optional.of(email.getValue());
            }
        }
        
        if(user.getEmails().size() > 0){
            return Optional.of(user.getEmails().get(0).getValue());
        }
        return Optional.absent();
    }
}
