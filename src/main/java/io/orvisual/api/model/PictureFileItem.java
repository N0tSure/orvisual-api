package io.orvisual.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

/**
 * <p>
 * Created on 20.05.2018.
 * </p>
 *
 * This interface describes transition item between raw file and {@link Picture} instance. Contains file content from
 * {@link org.springframework.web.multipart.MultipartFile} and file's metadata as {@link Picture}.
 *
 * @author Artemis A. Sirosh
 */
@Value
@AllArgsConstructor
public class PictureFileItem {

    private final Picture pictureItem;
    private final byte[] fileContent;

}
