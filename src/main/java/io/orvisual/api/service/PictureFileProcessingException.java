package io.orvisual.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>
 * Created on 21.05.2018.
 * </p>
 *
 * This exception will thrown, in case of error during uploaded file process, i.e. create directory, check directory,
 * write file or something.
 *
 * @author Artemis A. Sirosh
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "error during picture file processing")
public class PictureFileProcessingException extends RuntimeException {

    public PictureFileProcessingException(String message) {
        super(message);
    }

    public PictureFileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PictureFileProcessingException(Throwable cause) {
        super(cause);
    }
}
