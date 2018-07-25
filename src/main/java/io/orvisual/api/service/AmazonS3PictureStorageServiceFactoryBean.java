package io.orvisual.api.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Region;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

/**
 * <p>
 * Created on 25.07.2018.
 * </p>
 *
 * This component instantiate {@link AmazonS3PictureStorageService} using for
 * configuring {@link Environment}. For successful instantiation of a
 * {@link AmazonS3PictureStorageService} {@link Environment} should contain
 * following properties:
 *
 * <ul>
 *     <li>{@literal AWS_ACCESS_KEY_ID} - AWS access key id.</li>
 *     <li>{@literal AWS_SECRET_ACCESS_KEY} - AWS access secret key.</li>
 *     <li>
 *         {@literal AWS_REGION} - AWS Region, where to store Amazon S3 data,
 *         should one of {@link Regions}.
 *     </li>
 *     <li>
 *         {@literal AWS_BUCKET_NAME} - Amazon S3 bucket name for storing data.
 *     </li>
 * </ul>
 *
 * @author Artemis A. Sirosh
 */
public class AmazonS3PictureStorageServiceFactoryBean extends
        AbstractFactoryBean<AmazonS3PictureStorageService> implements EnvironmentAware {

    private Environment environment;

    @Override
    public Class<?> getObjectType() {
        return AmazonS3PictureStorageService.class;
    }

    /**
     * {@inheritDoc}
     *
     * Instate {@link AmazonS3PictureStorageService} using a {@link Environment}.
     *
     * @return {@link AmazonS3PictureStorageService} instance.
     * @throws IllegalStateException if some property missing in {@link Environment}.
     */
    @Override
    @NonNull
    protected AmazonS3PictureStorageService createInstance() {

        final AWSCredentials credentials = new BasicAWSCredentials(
                environment.getRequiredProperty("AWS_ACCESS_KEY_ID"),
                environment.getRequiredProperty("AWS_SECRET_ACCESS_KEY")
        );

        final Region region = Region.fromValue(environment.getRequiredProperty("AWS_REGION"));

        final AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region.getFirstRegionId())
                .build();

        return new AmazonS3PictureStorageService(
                amazonS3Client,
                environment.getRequiredProperty("AWS_BUCKET_NAME")
        );
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
