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
 * This {@link org.springframework.beans.factory.FactoryBean} instantiate
 * {@link FileSystemPictureStorageService} using {@link Environment} to
 * configure bean. For successful configuring {@link Environment} should contain
 * property {@literal GALLERY_DIR}. This property is a name of gallery path, if
 * this path not exists or not a directory instantiation will failed.
 *
 * @see org.springframework.beans.factory.FactoryBean
 * @see Environment
 * @author Artemis A. Sirosh
 */
@Component
public class FileSystemPictureStorageServiceFactoryBean extends AbstractFactoryBean<FileSystemPictureStorageService>
        implements EnvironmentAware {

    private Environment environment;

    @Autowired
    public FileSystemPictureStorageServiceFactoryBean() {
        super();
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public Class<?> getObjectType() {
        return FileSystemPictureStorageService.class;
    }

    /**
     * {@inheritDoc}
     *
     * Creates instance of {@link FileSystemPictureStorageService}
     * using {@link Environment}.
     *
     * @return instance of {@link FileSystemPictureStorageService}.
     * @throws IllegalStateException if {@literal GALLERY_DIR} missing in
     * {@link Environment} or path of gallery not exists or not a directory.
     */
    @Override
    @NonNull
    protected FileSystemPictureStorageService createInstance() {
        Path galleryPath = Paths.get(environment.getRequiredProperty("GALLERY_DIR"));
        checkState(Files.exists(galleryPath), "Directory '%s' not exists", galleryPath);
        checkState(Files.isDirectory(galleryPath), "File item '%s' not directory", galleryPath);
        checkState(Files.isWritable(galleryPath), "Directory '%s' not writable", galleryPath);

        return new FileSystemPictureStorageService(galleryPath);
    }
}
