package io.orvisual.api.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import io.orvisual.api.TestHelper;
import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created on 22 Jul, 2018.
 *
 * @author Artemis A. Sirosh
 */
@RunWith(MockitoJUnitRunner.class)
public class AmazonS3PictureStorageServiceTest {

    private static final String BUCKET_NAME = "test-bucket";

    private final Supplier<Picture> pictureSupplier = TestHelper.randomPictureSupplier();

    @Mock
    private AmazonS3 amazonS3Client;

    @Captor
    private ArgumentCaptor<InputStream> inputStreamCaptor;

    @Captor
    private ArgumentCaptor<ObjectMetadata> objectMetadataCaptor;

    private PictureStorageService storageService;

    @Before
    public void setUp() {
        this.storageService = new AmazonS3PictureStorageService(amazonS3Client, BUCKET_NAME);
    }

    @Test
    public void shouldDeleteExistedPictureFile() {
        Picture expectedPicture = pictureSupplier.get();

        storageService.deletePictureFile(expectedPicture);

        verify(amazonS3Client, times(1))
                .deleteObject(BUCKET_NAME, expectedPicture.getChecksum());
    }

    @Test(expected = PictureFileProcessingException.class)
    public void shouldProcessErrorWhileFileDeleting() {
        Picture picture = pictureSupplier.get();

        doThrow(new AmazonServiceException("test"))
                .when(amazonS3Client).deleteObject(eq(BUCKET_NAME), eq(picture.getChecksum()));

        storageService.deletePictureFile(picture);
    }

    @Test
    public void shouldReturnResourceOfExistedPictureFile() throws IOException {
        Picture picture = pictureSupplier.get();
        byte[] pictureFileContent = "OKLAHOMA".getBytes();
        Resource expectedResource = new ByteArrayResource(pictureFileContent);

        S3Object expectedS3Object = new S3Object();
        expectedS3Object.setObjectContent(expectedResource.getInputStream());
        when(amazonS3Client.getObject(BUCKET_NAME, picture.getChecksum())).thenReturn(expectedS3Object);

        Resource actualResource = storageService.resolvePictureResource(picture);
        byte[] actualContent = new byte[pictureFileContent.length];

        try (InputStream inputStream = actualResource.getInputStream()) {
            int readBytes = inputStream.read(actualContent);
            assertEquals("Wrong amount of bytes was read", pictureFileContent.length, readBytes);
        }

        assertArrayEquals("File contents not equal", pictureFileContent, actualContent);
    }

    @Test(expected = PictureFileProcessingException.class)
    public void shouldProcessErrorWhilePictureFileDownloading() {
        Picture picture = pictureSupplier.get();

        doThrow(new SdkClientException("test")).when(amazonS3Client).getObject(BUCKET_NAME, picture.getChecksum());

        storageService.resolvePictureResource(picture);
    }

    @Test
    public void shouldUploadPictureFile() throws IOException {
        Picture picture = pictureSupplier.get();
        byte[] expectedContent = "OKLAHOMA".getBytes();
        PictureFileItem fileItem = new PictureFileItem(picture, expectedContent);

        storageService.savePictureFileItem(fileItem);

        verify(amazonS3Client).putObject(
                eq(BUCKET_NAME), eq(picture.getChecksum()), inputStreamCaptor.capture(), objectMetadataCaptor.capture()
        );

        try (InputStream stream = inputStreamCaptor.getValue()) {
            byte[] actualContent = new byte[expectedContent.length];
            stream.read(actualContent);

            assertArrayEquals("Content of file", expectedContent, actualContent);
        }

        ObjectMetadata actualMetadata = objectMetadataCaptor.getValue();
        assertThat("Content MIME type", actualMetadata.getContentType(), equalTo(picture.getMimeType()));
        assertThat("Content length", actualMetadata.getContentLength(), equalTo((long) expectedContent.length));
    }

    @Test(expected = PictureFileProcessingException.class)
    public void shouldProcessErrorWhileFileUploading() {
        PictureFileItem fileItem = new PictureFileItem(pictureSupplier.get(), new byte[]{'f'});

        doThrow(new SdkClientException("test"))
                .when(amazonS3Client)
                .putObject(
                        eq(BUCKET_NAME),
                        eq(fileItem.getPictureItem().getChecksum()),
                        any(InputStream.class),
                        any(ObjectMetadata.class)
                );

        storageService.savePictureFileItem(fileItem);
    }
}
