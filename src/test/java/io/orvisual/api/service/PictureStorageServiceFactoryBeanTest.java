package io.orvisual.api.service;

import com.amazonaws.regions.Regions;
import org.junit.After;
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
 * Created on 12.09.2018.
 * </p>
 *
 * Test for {@link PictureStorageServiceFactoryBean}
 *
 * @author Artemis A. Sirosh
 */
public class PictureStorageServiceFactoryBeanTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private AnnotationConfigApplicationContext applicationContext;
    private Path galleryPath;

    @After
    public void closeAppContext() {
        this.applicationContext.close();
    }

    @Before
    public void setUp() throws IOException {
        this.applicationContext = new AnnotationConfigApplicationContext();
        this.galleryPath = Paths.get(temporaryFolder.newFolder().toURI()).resolve("gallery");
    }

    @Test
    public void shouldCreateFileSystemPictureStorageService() throws IOException {
        this.environmentVariables.set("GALLERY_DIR", this.galleryPath.toString());
        Files.createDirectory(galleryPath);

        registerFactoryBeanAndRefresh();
        assertNotNull(applicationContext.getBean(PictureStorageService.class));
    }

    @Test(expected = BeanCreationException.class)
    public void shouldRejectFileAsGallery() throws IOException {
        
        this.environmentVariables.set("GALLERY_DIR", this.galleryPath.toString());
        Files.createFile(galleryPath);
        registerFactoryBeanAndRefresh();
    }

    @Test(expected = BeanCreationException.class)
    public void shouldDetectNotExistentGalleryDirectory() {

        this.environmentVariables.set("GALLERY_DIR", this.galleryPath.toString());
        registerFactoryBeanAndRefresh();
    }

    @Test
    public void shouldCreateAmazonS3PictureStorageService() {
        environmentVariables.set("AWS_ACCESS_KEY_ID", "id");
        environmentVariables.set("AWS_SECRET_ACCESS_KEY", "key");
        environmentVariables.set("AWS_REGION", Regions.US_EAST_1.getName());
        environmentVariables.set("AWS_BUCKET_NAME", "test");

        registerFactoryBeanAndRefresh();
        assertNotNull(applicationContext.getBean(PictureStorageService.class));
    }

    @Test(expected = BeanCreationException.class)
    public void shouldDetectAbsenceOfAWSKey() {
        environmentVariables.set("AWS_SECRET_ACCESS_KEY", "key");
        environmentVariables.set("AWS_REGION", Regions.US_EAST_1.getName());
        environmentVariables.set("AWS_BUCKET_NAME", "test");

        registerFactoryBeanAndRefresh();
    }

    @Test(expected = BeanCreationException.class)
    public void shouldDetectAbsenceOfAWSAccessKey() {
        environmentVariables.set("AWS_ACCESS_KEY_ID", "id");
        environmentVariables.set("AWS_REGION", Regions.US_EAST_1.getName());
        environmentVariables.set("AWS_BUCKET_NAME", "test");

        registerFactoryBeanAndRefresh();
    }

    @Test(expected = BeanCreationException.class)
    public void shouldDetectAbsenceOfAWSRegion() {
        environmentVariables.set("AWS_ACCESS_KEY_ID", "id");
        environmentVariables.set("AWS_SECRET_ACCESS_KEY", "key");
        environmentVariables.set("AWS_BUCKET_NAME", "test");

        registerFactoryBeanAndRefresh();
    }

    @Test(expected = BeanCreationException.class)
    public void shouldRejectInvalidAWSRegion() {
        environmentVariables.set("AWS_ACCESS_KEY_ID", "id");
        environmentVariables.set("AWS_SECRET_ACCESS_KEY", "key");
        environmentVariables.set("AWS_REGION", "э,слышь");
        environmentVariables.set("AWS_BUCKET_NAME", "test");

        registerFactoryBeanAndRefresh();
    }

    @Test(expected = BeanCreationException.class)
    public void shouldDetectAbsenceOfAWSS3BucketName() {
        environmentVariables.set("AWS_ACCESS_KEY_ID", "id");
        environmentVariables.set("AWS_SECRET_ACCESS_KEY", "key");
        environmentVariables.set("AWS_REGION", Regions.US_EAST_1.getName());

        registerFactoryBeanAndRefresh();
    }

    private void registerFactoryBeanAndRefresh() {
        applicationContext.registerBean(PictureStorageServiceFactoryBean.class);
        applicationContext.refresh();
    }
}
