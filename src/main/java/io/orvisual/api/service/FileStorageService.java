package io.orvisual.api.service;

import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import io.orvisual.api.repository.PictureRepository;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * <p>
 * Created on 20.05.2018.
 * </p>
 *
 * This method provides operation with image files and it's metadata objects ({@link Picture}).
 *
 * @author Artemis A. Sirosh
 */
public class FileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);

    private final Path rootPath;

    public FileStorageService(@NonNull Path rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Saves image transport object. First this method saves file, second persist {@link Picture} instance.
     * @param pictureFileItem image transport object, must not be {@code null}
     * @throws PictureFileProcessingException in case of error during IO operations
     */
    public void savePictureFileItem(@NonNull PictureFileItem pictureFileItem) {

        Path pictureDirectoryPath;
        try {

            pictureDirectoryPath = this.rootPath.resolve(pictureFileItem.getPictureItem().getDirectory());
            if (!Files.exists(pictureDirectoryPath))
                Files.createDirectories(pictureDirectoryPath);

            LOGGER.debug("Directory for new picture file item:", pictureDirectoryPath);
        } catch (IOException exc) {
            LOGGER.warn("Creation of directory failed", exc);
            throw new PictureFileProcessingException("Creation of directory failed", exc);
        }

        Path pictureFilePath = pictureDirectoryPath.resolve(pictureFileItem.getPictureItem().getFileName());

        try {

            Files.write(pictureFilePath, pictureFileItem.getFileContent());
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
    public Resource resolvePictureResource(@NonNull Picture picture) {
        return new PathResource(this.rootPath.resolve(picture.getDirectory()).resolve(picture.getFileName()));
    }

    /**
     * Determines path to picture file using {@link Picture} and deletes it from file system.
     * @param picture metadata object instance of picture file
     * @throws PictureFileProcessingException in case of error while file deleting
     */
    public void deleteFile(@NonNull Picture picture) {
        try {
            Files.delete(this.rootPath.resolve(picture.getDirectory()).resolve(picture.getFileName()));
            LOGGER.debug("Picture file '{}' has been deleted.", picture.getFileName());
        } catch (IOException exc) {
            LOGGER.warn("File deleting failed", exc);
            throw new PictureFileProcessingException("Deleting of file failed", exc);
        }
    }
}
