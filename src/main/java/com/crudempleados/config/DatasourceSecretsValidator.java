package com.crudempleados.config;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DatasourceSecretsValidator {

    private static final String[] REQUIRED_PROPERTIES = {
        "CRUD_DB_HOST",
        "CRUD_DB_PORT",
        "CRUD_DB_NAME",
        "CRUD_DB_USER",
        "CRUD_DB_PASSWORD"
    };

    private final Environment environment;

    public DatasourceSecretsValidator(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    void validate() {
        List<String> missing = new ArrayList<>();
        for (String key : REQUIRED_PROPERTIES) {
            String value = environment.getProperty(key);
            if (!StringUtils.hasText(value)) {
                missing.add(key);
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing required datasource secret(s): " + String.join(", ", missing));
        }
    }
}
