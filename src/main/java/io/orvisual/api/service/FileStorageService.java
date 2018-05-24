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
     * @return metadata object instance for existed image
     * @throws PictureFileProcessingException in case of error during IO operations
     */
    public Picture savePictureFileItem(@NonNull PictureFileItem pictureFileItem) {
        Optional<Picture> optional = repository.findById(pictureFileItem.getPictureItem().getChecksum());
        Picture result;
        if (!optional.isPresent()) {

            Path pictureDirectoryPath;
            try {
                pictureDirectoryPath = getPictureDirectory(pictureFileItem.getPictureItem());
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

            result = repository.save(pictureFileItem.getPictureItem());
        } else {
            result = optional.get();
        }

        return result;
    }

    private Path getPictureDirectory(Picture picture) throws IOException {
        Path directoryPath = this.rootPath.resolve(picture.getDirectory());
        if (!Files.exists(directoryPath)) {
            return Files.createDirectories(directoryPath);
        } else {
            return directoryPath;
        }
    }
}
