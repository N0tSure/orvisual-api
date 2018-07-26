package io.orvisual.api.config;

import io.orvisual.api.repository.OrderValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

/**
 * Created on 09 May, 2018.
 *
 * Configure Rest Repositories.
 *
 * @author Artemis A. Sirosh
 */
@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        OrderValidator orderValidator = new OrderValidator();
        validatingListener.addValidator("beforeCreate", orderValidator);
        validatingListener.addValidator("beforeSave", orderValidator);
    }
}
