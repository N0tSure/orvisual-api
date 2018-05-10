package io.orvisual.api.repository;

import io.orvisual.api.model.OrderSummary;
import org.springframework.data.repository.CrudRepository;

/**
 * Created on 10 May, 2018.
 *
 * {@link OrderSummaryRepository} provides storage operations for {@link OrderSummary} model.
 *
 * @author Artemis A. Sirosh
 */
public interface OrderSummaryRepository extends CrudRepository<OrderSummary, Long> {
}
