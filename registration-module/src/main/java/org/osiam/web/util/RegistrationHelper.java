package org.osiam.web.util;

import java.util.ArrayList;
import java.util.List;

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
    
    public static List<Email> replaceOldPrimaryMail(String newEmail, List<Email> emails) {

        List<Email> updatedEmailList = new ArrayList<>();

        // add new primary email address
        updatedEmailList.add(new Email.Builder()
                .setValue(newEmail)
                .setPrimary(true)
                .build());

        // add only non primary mails to new list and remove all primary entries
        for (Email mail : emails) {
            if (mail.isPrimary()) {
                updatedEmailList.add(new Email.Builder().setType(mail.getType())
                        .setPrimary(mail.isPrimary())
                        .setValue(mail.getValue()).setOperation("delete").build());
            } else {
                updatedEmailList.add(mail);
            }
        }

        return updatedEmailList;
    }
}
