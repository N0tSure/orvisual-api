package io.orvisual.api.repository;

import io.orvisual.api.model.Picture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

/**
 * Created on 10 May, 2018.
 *
 * Provides persistent operations with {@link Picture}.
 *
 * @author Artemis A. Sirosh
 */
@RepositoryRestResource(exported = false)
public interface PictureRepository extends CrudRepository<Picture, String> {

}
