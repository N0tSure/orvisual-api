package io.orvisual.api.service;

import com.amazonaws.regions.Regions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * <p>
 * Created on 25.07.2018.
 * </p>
 *
 * Test for {@link AmazonS3PictureStorageServiceFactoryBean}.
 *
 * @author Artemis A. Sirosh
 */
@RunWith(JUnit4.class)
public class AmazonS3PictureStorageServiceFactoryBeanTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private AnnotationConfigApplicationContext applicationContext;

    @After
    public void closeAppContext() {
        applicationContext.close();
    }

    @Before
    public void setUpAppContext() {
        applicationContext = new AnnotationConfigApplicationContext();
    }

    @Test
    public void shouldCreateServiceComponentInstance() {
        environmentVariables.set("AWS_ACCESS_KEY_ID", "id");
        environmentVariables.set("AWS_SECRET_ACCESS_KEY", "key");
        environmentVariables.set("AWS_REGION", Regions.US_EAST_1.getName());
        environmentVariables.set("AWS_BUCKET_NAME", "test");

        registerFactoryBeanAndRefresh(AmazonS3PictureStorageServiceFactoryBean.class);
    }

    @Test(expected = BeanCreationException.class)
    public void shouldDetectAbsenceOfAWSKey() {
        environmentVariables.set("AWS_SECRET_ACCESS_KEY", "key");
        environmentVariables.set("AWS_REGION", Regions.US_EAST_1.getName());
        environmentVariables.set("AWS_BUCKET_NAME", "test");

        registerFactoryBeanAndRefresh(AmazonS3PictureStorageServiceFactoryBean.class);
    }

    @Test(expected = BeanCreationException.class)
    public void shouldDetectAbsenceOfAWSAccessKey() {
        environmentVariables.set("AWS_ACCESS_KEY_ID", "id");
        environmentVariables.set("AWS_REGION", Regions.US_EAST_1.getName());
        environmentVariables.set("AWS_BUCKET_NAME", "test");

        registerFactoryBeanAndRefresh(AmazonS3PictureStorageServiceFactoryBean.class);
    }

    @Test(expected = BeanCreationException.class)
    public void shouldDetectAbsenceOfAWSRegion() {
        environmentVariables.set("AWS_ACCESS_KEY_ID", "id");
        environmentVariables.set("AWS_SECRET_ACCESS_KEY", "key");
        environmentVariables.set("AWS_BUCKET_NAME", "test");

        registerFactoryBeanAndRefresh(AmazonS3PictureStorageServiceFactoryBean.class);
    }

    @Test(expected = BeanCreationException.class)
    public void shouldRejectInvalidAWSRegion() {
        environmentVariables.set("AWS_ACCESS_KEY_ID", "id");
        environmentVariables.set("AWS_SECRET_ACCESS_KEY", "key");
        environmentVariables.set("AWS_REGION", "э,слышь");
        environmentVariables.set("AWS_BUCKET_NAME", "test");

        registerFactoryBeanAndRefresh(AmazonS3PictureStorageServiceFactoryBean.class);
    }

    @Test(expected = BeanCreationException.class)
    public void shouldDetectAbsenceOfAWSS3BucketName() {
        environmentVariables.set("AWS_ACCESS_KEY_ID", "id");
        environmentVariables.set("AWS_SECRET_ACCESS_KEY", "key");
        environmentVariables.set("AWS_REGION", Regions.US_EAST_1.getName());

        registerFactoryBeanAndRefresh(AmazonS3PictureStorageServiceFactoryBean.class);

    }

    private void registerFactoryBeanAndRefresh(Class<?> factoryBeanClass) {
        applicationContext.registerBean(factoryBeanClass);
        applicationContext.refresh();
    }
}
