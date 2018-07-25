package io.orvisual.api.service;

import com.google.common.collect.ImmutableMap;
import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * Created on 20.05.2018.
 * </p>
 *
 * This method provides operation with image files and it's metadata objects
 * ({@link Picture}). For instantiate use
 * {@link FileSystemPictureStorageServiceFactoryBean}.
 *
 * @see FileSystemPictureStorageServiceFactoryBean
 * @author Artemis A. Sirosh
 */
class FileSystemPictureStorageService implements PictureStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemPictureStorageService.class);
    private static final Map<String, String> MIME_TYPES_MAP = ImmutableMap.of(
            MediaType.IMAGE_JPEG_VALUE, "jpg",
            MediaType.IMAGE_PNG_VALUE, "png",
            MediaType.IMAGE_GIF_VALUE, "gif",
            "image/bmp", "bmp"
    );

    private final Path rootPath;

    /**
     * This method returns file extension for MIME type given as hint. Look about MIME type on
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">MDN</a>.
     * Supported MIME types:
     * <ul>
     *     <li>image/jpeg</li>
     *     <li>image/png</li>
     *     <li>image/gif</li>
     *     <li>image/bmp</li>
     * </ul>
     *
     * @param hint MIME content type
     * @return file extension associated with this MIME type
     * @throws PictureFileProcessingException in case of unsupported type was given
     */
    private static String lookupFileExtension(final String hint) throws PictureFileProcessingException {
        return Optional
                .ofNullable(MIME_TYPES_MAP.get(hint))
                .orElseThrow(() -> new PictureFileProcessingException("Unsupported media type: " + hint));
    }

    FileSystemPictureStorageService(@NonNull Path rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Saves image transport object. First this method saves file, second persist {@link Picture} instance.
     * @param fileItem image transport object, must not be {@code null}
     * @throws PictureFileProcessingException in case of error during IO operations
     */
    @Override
    public void savePictureFileItem(@NonNull PictureFileItem fileItem) {

        Path pictureDirectoryPath;
        try {

            pictureDirectoryPath = this.rootPath.resolve(fileItem.getPictureItem().getChecksum().substring(0,4));
            if (!Files.exists(pictureDirectoryPath))
                Files.createDirectories(pictureDirectoryPath);

            LOGGER.debug("Directory for new picture file item: {}", pictureDirectoryPath);
        } catch (IOException exc) {
            LOGGER.warn("Creation of directory failed", exc);
            throw new PictureFileProcessingException("Creation of directory failed", exc);
        }

        Path pictureFilePath = pictureDirectoryPath.resolve(
                fileItem.getPictureItem().getChecksum()
                        + "."
                        + lookupFileExtension(fileItem.getPictureItem().getMimeType())
        );

        try {

            Files.write(pictureFilePath, fileItem.getFileContent());
            LOGGER.debug("Picture file recorded as: {}", pictureFilePath);
        } catch (IOException exc) {
            LOGGER.warn("Recording of picture file failed", exc);
            throw new PictureFileProcessingException("Recording of picture file failed", exc);
        }

    }

    /**
     * Resolves pictures {@link Resource} using metadata from {@link Picture} instance
     * @param picture metadata object instance
     * @return {@link Resource} instance
     */
    @Override
    @NonNull
    public Resource resolvePictureResource(@NonNull Picture picture) {
        return new PathResource(
                this.rootPath.resolve(picture.getChecksum().substring(0, 4))
                        .resolve(picture.getChecksum() + "." + lookupFileExtension(picture.getMimeType()))
        );
    }

    /**
     * Determines path to picture file using {@link Picture} and deletes it from file system.
     * @param picture metadata object instance of picture file
     * @throws PictureFileProcessingException in case of error while file deleting
     */
    @Override
    public void deletePictureFile(@NonNull Picture picture) {
        try {
            Files.delete(
                    this.rootPath.resolve(picture.getChecksum().substring(0, 4)).resolve(
                            picture.getChecksum() + "." + lookupFileExtension(picture.getMimeType())
                    )
            );
            LOGGER.debug("Picture file '{}' has been deleted.", picture.getChecksum());
        } catch (IOException exc) {
            LOGGER.warn("File deleting failed", exc);
            throw new PictureFileProcessingException("Deleting of file failed", exc);
        }
    }
}
