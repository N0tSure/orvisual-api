package io.orvisual.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created on 29 May, 2018.
 *
 * This exception will be thrown, when {@link org.springframework.web.multipart.MultipartFile MultipartFile}
 * object, passed in to {@link MultiPartFileToPictureFileItemConverter} with incompatible content. For
 * example, with unknown content type, without content etc.
 *
 * @author Artemis A. Sirosh
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "unable to process uploaded multipart")
class MultiPartFileProcessingException extends IllegalArgumentException {

    MultiPartFileProcessingException(String s) {
        super(s);
    }

}
