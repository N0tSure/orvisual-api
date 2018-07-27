package io.orvisual.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Created on 09 May, 2018.
 *
 * Model of picture, used for maintain file storage.
 * Attribute description:
 * <li>
 *     <ul>
 *         {@code checksum} -- file's checksum
 *     </ul>
 *     <ul>
 *         {@code mimeType} -- MIME type of the file
 *     </ul>
 *     <ul>
 *         {@code loadedAt} -- Datetime of picture's uploading
 *     </ul>
 * </li>
 *
 * @author Artemis A. Sirosh
 */
@Entity
@Table(name = "PICTURES")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Picture {

    @Id
    @Column(name = "CHECKSUM")
    private String checksum;

    @Column(name = "MIME_TYPE")
    private String mimeType;

    @Column(name = "LOADED_AT")
    private Instant loadedAt;

}
