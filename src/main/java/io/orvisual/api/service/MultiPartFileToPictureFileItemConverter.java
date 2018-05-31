package io.orvisual.api.service;

import com.google.common.collect.ImmutableMap;
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
import java.util.Map;
import java.util.Optional;

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
@Component
public class MultiPartFileToPictureFileItemConverter implements Converter<MultipartFile, PictureFileItem> {

    private static final Map<String, String> MIME_TYPES_MAP = ImmutableMap.of(
            MediaType.IMAGE_JPEG_VALUE, "jpg",
            MediaType.IMAGE_PNG_VALUE, "png",
            MediaType.IMAGE_GIF_VALUE, "gif",
            "image/bmp", "bmp"
    );


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
     * @param hint MIME content type from HTTP request
     * @return file extension associated with this MIME type
     * @throws MultiPartFileProcessingException in case of unsupported type was given
     */
    private static String lookupFileExtension(final String hint) {
        return Optional
                .ofNullable(MIME_TYPES_MAP.get(hint))
                .orElseThrow(() -> new MultiPartFileProcessingException("Unsupported media type: " + hint));
    }


    @Override
    public PictureFileItem convert(final @NonNull MultipartFile source) {
        if (!source.isEmpty()) {
            try {
                final byte[] multipartContent = source.getBytes();
                final String checksum = Hashing.sha256().hashBytes(multipartContent).toString();
                final String directoryName = checksum.substring(0, 4);
                final String fileName = checksum + '.' + lookupFileExtension(source.getContentType());

                return new PictureFileItem(
                        new Picture(checksum, fileName, source.getContentType(), directoryName, Instant.now()),
                        multipartContent
                );

            } catch (IOException exc) {
                throw new PictureFileProcessingException("Error while checksum calculation", exc);
            }
        }

        throw new MultiPartFileProcessingException("Uploaded multipart has no content.");
    }
}
