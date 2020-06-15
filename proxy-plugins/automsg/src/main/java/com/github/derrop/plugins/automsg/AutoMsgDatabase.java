package com.github.derrop.plugins.automsg;

import com.github.derrop.proxy.api.database.DatabaseProvidedStorage;
import com.github.derrop.proxy.api.service.ServiceRegistry;

import java.util.UUID;

public class AutoMsgDatabase extends DatabaseProvidedStorage<String> {

    private static final String DEFAULT_MESSAGE = "Ich bin seit dem %date% (%min% Minuten) AFK!";

    public AutoMsgDatabase(ServiceRegistry registry) {
        super(registry, "auto_msg_data", String.class);
    }

    public String getDefaultMessage() {
        return DEFAULT_MESSAGE;
    }

    public String getMessage(UUID uniqueId) {
        String message = super.get(uniqueId.toString());
        if (message == null) {
            message = DEFAULT_MESSAGE;
            super.insert(uniqueId.toString(), message);
        }
        return message;
    }

    public void updateMessage(UUID uniqueId, String message) {
        super.insertOrUpdate(uniqueId.toString(), message);
    }

}
