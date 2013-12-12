package org.osiam.helper;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.User;
import org.springframework.stereotype.Component;

/**
 * Class extending jackson's object mapper to be able to add deserializer configuration for scim user with extensions.
 * User: Jochen Todea
 * Date: 26.11.13
 * Time: 15:33
 * Created: with Intellij IDEA
 */
@Component
public class ObjectMapperWithExtensionConfig extends ObjectMapper {

    private static final long serialVersionUID = -1206817582445889248L;

    /**
     * Building jackson's object mapper and register a module with a default deserializer for scim user with extensions.
     */
    public ObjectMapperWithExtensionConfig() {
        super();
        SimpleModule userDeserializerModule = new SimpleModule("userDeserializerModule", Version.unknownVersion())
                .addDeserializer(User.class, new UserDeserializer(User.class));
        registerModule(userDeserializerModule);
    }
}