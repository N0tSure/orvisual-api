package io.orvisual.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.orvisual.api.model.Order;
import io.orvisual.api.service.PictureStorageService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created on 26 Jul, 2018.
 *
 * Test for {@link OrderEvenHandler}.
 *
 * @author Artemis A. Sirosh
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderEventHandlerTest {

    @MockBean
    private PictureStorageService storageService;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Before
    public void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
    }

    @Test
    public void shouldSetNullAsTimestampsToANewOrder() throws Exception {
        Order order = new Order(
            "foo",
            "+7985458651",
            "foo@basr.ru",
            "foo bar baz",
            null
        );

        order.setAcceptedAt(Instant.now());
        order.setCompletedAt(Instant.now());

        String orderUrl = mockMvc.perform(
                post("/orders")
                        .content(objectMapper.writeValueAsString(order))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn().getResponse().getHeader("Location");

        Assert.assertNotNull(orderUrl);

        mockMvc.perform(get(orderUrl))
                .andDo(log())
                .andExpect(jsonPath("$.acceptedAt", Matchers.nullValue()))
                .andExpect(jsonPath("$.completedAt", Matchers.nullValue()));
    }
}
