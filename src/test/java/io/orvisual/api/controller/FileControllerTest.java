package io.orvisual.api.controller;

import io.orvisual.api.TestHelper;
import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import io.orvisual.api.repository.PictureRepository;
import io.orvisual.api.service.FileStorageService;
import io.orvisual.api.service.PictureFileProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.function.Supplier;

import static io.orvisual.api.TestHelper.ignoreUnPredictableAttributes;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * Created on 27.05.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
@WebMvcTest
@RunWith(SpringRunner.class)
public class FileControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileControllerTest.class);

    private final Supplier<PictureFileItem> fileItemSupplier = TestHelper.pictureSupplier();

    @MockBean
    private FileStorageService storageService;

    @MockBean
    private PictureRepository pictureRepository;

    @Captor
    private ArgumentCaptor<PictureFileItem> pictureFileItemCaptor;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnAlreadyExisted() throws Exception {
        PictureFileItem expectedFileItem = fileItemSupplier.get();

        when(pictureRepository.findById(expectedFileItem.getPictureItem().getChecksum()))
                .thenReturn(Optional.of(expectedFileItem.getPictureItem()));

        MockMultipartFile mockMultiPart = new MockMultipartFile(
                "image",
                "foo.jpg",
                expectedFileItem.getPictureItem().getMimeType(),
                expectedFileItem.getFileContent()
        );

        mockMvc.perform(multipart("/images").file(mockMultiPart))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checksum", equalTo(expectedFileItem.getPictureItem().getChecksum())))
                .andExpect(jsonPath("$.mimeType", equalTo(expectedFileItem.getPictureItem().getMimeType())));

        verify(pictureRepository).findById(expectedFileItem.getPictureItem().getChecksum());
        verify(storageService, never()).savePictureFileItem(any());
    }

    @Test
    public void shouldSaveNewPicture() throws Exception {
        final PictureFileItem expectedFileItem = fileItemSupplier.get();
        when(pictureRepository.save(
                argThat(ignoreUnPredictableAttributes(expectedFileItem.getPictureItem())))
        ).thenReturn(expectedFileItem.getPictureItem());

        MockMultipartFile mockMultiPart = new MockMultipartFile(
                "image",
                "foo.jpg",
                expectedFileItem.getPictureItem().getMimeType(),
                expectedFileItem.getFileContent()
        );

        mockMvc.perform(multipart("/images").file(mockMultiPart))
                .andDo(log())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.checksum", equalTo(expectedFileItem.getPictureItem().getChecksum())))
                .andExpect(jsonPath("$.mimeType", equalTo(expectedFileItem.getPictureItem().getMimeType())));

        verify(pictureRepository).findById(eq(expectedFileItem.getPictureItem().getChecksum()));
        verify(storageService).savePictureFileItem(argThat(ignoreUnPredictableAttributes(expectedFileItem)));
        verify(pictureRepository)
                .save(argThat(ignoreUnPredictableAttributes(expectedFileItem.getPictureItem())));

    }

    @Test
    public void shouldProcessErrorWhileImageFileSaving() throws Exception {

        doThrow(new PictureFileProcessingException("error while saving"))
                .when(storageService).savePictureFileItem(any());

        MockMultipartFile multipartFile = new MockMultipartFile(
                "image", "foo.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{2, 42});

        mockMvc.perform(multipart("/images").file(multipartFile))
                .andDo(log())
                .andExpect(status().isInternalServerError());

        verify(pictureRepository).findById(any());
        verify(storageService).savePictureFileItem(any());
        verify(pictureRepository, never()).save(any());
    }

    @Test
    public void shouldReturnExistedPicture() throws Exception {
        PictureFileItem fileItem = fileItemSupplier.get();
        when(pictureRepository.findById(fileItem.getPictureItem().getChecksum()))
                .thenReturn(Optional.of(fileItem.getPictureItem()));

        when(storageService.resolvePictureResource(fileItem.getPictureItem()))
                .thenReturn(new ByteArrayResource(fileItem.getFileContent()));

        mockMvc.perform(get("/images/" + fileItem.getPictureItem().getChecksum()))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", fileItem.getPictureItem().getMimeType()))
                .andExpect(content().bytes(fileItem.getFileContent()));

        verify(pictureRepository).findById(fileItem.getPictureItem().getChecksum());
        verify(storageService).resolvePictureResource(fileItem.getPictureItem());
        
    }

    @Test
    public void shouldRejectRequestWithNotExistedPicture() throws Exception {
        PictureFileItem fileItem = fileItemSupplier.get();

        mockMvc.perform(get("/images/" + fileItem.getPictureItem().getChecksum()))
                .andDo(log())
                .andExpect(status().isNotFound());

        verify(pictureRepository).findById(fileItem.getPictureItem().getChecksum());
        verify(storageService, never()).resolvePictureResource(any());
    }

    @Test
    public void shouldRejectNullAsPictureChecksum() throws Exception {

        mockMvc.perform(get("/images"))
                .andDo(log())
                .andExpect(status().isMethodNotAllowed());

        verify(pictureRepository, never()).findById(any());
        verify(storageService, never()).resolvePictureResource(any());
    }
}
