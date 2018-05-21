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
 *
 *
 * @author Artemis A. Sirosh
 */
@RunWith(JUnit4.class)
public class MultiPartFileToPictureFileItemConverterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiPartFileToPictureFileItemConverterTest.class);

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

        String expectedDirectoryName = expectedHash.substring(0, 4);
        LOGGER.info("Expected folder name: {}", expectedDirectoryName);

        String expectedFileName = expectedHash + ".jpg";
        LOGGER.info("Expected file name: {}", expectedFileName);

        Picture expectedPicture = new Picture(expectedHash, expectedDirectoryName, expectedFileName, Instant.EPOCH);
        LOGGER.info("Expected picture: {}", expectedPicture);

        PictureFileItem expectedFileItem =
                new PictureFileItem(expectedPicture, new byte[]{'O', 'K', 'L', 'A', 'H', 'O', 'M', 'A'});

        LOGGER.info("Expected file item: {}", expectedFileItem);

        MultiPartFileToPictureFileItemConverter converter = new MultiPartFileToPictureFileItemConverter();

        PictureFileItem actualFileItem = converter.convert(multipartFile);

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
                "Directory name not equal",
                expectedDirectoryName,
                actualFileItem.getPictureItem().getDirectory()
        );

        assertEquals("File names not equal", expectedFileName, actualFileItem.getPictureItem().getFileName());

    }


    @Test
    public void shouldProcessEmptyMultiPartFile() {
        final MultipartFile multipartFile = new MockMultipartFile("image", new byte[]{});

        MultiPartFileToPictureFileItemConverter converter = new MultiPartFileToPictureFileItemConverter();

        assertNull(converter.convert(multipartFile));
    }
}
