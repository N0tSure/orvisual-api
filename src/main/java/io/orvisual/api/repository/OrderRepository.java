package io.orvisual.api.repository;

import io.orvisual.api.model.Order;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created on 10 May, 2018.
 *
 * {@link OrderRepository} provides storage operations for {@link Order} model.
 *
 * @author Artemis A. Sirosh
 */
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {
}
