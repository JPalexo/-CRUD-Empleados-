package com.crudempleados.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DatasourceSecretsValidator implements BeanFactoryPostProcessor, EnvironmentAware {

    private static final String[] REQUIRED_PROPERTIES = {
        "CRUD_DB_HOST",
        "CRUD_DB_PORT",
        "CRUD_DB_NAME",
        "CRUD_DB_USER",
        "CRUD_DB_PASSWORD"
    };

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        validate();
    }

    private void validate() {
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
