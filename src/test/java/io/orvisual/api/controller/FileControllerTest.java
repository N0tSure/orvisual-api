package io.orvisual.api.controller;

import io.orvisual.api.TestHelper;
import io.orvisual.api.model.PictureFileItem;
import io.orvisual.api.repository.PictureRepository;
import io.orvisual.api.service.FileStorageService;
import io.orvisual.api.service.PictureFileProcessingException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        PictureFileItem expectedFileItem = fileItemSupplier.get();
        when(pictureRepository.save(eq(expectedFileItem.getPictureItem())))
                .thenReturn(expectedFileItem.getPictureItem());

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
        verify(storageService).savePictureFileItem(eq(expectedFileItem));
        verify(pictureRepository).save(expectedFileItem.getPictureItem());

    }

    @Test
    public void shouldProcessErrorWhileImageFileSaving() throws Exception {

        doThrow(new PictureFileProcessingException("error while saving"))
                .when(storageService).savePictureFileItem(any());

        MockMultipartFile multipartFile =
                new MockMultipartFile("image", "foo.jpg", MediaType.IMAGE_JPEG_VALUE,new byte[]{});

        mockMvc.perform(multipart("/images").file(multipartFile))
                .andDo(log())
                .andExpect(status().isInternalServerError());

        verify(pictureRepository).findById(any());
        verify(storageService).savePictureFileItem(any());
        verify(pictureRepository, never()).save(any());
    }
}
