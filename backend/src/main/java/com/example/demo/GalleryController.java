package com.example.demo;

import com.example.demo.model.AddTagDTO;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.loader.ResourceEntry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/photo-gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping("/all")
    public void getAllImages() {
        galleryService.getAllImages();
    }

    @PostMapping("/add")
    public void addImageToDatabase() {
        galleryService.addImageToDatabase();
    }

    @PutMapping("/add/tag")
    public ResponseEntity<?> addTagToDatabase(@RequestBody AddTagDTO addTagDTO) {
        try {
            galleryService.addTagToDatabase(addTagDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(addTagDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

}
