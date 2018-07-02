package io.orvisual.api.controller;

import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import io.orvisual.api.repository.PictureRepository;
import io.orvisual.api.service.FileStorageService;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * <p>
 * Created on 20.05.2018.
 * </p>
 *
 * Operates with {@link Picture} metadata object and image files. Supported operations:
 * {@code POST} for {@code /files}, and {@code GET and POST} for {@code /files/{checksum}}.
 *
 * @author Artemis A. Sirosh
 */
@Controller
@RequestMapping("/files")
public class FileController {

    private final FileStorageService storageService;
    private final PictureRepository pictureRepository;
    private final RepositoryEntityLinks entityLinks;

    public FileController(
            FileStorageService storageService, PictureRepository pictureRepository, RepositoryEntityLinks entityLinks
    ) {
        this.storageService = storageService;
        this.pictureRepository = pictureRepository;
        this.entityLinks = entityLinks;
    }


    @PostMapping
    public ResponseEntity<Resource<Picture>> savePictureFile(@RequestParam("image") PictureFileItem fileItem) {
        Optional<Picture> optionalPicture = pictureRepository.findById(fileItem.getPictureItem().getChecksum());
        if (optionalPicture.isPresent()) {
            return ResponseEntity.ok(createResource(optionalPicture.get()));
        } else {
            storageService.savePictureFileItem(fileItem);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createResource(pictureRepository.save(fileItem.getPictureItem())));
        }
    }

    @GetMapping("/{checksum}")
    public ResponseEntity<?> findPictureFile(@PathVariable String checksum) {
        Optional<Picture> optionalPicture = pictureRepository.findById(checksum);
        return optionalPicture.<ResponseEntity<?>>map(picture -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, picture.getMimeType())
                .body(storageService.resolvePictureResource(picture)))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    /**
     * Creates REST resource from {@link Picture} metadata instance.
     *
     * @param picture metadata object instance
     * @return picture {@link Resource}
     */
    private Resource<Picture> createResource(Picture picture) {
        Link pictureLink = entityLinks.linkFor(Picture.class).slash(picture.getChecksum()).withRel("picture");
        Link selfLink = entityLinks.linkFor(Picture.class).slash(picture.getChecksum()).withSelfRel();
        Link imageFileLink = ControllerLinkBuilder
                .linkTo(FileController.class)
                .slash(picture.getChecksum())
                .withRel("imageFile");

        return new Resource<>(picture, selfLink, pictureLink, imageFileLink);
    }

}
