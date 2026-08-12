package com.BackEnd.Master.GYM.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Exposes the configured default avatar object key statically so MapStruct mappers
// (declarative interfaces, not easily constructor-injected) can reference it. The key
// is resolved to a full URL by StorageService.resolveUrl(), never hardcoded here.
@Component
public class DefaultAvatar {

    private static volatile String key;

    public DefaultAvatar(@Value("${app.default-avatar-key}") String configuredKey) {
        key = configuredKey;
    }

    public static String key() {
        return key;
    }
}
