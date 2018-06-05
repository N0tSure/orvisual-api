package io.orvisual.api.controller;

import io.orvisual.api.model.Picture;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

/**
 * <p>
 * Created on 01.06.2018.
 * </p>
 *
 * This implementation of {@link ResourceProcessor}, provide link to image file for
 * {@link Picture} resource.
 *
 * @author Artemis A. Sirosh
 */
@Component
public class PictureResourceProcessor implements ResourceProcessor<Resource<Picture>> {

    @Override
    public Resource<Picture> process(Resource<Picture> resource) {
        resource.add(new Link(
                MvcUriComponentsBuilder.fromMethodName(
                        FileController.class,
                        "findPictureFile",
                        resource.getContent().getChecksum()
                ).toUriString(),
                "imageFile"
        ));

        return resource;
    }
}
