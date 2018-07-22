package io.orvisual.api.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;

/**
 * <p>
 * Created on 25.05.2018.
 * </p>
 *
 * This test of {@link FileSystemPictureStorageServiceFactoryBean}
 *
 * @author Artemis A. Sirosh
 */
public class FileSystemPictureStorageServiceFactoryBeanTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path galleryPath;

    @Before
    public void setUpEnvironment() throws Exception {
        this.galleryPath = Paths.get(temporaryFolder.newFolder().toURI()).resolve("gallery");
        this.environmentVariables.set("GALLERY_DIR", this.galleryPath.toString());
    }

    @Test(expected = BeanCreationException.class)
    public void shouldDetectNotExistentGalleryDirectory() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(FileSystemPictureStorageServiceFactoryBean.class);
            context.refresh();
        }
    }

    @Test(expected = BeanCreationException.class)
    public void shouldRejectFileAsGallery() throws IOException {
        Files.createFile(galleryPath);
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(FileSystemPictureStorageServiceFactoryBean.class);
            context.refresh();
        }
    }

    @Test
    public void shouldCreateFileStorageService() throws IOException {
        Files.createDirectory(galleryPath);
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(FileSystemPictureStorageServiceFactoryBean.class);
            context.refresh();

            assertNotNull(context.getBean(FileSystemPictureStorageServiceFactoryBean.class));
        }
    }
}