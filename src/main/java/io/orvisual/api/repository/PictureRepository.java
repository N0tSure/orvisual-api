package io.orvisual.api.repository;

import io.orvisual.api.model.Picture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Created on 10 May, 2018.
 *
 * Provides persistent operations with {@link Picture}.
 *
 * @author Artemis A. Sirosh
 */
@RepositoryRestResource
public interface PictureRepository extends CrudRepository<Picture, String> {

    @RestResource(exported = false)
    @NonNull
    @Override
    <S extends Picture> S save(@NonNull S entity);

    @RestResource(exported = false)
    @NonNull
    @Override
    <S extends Picture> Iterable<S> saveAll(@NonNull Iterable<S> entities);
}
