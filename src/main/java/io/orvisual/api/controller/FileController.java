package io.orvisual.api.controller;

import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import io.orvisual.api.repository.PictureRepository;
import io.orvisual.api.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * <p>
 * Created on 20.05.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
@Controller
@RequestMapping("/images")
public class FileController {

    private final FileStorageService storageService;
    private final PictureRepository pictureRepository;

    public FileController(FileStorageService storageService, PictureRepository pictureRepository) {
        this.storageService = storageService;
        this.pictureRepository = pictureRepository;
    }

    @PostMapping
    public ResponseEntity<Picture> saveImageFile(@RequestParam("image") PictureFileItem fileItem) {
        Optional<Picture> optionalPicture = pictureRepository.findById(fileItem.getPictureItem().getChecksum());
        if (optionalPicture.isPresent()) {
            return ResponseEntity.ok(optionalPicture.get());
        } else {
            storageService.savePictureFileItem(fileItem);
            return ResponseEntity.status(HttpStatus.CREATED).body(pictureRepository.save(fileItem.getPictureItem()));
        }
    }
}
