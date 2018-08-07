package io.orvisual.api.repository;

import io.orvisual.api.model.Order;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

/**
 * Created on 26 Jul, 2018.
 *
 * Validate attributes of {@link Order}.
 *
 * @author Artemis A. Sirosh
 */
public class OrderValidator implements Validator {

    private final Pattern phonePattern = Pattern.compile("\\+?\\d+");
    private final Pattern emailPattern = Pattern.compile(".{2,}[@].{2,}");

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return Order.class.equals(clazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object target, @NonNull Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "clientName", "required", "Attribute value required"
        );

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "clientPhone", "required", "Attribute value required"
        );

        Order order = (Order) target;

        if (!StringUtils.isEmpty(order.getClientPhone()) && !phonePattern.matcher(order.getClientPhone()).matches()) {
            errors.rejectValue("clientPhone", "invalid", "Client phone not valid");
        }

        if (!StringUtils.isEmpty(order.getClientEmail()) && !emailPattern.matcher(order.getClientEmail()).matches()) {
            errors.rejectValue("clientEmail", "invalid", "Client email not valid");
        }

        if (!StringUtils.isEmpty(order.getDescription()) && order.getDescription().length() > 4000) {
            errors.rejectValue("description", "invalid", "Order's description too long");
        }

    }
}
