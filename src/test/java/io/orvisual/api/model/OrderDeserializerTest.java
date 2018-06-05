package io.orvisual.api.model;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * <p>
 * Created on 01.06.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
public class OrderDeserializerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDeserializerTest.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUpObjectMapper() throws Exception {
        Module module = new SimpleModule("Simple") {
            @Override
            public void setupModule(SetupContext context) {
                SimpleDeserializers deserializers = new SimpleDeserializers();
                deserializers.addDeserializer(Order.class, new OrderSummaryDeserializer());
                context.addDeserializers(deserializers);
            }
        };

        objectMapper.registerModule(module);
    }

    @Test
    public void shouldDeserializeSimpleOrderSummary() throws IOException {
        final String orderSummaryString = "{ \"clientName\":\"Foo Bar Quux\", " +
                "\"clientPhone\":\"+798005553535\", " +
                "\"clientEmail\":\"example@email.com\" }";

        LOGGER.info("Example Order JSON: {}", orderSummaryString);

        Order order = objectMapper.readValue(orderSummaryString, Order.class);
        LOGGER.info("Order instance: {}", order);

        assertThat(order.getClientName(), equalTo("Foo Bar Quux"));
        assertThat(order.getClientPhone(), equalTo("+798005553535"));
        assertThat(order.getClientEmail(), equalTo("example@email.com"));

    }

    @Test
    public void shouldDeserializeOrderSummaryWithDescription() throws IOException {
        final String orderSummaryString = "{ \"clientName\":\"Foo Bar Quux\", " +
                "\"clientPhone\":\"+798005553535\", " +
                "\"clientEmail\":\"example@email.com\", " +
                "\"description\": \"проще позвонить чем у кого-то занимать\" }";

        LOGGER.info("Example Order JSON: {}", orderSummaryString);

        Order actualOrder = objectMapper.readValue(orderSummaryString, Order.class);
        LOGGER.info("Described instance: {}", actualOrder);

        assertThat(actualOrder.getDescription(), equalTo("проще позвонить чем у кого-то занимать"));
    }

    @Test
    public void shouldDeserializeOrderSummaryWithPictures() throws IOException {
        final String orderSummaryString = "{ \"clientName\":\"Foo Bar Quux\", " +
                "\"clientPhone\": \"+798005553535\", " +
                "\"clientEmail\": \"example@email.com\", " +
                "\"description\": \"проще позвонить чем у кого-то занимать\", " +
                "\"pictures\": [ 4, 8, 15, 16, 23, 42 ] }";

        LOGGER.info("Example Order JSON: {}", orderSummaryString);

        Order actualOrder = objectMapper.readValue(orderSummaryString, Order.class);
        LOGGER.info("Order & Pictures: {}", actualOrder);

        assertThat(actualOrder.getPictures(), equalByPictureId(4, 8, 15, 16, 23, 42));
    }

    @SuppressWarnings("unchecked")
    private Matcher<List<Picture>> equalByPictureId(Integer... ids) {
        final Set<Picture> expected = Arrays.stream(ids)
                .map(String::valueOf)
                .map(id -> {
                    Picture p = new Picture();
                    p.setChecksum(id);
                    return p;
                }).collect(Collectors.toSet());

        return new BaseMatcher<List<Picture>>() {
            @Override
            public boolean matches(Object item) {
                if (item instanceof List) {
                    List<Picture> actual = (List<Picture>) item;
                    return expected.containsAll(actual);
                }

                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(expected);
            }
        };
    }
}
