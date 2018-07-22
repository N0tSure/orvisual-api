package io.orvisual.api.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
@Component
public class MultiPartFileToPictureFileItemConverter implements Converter<MultipartFile, PictureFileItem> {

    private static final List<String> MIME_TYPES = ImmutableList.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            "image/bmp"
    );

    /**
     * This method return MIME type of MultiPart content or throw exception
     * if given MIME type not supported. Look about MIME type on
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">MDN</a>.
     * Supported MIME types:
     * <ul>
     *   <li>image/jpeg</li>
     *   <li>image/png</li>
     *   <li>image/gif</li>
     *   <li>image/bmp</li>
     * </ul>
     *
     * @param type MIME content type from HTTP request
     * @return MIME type string
     * @throws MultiPartFileProcessingException in case of unsupported type was given
     */
    private static String checkMimeType(final String type) throws MultiPartFileProcessingException {
        return MIME_TYPES.stream().filter(t -> t.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new MultiPartFileProcessingException("Unsupported media type: " + type));
    }

    private final HashFunction sha256HashFunction = Hashing.sha256();

    @Override
    public PictureFileItem convert(final @NonNull MultipartFile source) {
        if (!source.isEmpty()) {
            try {
                final byte[] multipartContent = source.getBytes();
                final String checksum = sha256HashFunction.hashBytes(multipartContent).toString();

                return new PictureFileItem(
                        new Picture(checksum, checkMimeType(source.getContentType()), Instant.now()),
                        multipartContent
                );

            } catch (IOException exc) {
                throw new PictureFileProcessingException("Error while checksum calculation", exc);
            }
        }

        throw new MultiPartFileProcessingException("Uploaded multipart has no content.");
    }
}
