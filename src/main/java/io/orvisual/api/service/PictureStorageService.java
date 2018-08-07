package io.orvisual.api.service;

import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

/**
 * Created on 22 Jul, 2018.
 *
 * This component provides API for manage picture files.
 *
 * @author Artemis A. Sirosh
 */
public interface PictureStorageService {

    /**
     * Removes picture file from storage.
     *
     * @param picture contains information of a file, which will be removed.
     * @throws PictureFileProcessingException if while file processing error
     * occurs
     */
    void deletePictureFile(@NonNull Picture picture) throws PictureFileProcessingException;

    /**
     * Try to find picture file on storage and if exists wrap in {@link Resource}.
     *
     * @param picture contains information about a file, for searching.
     * @return {@link Resource} with picture file content
     * @throws PictureFileProcessingException if file not found, or an error
     * occurs while file searching
     */
    @NonNull Resource resolvePictureResource(@NonNull Picture picture) throws PictureFileProcessingException;

    /**
     * Save on storage content of {@link PictureFileItem}.
     *
     * @param fileItem {@link PictureFileItem} with metadata of file and
     *                                        it's content
     * @throws PictureFileProcessingException if while file saving an
     * error occurs
     */
    void savePictureFileItem(@NonNull PictureFileItem fileItem) throws PictureFileProcessingException;
}
