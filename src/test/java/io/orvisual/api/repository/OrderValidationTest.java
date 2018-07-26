package io.orvisual.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.orvisual.api.model.Order;
import io.orvisual.api.service.PictureStorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.Random;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created on 26 Jul, 2018.
 *
 * @author Artemis A. Sirosh
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderValidationTest {

    @MockBean
    private PictureStorageService storageService;

    @Autowired
    private OrderRepository orderRepository;

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
    public void shouldDetectMissingClientName() throws Exception {
        Order notValidOrder = new Order();
        notValidOrder.setClientPhone("+7985155456");
        notValidOrder.setClientEmail("foo@bar.com");
        notValidOrder.setDescription("foo bar baz");

        mockMvc.perform(
                post("/orders")
                        .content(objectMapper.writeValueAsString(notValidOrder))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()", equalTo(1)))
                .andExpect(jsonPath("$.errors[0].entity", equalTo("Order")))
                .andExpect(jsonPath("$.errors[0].property", equalTo("clientName")));
    }

    @Test
    public void shouldDetectMissingClientPhone() throws Exception {
        Order notValidOrder = new Order();
        notValidOrder.setClientName("foo");
        notValidOrder.setClientEmail("foo@bar.com");
        notValidOrder.setDescription("foo bar baz");

        mockMvc.perform(
                post("/orders")
                        .content(objectMapper.writeValueAsString(notValidOrder))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()", equalTo(1)))
                .andExpect(jsonPath("$.errors[0].entity", equalTo("Order")))
                .andExpect(jsonPath("$.errors[0].property", equalTo("clientPhone")));
    }

    @Test
    public void shouldRejectInvalidClientPhone() throws Exception {
        Order notValidOrder = new Order();
        notValidOrder.setClientName("foo");
        notValidOrder.setClientPhone("qux756789");
        notValidOrder.setClientEmail("foo@bar.com");
        notValidOrder.setDescription("foo bar baz");

        mockMvc.perform(
                post("/orders")
                        .content(objectMapper.writeValueAsString(notValidOrder))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()", equalTo(1)))
                .andExpect(jsonPath("$.errors[0].entity", equalTo("Order")))
                .andExpect(jsonPath("$.errors[0].property", equalTo("clientPhone")));
    }

    @Test
    public void shouldRejectInvalidClientEmail() throws Exception {
        Order notValidOrder = new Order();
        notValidOrder.setClientName("foo");
        notValidOrder.setClientPhone("+756789");
        notValidOrder.setClientEmail("fhsdh");
        notValidOrder.setDescription("foo bar baz");

        mockMvc.perform(
                post("/orders")
                        .content(objectMapper.writeValueAsString(notValidOrder))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()", equalTo(1)))
                .andExpect(jsonPath("$.errors[0].entity", equalTo("Order")))
                .andExpect(jsonPath("$.errors[0].property", equalTo("clientEmail")));
    }

    @Test
    public void shouldRejectInvalidDescription() throws Exception {
        Order notValidOrder = new Order();
        notValidOrder.setClientName("foo");
        notValidOrder.setClientPhone("+756789");
        notValidOrder.setClientEmail("foo@bar.com");

        Random random = new Random();

        byte[] descContent = new byte[4042];
        for (int i = 0; i < descContent.length; i++) {
            descContent[i] = (byte) (65 + random.nextInt(98 - 65));
        }

        notValidOrder.setDescription(new String(descContent, Charset.forName("utf-8")));

        mockMvc.perform(
                post("/orders")
                        .content(objectMapper.writeValueAsString(notValidOrder))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()", equalTo(1)))
                .andExpect(jsonPath("$.errors[0].entity", equalTo("Order")))
                .andExpect(jsonPath("$.errors[0].property", equalTo("description")));
    }

    @Test
    public void shouldRejectInvalidValuesOnUpdating() throws Exception {

        Order saved = orderRepository.save(new Order(
                "foo", "+756789", "foo@bar.com", "foo bar baz", null)
        );

        mockMvc.perform(
                put("/orders/" + String.valueOf(saved.getId()))
                        .content("{}").contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()", equalTo(2)));

    }

    @Test
    public void shouldAcceptOrderWithoutOptionalAttributes() throws Exception {
        Order notValidOrder = new Order();
        notValidOrder.setClientName("foo");
        notValidOrder.setClientPhone("+756789");


        mockMvc.perform(
                post("/orders")
                        .content(objectMapper.writeValueAsString(notValidOrder))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(log())
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldAcceptValidOrder() throws Exception {
        Order notValidOrder = new Order();
        notValidOrder.setClientName("foo");
        notValidOrder.setClientPhone("+756789");
        notValidOrder.setClientEmail("foo@bar.com");
        notValidOrder.setDescription("foo bar baz");


        mockMvc.perform(
                post("/orders")
                        .content(objectMapper.writeValueAsString(notValidOrder))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(log())
                .andExpect(status().isCreated());
    }
}
