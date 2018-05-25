package io.orvisual.api.service;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Created on 25.05.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
public class FileStorageServiceFactoryBean extends AbstractFactoryBean<FileStorageService> {

    public FileStorageServiceFactoryBean(Environment environment) {

    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    protected FileStorageService createInstance() throws Exception {
        return null;
    }
}
