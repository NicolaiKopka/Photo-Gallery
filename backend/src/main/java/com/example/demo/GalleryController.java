package com.example.demo;

import com.example.demo.DTOs.*;
import com.example.demo.model.ImageDbModel;
import com.example.demo.model.TagDbModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.HTML;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin
@RestController
@RequestMapping("/api/photo-gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping("/all/images")
    public ResponseEntity<Collection<ImageDbModel>> getAllImages() {
        return ResponseEntity.ok(galleryService.getAllImages());
    }

    @GetMapping("/all/tags")
    public ResponseEntity<Collection<TagDbModel>> getAllTags() {
        return ResponseEntity.ok(galleryService.getAllTags());
    }
    @GetMapping("/all/tags/{image_id}")
    public ResponseEntity<?> getAllTagsByImageId(@PathVariable Long image_id) {
        try {
            List<TagDbModel> tagList = galleryService.getAllTagsByImageId(image_id);
            return ResponseEntity.ok(tagList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
    // TODO should be GET request
    @PostMapping("all/images/by-tags")
    public ResponseEntity<?> getAllImagesByTags(@RequestBody ImageByTagQueryDTO imageByTagQueryDTO) {
        try {
            Collection<ImageDbModel> allImagesByTags = galleryService.getAllImagesByTags(imageByTagQueryDTO);
            return ResponseEntity.ok(allImagesByTags);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some tags are not existing");
        }

    }
    @PostMapping("/add/image")
    public ResponseEntity<?> addImageToDatabase(@RequestBody AddImageDTO addImageDTO) {
        try {
            return ResponseEntity.ok(galleryService.addImageToDatabase(addImageDTO));
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/add/tag/{tagName}")
    public ResponseEntity<?> addTagToDatabase(@PathVariable String tagName) {
        try {
            TagDbModel tagDbModel = galleryService.addTagToDatabase(tagName);
            return ResponseEntity.status(HttpStatus.CREATED).body(tagDbModel);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/edit/image")
    public ResponseEntity<?> editImage(@RequestBody EditImageDTO editImageDTO) {
        try {
            return ResponseEntity.ok(galleryService.editImage(editImageDTO));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Image found");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteImageById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(galleryService.deleteImageById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Image found");
        }

    }

}
