package io.orvisual.api.repository;

import io.orvisual.api.model.Order;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

/**
 * Created on 26 Jul, 2018.
 *
 * This component handle event of {@link OrderRepository}.
 *
 * @author Artemis A. Sirosh
 */
@Component
@RepositoryEventHandler
public class OrderEvenHandler {

    /**
     * Set {@link Order#acceptedAt} and {@link Order#completedAt}
     * attributes as {@code null} before it saves in database.
     *
     * @param order new {@link Order}
     */
    @HandleBeforeCreate
    public void handleBeforeCreate(Order order) {
        order.setAcceptedAt(null);
        order.setCompletedAt(null);
    }

}
