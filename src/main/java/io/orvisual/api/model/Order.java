package io.orvisual.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

/**
 * Created on 10 May, 2018.
 *
 * This model describe order details, such as client's name, phone and email. {@link Order} contains short
 * description of order, written by client and images uploaded by client. Also model provides order metadata, datetime
 * order's accepting and completion: {@link #acceptedAt} and {@link #completedAt} respectively.
 *
 * @author Artemis A. Sirosh
 */
@Entity
@Table(name = "ORDER_SUMMARY")
@Data
@NoArgsConstructor
public class Order {

    public Order(
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
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "ORDER_ID_SEQ"
    )
    @SequenceGenerator(name = "ORDER_ID_SEQ", sequenceName = "ORDER_ID_SEQ")
    @Column(name = "ID_ORDER")
    private Long id;

    @Column(name = "CLIENT_NAME")
    private String clientName;

    @Column(name = "CLIENT_PHONE")
    private String clientPhone;

    @Column(name = "CLIENT_EMAIL")
    private String clientEmail;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ACCEPTED_AT")
    private Instant acceptedAt;

    @Column(name = "COMPLETED_AT")
    private Instant completedAt;

    @ManyToMany
    @JoinTable(
            name = "ORDER_SUMMARY_PICTURES",
            joinColumns = { @JoinColumn(name = "ORDER_ID") },
            inverseJoinColumns = { @JoinColumn(name = "CHECKSUM") }
    )
    private List<Picture> pictures;

}
