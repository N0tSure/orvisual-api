package io.orvisual.api.controller;

import io.orvisual.api.service.FileStorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * Created on 31.05.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PictureResourceTest {

    @MockBean
    private FileStorageService storageService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
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
}
