package io.orvisual.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
 *         {@code fileName} -- file's of file with extension
 *     </ul>
 *     <ul>
 *         {@code directory} -- directory relative to gallery path
 *     </ul>
 *     <ul>
 *         {@code loadedAt} -- Datetime of picture's uploading
 *     </ul>
 * </li>
 *
 * @author Artemis A. Sirosh
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Picture {

    @Id
    private String checksum;
    private String fileName;
    private String directory;
    private Instant loadedAt;

}
