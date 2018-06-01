package io.orvisual.api.controller;

import io.orvisual.api.model.Picture;
import io.orvisual.api.repository.PictureRepository;
import io.orvisual.api.service.FileStorageService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * Created on 31 May, 2018.
 *
 * This controller customize {@literal DELETE} operation of Rest resource {@link Picture}
 *
 * @author Artemis A. Sirosh
 */
@RepositoryRestController
public class PictureCustomController {

    private final PictureRepository pictureRepository;
    private final FileStorageService storageService;

    public PictureCustomController(PictureRepository pictureRepository, FileStorageService storageService) {
        this.pictureRepository = pictureRepository;
        this.storageService = storageService;
    }

    @DeleteMapping("/pictures/{checksum}")
    public ResponseEntity<Picture> deletePicture(@PathVariable String checksum) {
        return pictureRepository.findById(checksum)
                .<ResponseEntity<Picture>>map(picture -> {
                    pictureRepository.delete(picture);
                    storageService.deleteFile(picture);
                    return ResponseEntity.noContent().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
