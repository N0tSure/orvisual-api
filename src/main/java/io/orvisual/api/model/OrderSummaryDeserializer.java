package io.orvisual.api.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>
 * Created on 01.06.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
public class OrderSummaryDeserializer extends JsonDeserializer<OrderSummary> {

    @Override
    public OrderSummary deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);
        OrderSummary result = new OrderSummary();

        result.setClientName(rootNode.get("clientName").asText());
        result.setClientPhone(rootNode.get("clientPhone").asText());
        result.setClientEmail(rootNode.get("clientEmail").asText());
        result.setDescription(rootNode.get("description") != null ? rootNode.get("description").asText() : null);

        JsonNode pictureCheckSumsNode = rootNode.get("pictures");
        if (pictureCheckSumsNode != null) {
            List<Picture> pictures = new ArrayList<>();
            for (JsonNode pictureCheckSumNode : pictureCheckSumsNode) {
                Picture picture = new Picture();
                picture.setChecksum(pictureCheckSumNode.asText());
                pictures.add(picture);
            }
            result.setPictures(pictures);
        }

        return result;
    }
}
