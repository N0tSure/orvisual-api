package io.orvisual.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkState;

/**
 * <p>
 * Created on 25.05.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
@Component
public class FileStorageServiceFactoryBean extends AbstractFactoryBean<FileStorageService> implements EnvironmentAware {

    private Environment environment;

    @Autowired
    public FileStorageServiceFactoryBean() {
        super();
    }

    @Override
    public void setEnvironment(@SuppressWarnings("NullableProblems") Environment environment) {
        this.environment = environment;
    }

    @Override
    public Class<?> getObjectType() {
        return FileStorageService.class;
    }

    @Override
    @NonNull
    protected FileStorageService createInstance() {
        Path galleryPath = Paths.get(environment.getRequiredProperty("GALLERY_DIR"));
        checkState(Files.exists(galleryPath), "Directory '%s' not exists", galleryPath);
        checkState(Files.isDirectory(galleryPath), "File item '%s' not directory", galleryPath);
        checkState(Files.isWritable(galleryPath), "Directory '%s' not writable", galleryPath);

        return new FileStorageService(galleryPath);
    }
}
