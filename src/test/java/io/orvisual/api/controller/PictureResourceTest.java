package io.orvisual.api.controller;

import io.orvisual.api.TestHelper;
import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import io.orvisual.api.repository.PictureRepository;
import io.orvisual.api.service.FileStorageService;
import io.orvisual.api.service.PictureFileProcessingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.function.Supplier;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * Created on 31.05.2018.
 * </p>
 *
 * Test for customization Spring Data Rest resource {@link Picture}
 *
 * @author Artemis A. Sirosh
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PictureResourceTest {

    @MockBean
    private FileStorageService storageService;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private final Supplier<PictureFileItem> itemSupplier = TestHelper.uniformPictureItemSupplier();

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDownRepositories() {
        pictureRepository.deleteAll();
    }

    @Test
    public void shouldNotExposeUnSafeMethods() throws Exception {
        mockMvc.perform(get("/profile/pictures"))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.alps.descriptors[*].id",
                        allOf(
                                not("create-pictures"),
                                not("patch-picture"),
                                not("update-picture")
                        )
                ));

    }

    @Test
    public void shouldDeletePictureAndImageFile() throws Exception {
        Picture fixturePicture = itemSupplier.get().getPictureItem();
        fixturePicture = pictureRepository.save(fixturePicture);

        assertNotNull("sanity check failed", pictureRepository.findById(fixturePicture.getChecksum()));

        mockMvc.perform(delete("/pictures/" + fixturePicture.getChecksum()))
                .andDo(log())
                .andExpect(status().isNoContent());

        assertFalse("fixture not removed", pictureRepository.existsById(fixturePicture.getChecksum()));
        verify(storageService).deleteFile(fixturePicture);

    }

    @Test
    public void shouldRejectDeletionNotExistedPicture() throws Exception {
        Picture fixturePicture = itemSupplier.get().getPictureItem();

        assertFalse("sanity check failed", pictureRepository.existsById(fixturePicture.getChecksum()));

        mockMvc.perform(delete("/pictures/" + fixturePicture.getChecksum()))
                .andDo(log())
                .andExpect(status().isNotFound());

        verify(storageService, never()).deleteFile(any());

    }

    @Test
    public void shouldProcessErrorDuringFileDeletion() throws Exception {
        Picture fixturePicture = itemSupplier.get().getPictureItem();
        fixturePicture = pictureRepository.save(fixturePicture);

        doThrow(new PictureFileProcessingException("deletion failed")).when(storageService).deleteFile(any());

        assertTrue("sanity check failed", pictureRepository.existsById(fixturePicture.getChecksum()));

        mockMvc.perform(delete("/pictures/" + fixturePicture.getChecksum()))
                .andDo(log())
                .andExpect(status().isInternalServerError());

        assertFalse("fixture must be removed first",
                pictureRepository.existsById(fixturePicture.getChecksum()));

        verify(storageService).deleteFile(fixturePicture);

    }

    @Test
    public void shouldIncludeLinkToImageFile() throws Exception {
        Picture fixturePicture = pictureRepository.save(itemSupplier.get().getPictureItem());
        assertTrue("sanity check failed", pictureRepository.existsById(fixturePicture.getChecksum()));

        mockMvc.perform(get("/pictures/{checksum}", fixturePicture.getChecksum()))
                .andDo(log())
                .andExpect(
                        jsonPath(
                                "$._links.imageFile.href",
                                endsWith("files/" + fixturePicture.getChecksum())
                        )
                );
    }
}
