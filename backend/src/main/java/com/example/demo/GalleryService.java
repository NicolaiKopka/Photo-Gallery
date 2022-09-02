package com.example.demo;

import com.example.demo.model.AddTagDTO;
import com.example.demo.model.TagDbModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.HTML;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;
    public void getAllImages() {

    }
    public Collection<TagDbModel> getAllTags() {
        return tagRepository.findAll();
    }

    public void addImageToDatabase() {

    }

    public void addTagToDatabase(AddTagDTO addTagDTO) {

        if(addTagDTO.getTagName() == null) {
            throw new IllegalArgumentException("Field must exists");
        }

        if(addTagDTO.getTagName().isBlank() || addTagDTO.getTagName().isEmpty()) {
            throw new IllegalArgumentException("Cannot leave field empty");
        }

        tagRepository.findByTagName(addTagDTO.getTagName().toLowerCase()).ifPresentOrElse( model -> {
            throw new IllegalArgumentException("Tag name already exists");
        }, () -> {
            TagDbModel tagDbModel = new TagDbModel();
            tagDbModel.setTagName(addTagDTO.getTagName().toLowerCase());
            tagRepository.save(tagDbModel);
        });
    }

}
