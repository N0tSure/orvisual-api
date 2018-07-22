package io.orvisual.api.service;

import com.google.common.hash.Hashing;
import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

import static org.junit.Assert.*;

/**
 * <p>
 * Created on 20.05.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
@RunWith(JUnit4.class)
public class MultiPartFileToPictureFileItemConverterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiPartFileToPictureFileItemConverterTest.class);

    private final MultiPartFileToPictureFileItemConverter converter = new MultiPartFileToPictureFileItemConverter();

    @Test
    public void shouldConvertMultiPartCorrectly() throws Exception {
        final MultipartFile multipartFile = new MockMultipartFile(
                "image",
                "foo.jpg",
                "image/jpeg",
                new byte[]{'O', 'K', 'L', 'A', 'H', 'O', 'M', 'A'}
        );

        String expectedHash = Hashing.sha256().hashBytes(multipartFile.getBytes()).toString();
        LOGGER.info("MultiPartFile content hash: {}", expectedHash);

        Picture expectedPicture = new Picture(
                expectedHash, multipartFile.getContentType(), Instant.EPOCH
        );
        LOGGER.info("Expected picture: {}", expectedPicture);

        PictureFileItem expectedFileItem =
                new PictureFileItem(expectedPicture, new byte[]{'O', 'K', 'L', 'A', 'H', 'O', 'M', 'A'});

        LOGGER.info("Expected file item: {}", expectedFileItem);

        PictureFileItem actualFileItem = converter.convert(multipartFile);
        LOGGER.info("Actual picture file item: {}", actualFileItem);

        assertNotNull(actualFileItem);
        assertArrayEquals(
                "Content not equal", expectedFileItem.getFileContent(), actualFileItem.getFileContent()
        );

        assertEquals(
                "Checksum not equal",
                expectedHash,
                actualFileItem.getPictureItem().getChecksum()
        );

        assertEquals(
                "MIME type not equal",
                multipartFile.getContentType(),
                actualFileItem.getPictureItem().getMimeType()
        );

    }


    @Test(expected = MultiPartFileProcessingException.class)
    public void shouldRejectEmptyMultiPartFile() {
        final MultipartFile multipartFile = new MockMultipartFile("image", new byte[]{});
        converter.convert(multipartFile);
    }

    @Test(expected = MultiPartFileProcessingException.class)
    public void shouldRejectMultiPartWithoutMimeType() {
        final MultipartFile multipartFile =
                new MockMultipartFile("image", null, null, new byte[]{});
        converter.convert(multipartFile);
    }

    @Test(expected = MultiPartFileProcessingException.class)
    public void shouldRejectMultiPartWithWrongType() {
        final MultipartFile multipartFile =
                new MockMultipartFile("image", "foo.txt", "text/plain", new byte[]{12});
        converter.convert(multipartFile);
    }
}
