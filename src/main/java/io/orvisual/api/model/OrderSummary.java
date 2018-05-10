package io.orvisual.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

/**
 * Created on 10 May, 2018.
 *
 * This model describe order details, such as client's name, phone and email. {@link OrderSummary} contains short
 * description of order, written by client and images uploaded by client. Also model provides order metadata, datetime
 * order's accepting and completion: {@link #acceptedAt} and {@link #completedAt} respectively.
 *
 * @author Artemis A. Sirosh
 */
@Entity
@Data
@NoArgsConstructor
public class OrderSummary {

    public OrderSummary(
            String clientName,
            String clientPhone,
            String clientEmail,
            String description,
            List<Picture> pictures) {
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientEmail = clientEmail;
        this.description = description;
        this.pictures = pictures;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private String description;
    private Instant acceptedAt;
    private Instant completedAt;

    @ManyToMany
    private List<Picture> pictures;

}
