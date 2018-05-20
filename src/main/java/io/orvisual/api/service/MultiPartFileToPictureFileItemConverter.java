package io.orvisual.api.service;

import io.orvisual.api.model.PictureFileItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * Created on 20.05.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
public class MultiPartFileToPictureFileItemConverter implements Converter<MultipartFile, PictureFileItem> {

    @Override
    public PictureFileItem convert(MultipartFile source) {
        return null;
    }
}
