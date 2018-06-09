package io.orvisual.api.controller;

import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import io.orvisual.api.repository.PictureRepository;
import io.orvisual.api.service.FileStorageService;
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

    public FileController(FileStorageService storageService, PictureRepository pictureRepository) {
        this.storageService = storageService;
        this.pictureRepository = pictureRepository;
    }

    @PostMapping
    public ResponseEntity<Picture> savePictureFile(@RequestParam("image") PictureFileItem fileItem) {
        Optional<Picture> optionalPicture = pictureRepository.findById(fileItem.getPictureItem().getChecksum());
        if (optionalPicture.isPresent()) {
            return ResponseEntity.ok(optionalPicture.get());
        } else {
            storageService.savePictureFileItem(fileItem);
            return ResponseEntity.status(HttpStatus.CREATED).body(pictureRepository.save(fileItem.getPictureItem()));
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

}
