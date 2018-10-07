package io.orvisual.api.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 22 Jul, 2018.
 *
 * This implementation of {@link PictureStorageService} provide API for storing
 * picture file on Amazon S3 cloud. To instantiate this component use
 * {@link PictureStorageServiceFactoryBean}.
 *
 * @see PictureStorageServiceFactoryBean
 * @author Artemis A. Sirosh
 */
class AmazonS3PictureStorageService implements PictureStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3PictureStorageService.class);

    private final AmazonS3 amazonS3Client;
    private final String bucketName;

    AmazonS3PictureStorageService(AmazonS3 amazonS3Client, String bucketName) {
        this.amazonS3Client = amazonS3Client;
        this.bucketName = bucketName;
    }

    /**
     * {@inheritDoc}
     *
     * @param picture contains information of a file, which will be removed.
     * @throws PictureFileProcessingException if while deleting.
     * {@link com.amazonaws.services.s3.model.S3Object} from AWS S3 error occurs.
     */
    @Override
    public void deletePictureFile(@NonNull Picture picture) throws PictureFileProcessingException {
        try {
            amazonS3Client.deleteObject(bucketName, picture.getChecksum());
        } catch (SdkClientException exc) {
            LOGGER.error("Error while file deleting process", exc);
            throw new PictureFileProcessingException(exc);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param picture contains information about a file, for searching.
     * @return {@link Resource} from content of
     *         {@link com.amazonaws.services.s3.model.S3Object}.
     *
     * @throws PictureFileProcessingException if while requesting AWS S3 error
     *         occurs.
     */
    @Override
    @NonNull
    public Resource resolvePictureResource(@NonNull Picture picture) throws PictureFileProcessingException {
        try {
            return new InputStreamResource(
                    amazonS3Client.getObject(bucketName, picture.getChecksum()).getObjectContent()
            );
        } catch (SdkClientException exc) {
            LOGGER.error("Error while downloading picture file", exc);
            throw new PictureFileProcessingException(exc);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param fileItem {@link PictureFileItem} with metadata of file and
     *                                        it's content.
     * @throws PictureFileProcessingException if due picture content saving in
     *         AWS S3 bucket error occurs.
     */
    @Override
    public void savePictureFileItem(@NonNull PictureFileItem fileItem) throws PictureFileProcessingException {

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileItem.getFileContent().length);
        metadata.setContentType(fileItem.getPictureItem().getMimeType());

        try (InputStream stream = new ByteArrayInputStream(fileItem.getFileContent())) {

            amazonS3Client.putObject(bucketName, fileItem.getPictureItem().getChecksum(), stream, metadata);
        } catch (IOException | SdkClientException exc) {
            LOGGER.error("Error while picture file uploading.", exc);
            throw new PictureFileProcessingException(exc);
        }

    }
}
