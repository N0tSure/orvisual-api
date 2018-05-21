package io.orvisual.api.service;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p>
 * Created on 20.05.2018.
 * </p>
 *
 * Converts {@link MultipartFile} to {@link PictureFileItem}, use {@link com.google.common.hash.Hashing} to calculate
 * {@link MultipartFile}'s data checksum using {@code SHA-256}.
 *
 * @author Artemis A. Sirosh
 */
public class MultiPartFileToPictureFileItemConverter implements Converter<MultipartFile, PictureFileItem> {

    @Override
    public PictureFileItem convert(final MultipartFile source) {
        PictureFileItem result = null;
        if (!source.isEmpty()) {
            try {
                final byte[] multipartContent = source.getBytes();
                final String checksum = Hashing.sha256().hashBytes(multipartContent).toString();
                final String directoryName = checksum.substring(0, 4);
                final String fileName = String.format("%s.jpg", checksum);
                result = new PictureFileItem(
                        new Picture(checksum, fileName, directoryName, Instant.now()),
                        multipartContent
                );

            } catch (IOException exc) {

            }
        }

        return result;
    }
}
