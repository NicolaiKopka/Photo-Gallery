package com.example.demo;

import com.example.demo.model.AddTagDTO;
import com.example.demo.model.TagDbModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GalleryServiceTest {

    @Test
    void shouldFailIfTagFieldIsNullOrEmptyOnCreation() {

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        AddTagDTO addTagDTOEmpty = new AddTagDTO();
        addTagDTOEmpty.setTagName(" ");

        AddTagDTO addTagDTONull = new AddTagDTO();

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Assertions.assertThatThrownBy(() -> galleryService.addTagToDatabase(addTagDTOEmpty))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Cannot leave field empty");

        Assertions.assertThatThrownBy(() -> galleryService.addTagToDatabase(addTagDTONull))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Field must exists");

    }

    @Test
    void shouldFailIfTagAlreadyExistsOnCreation() {

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        AddTagDTO addTagDTO = new AddTagDTO();
        addTagDTO.setTagName("tag");

        TagDbModel tagDbModel = new TagDbModel();
        tagDbModel.setTagName("tag");

        Mockito.when(tagRepository.findByTagName("tag")).thenReturn(Optional.of(tagDbModel));

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Assertions.assertThatThrownBy(() -> galleryService.addTagToDatabase(addTagDTO))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Tag name already exists");
    }

    @Test
    void shouldCreateTagWithLowerCase() {

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        AddTagDTO addTagDTO = new AddTagDTO();
        addTagDTO.setTagName("TagNaMe");

        TagDbModel tagDbModel = new TagDbModel();
        tagDbModel.setTagName("tagname");

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        galleryService.addTagToDatabase(addTagDTO);

        Mockito.verify(tagRepository).save(tagDbModel);
    }

}