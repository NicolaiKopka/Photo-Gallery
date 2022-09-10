package com.example.demo;

import com.example.demo.DTOs.AddImageDTO;
import com.example.demo.DTOs.AddTagDTO;
import com.example.demo.DTOs.ImageByTagQueryDTO;
import com.example.demo.model.ImageDbModel;
import com.example.demo.model.TagDbModel;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;
    public Collection<ImageDbModel> getAllImages() {
        return imageRepository.findAll();
    }
    public Collection<TagDbModel> getAllTags() {
        return tagRepository.findAll();
    }

    public ImageDbModel addImageToDatabase(AddImageDTO addImageDTO) {

        if(addImageDTO.getImageUrl() == null || addImageDTO.getTagNames() == null) {
            throw new IllegalArgumentException("All fields have to exist");
        }

        if(addImageDTO.getImageUrl().isEmpty() || addImageDTO.getImageUrl().isBlank() || addImageDTO.getTagNames().isEmpty()) {
            throw new IllegalArgumentException("Cannot leave field empty");
        }

        if(imageRepository.findByImageUrl(addImageDTO.getImageUrl()).isPresent()) {
            throw new IllegalArgumentException("Image url already exists");
        }

        ImageDbModel imageDbModel = new ImageDbModel();
        imageDbModel.setImageUrl(addImageDTO.getImageUrl());

        List<TagDbModel> addTagsList = new ArrayList<>();

        addImageDTO.getTagNames().forEach(tagName -> {
            Optional<TagDbModel> tagOptional = tagRepository.findByTagName(tagName.toLowerCase());
            if(tagOptional.isPresent()) {
                addTagsList.add(tagOptional.get());
            } else {
                addTagsList.add(addTagToDatabase(tagName));
            }

        });

        imageDbModel.addImageTags(addTagsList);
        return imageRepository.save(imageDbModel);
    }

    public TagDbModel addTagToDatabase(String tagName) {

        if(tagName == null) {
            throw new IllegalArgumentException("Tag name has to exist");
        }

        if(tagName.isBlank() || tagName.isEmpty()) {
            throw new IllegalArgumentException("No tag name present");
        }

        if(tagRepository.findByTagName(tagName.toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("Tag name already exists");
        }

        TagDbModel tagDbModel = new TagDbModel();
        tagDbModel.setTagName(tagName.toLowerCase());

        return tagRepository.save(tagDbModel);
    }

    public List<TagDbModel> getAllTagsByImageId(Long image_id) {
        List<TagDbModel> tagList = tagRepository.findAllByImageId(image_id);

        if(tagList.isEmpty()) {
            throw new IllegalArgumentException("No tags found for this id");
        }

        return tagList;
    }

    public Collection<ImageDbModel> getAllImagesByTags(ImageByTagQueryDTO imageByTagQueryDTO) {

        Collection<Long> allTags = new ArrayList<>();
        imageByTagQueryDTO.getTagNames().forEach(tagName -> {
            allTags.add(tagRepository.findByTagName(tagName.toLowerCase()).orElseThrow().getId());
        });

        return imageRepository.findByImageTagsIn(allTags, (long) allTags.size());
    }
}
