package com.example.demo;

import com.example.demo.DTOs.TagDbModelDTO;
import com.example.demo.model.AddTagDTO;
import com.example.demo.model.TagDbModel;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.loader.ResourceEntry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/photo-gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping("/all/images")
    public void getAllImages() {
        galleryService.getAllImages();
    }

    @GetMapping("/all/tags")
    public ResponseEntity<Collection<TagDbModelDTO>> getAllTags() {
        Collection<TagDbModel> allTags = galleryService.getAllTags();
        List<TagDbModelDTO> allTagDTOs = allTags.stream().map(tag -> {
            TagDbModelDTO tagDbModelDTO = new TagDbModelDTO();
            tagDbModelDTO.setTagName(tag.getTagName());
            tagDbModelDTO.setId(tag.getId());
            return tagDbModelDTO;
        }).toList();
        return ResponseEntity.ok(allTagDTOs);
    }

    @PostMapping("/add/image")
    public void addImageToDatabase() {
        galleryService.addImageToDatabase();
    }

    @PostMapping("/add/tag")
    public ResponseEntity<?> addTagToDatabase(@RequestBody AddTagDTO addTagDTO) {
        try {
            galleryService.addTagToDatabase(addTagDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(addTagDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

}
