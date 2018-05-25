package io.orvisual.api.service;

import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import io.orvisual.api.repository.PictureRepository;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

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
    private final PictureRepository repository;

    public FileStorageService(@NonNull Path rootPath, @NonNull PictureRepository repository) {
        this.rootPath = rootPath;
        this.repository = repository;
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

}
