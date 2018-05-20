package io.orvisual.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * Created on 20.05.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
@RunWith(JUnit4.class)
public class MultiPartFileToPictureFileItemConverterTest {

    @Test
    public void shouldConvertMultiPartCorrectly() {
        final MultipartFile multipartFile = new MockMultipartFile(
                "image",
                "foo.jpg",
                "image/jpeg",
                new byte[]{'O', 'K', 'L', 'A', 'H', 'O', 'M', 'A'}
        );
    }
}
