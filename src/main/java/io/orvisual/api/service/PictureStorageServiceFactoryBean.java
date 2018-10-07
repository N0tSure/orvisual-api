package io.orvisual.api.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Region;
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
 * Created on 12.09.2018.
 * </p>
 * <p>
 *     Creates a new instance of {@link PictureStorageService}. Choose
 *     implementation by inspecting environment variables presence.
 *     <li>
 *         <ul>
 *             If {@literal GALLERY_DIR} variable presents, creates a new
 *             instance of {@link FileSystemPictureStorageService}.
 *         </ul>
 *         <ul>
 *             If {@literal GALLERY_DIR} not presented in {@link Environment},
 *             try to find {@literal AWS_ACCESS_KEY_ID},
 *             {@literal AWS_SECRET_ACCESS_KEY}, {@literal AWS_REGION} and
 *             {@literal AWS_BUCKET_NAME}. In case when just one of this
 *             environments not presented, it will fail bean instantiation.
 *         </ul>
 *     </li>
 * </p>
 *
 * @author Artemis A. Sirosh
 */
@Component
public class PictureStorageServiceFactoryBean extends
        AbstractFactoryBean<PictureStorageService> implements EnvironmentAware {

    private Environment environment;

    @Override
    public Class<?> getObjectType() {
        return PictureStorageService.class;
    }

    /**
     * {@inheritDoc}
     *
     * If {@literal GALLERY_DIR} presented in environment, this should be path
     * to directory and current user should have write permission to this
     * directory.
     *
     * In case when {@literal GALLERY_DIR} not presented in environment,
     * {@literal AWS_ACCESS_KEY_ID}, {@literal AWS_SECRET_ACCESS_KEY},
     * {@literal AWS_REGION} and {@literal AWS_BUCKET_NAME} should be
     * assigned. Also, {@literal AWS_REGION} should be one of AWS regions ids.
     *
     * @return instance of {@link PictureStorageService}
     * @throws IllegalStateException if any above condition not satisfied
     * @throws IllegalArgumentException if value {@literal AWS_REGION} not
     * valid
     * @see Region
     */
    @NonNull
    @Override
    protected PictureStorageService createInstance() {
        final String galleryPathName = environment.getProperty("GALLERY_DIR");
        if (galleryPathName != null) {

            Path galleryPath = Paths.get(galleryPathName);
            checkState(Files.exists(galleryPath), "Directory '%s' not exists", galleryPath);
            checkState(Files.isDirectory(galleryPath), "File item '%s' not directory", galleryPath);
            checkState(Files.isWritable(galleryPath), "Directory '%s' not writable", galleryPath);
            return new FileSystemPictureStorageService(galleryPath);
        }

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
                amazonS3Client, environment.getRequiredProperty("AWS_BUCKET_NAME")
        );
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
