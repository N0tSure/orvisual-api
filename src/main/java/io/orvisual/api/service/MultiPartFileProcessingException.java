package io.orvisual.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created on 29 May, 2018.
 *
 * @author Artemis A. Sirosh
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "unable to process uploaded multipart")
public class MultiPartFileProcessingException extends IllegalArgumentException {

    public MultiPartFileProcessingException(String s) {
        super(s);
    }

}
