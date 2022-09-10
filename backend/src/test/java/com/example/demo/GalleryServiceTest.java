package com.example.demo;

import com.example.demo.DTOs.AddImageDTO;
import com.example.demo.DTOs.AddTagDTO;
import com.example.demo.DTOs.ImageByTagQueryDTO;
import com.example.demo.model.ImageDbModel;
import com.example.demo.model.TagDbModel;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.nullness.Opt;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.*;

class GalleryServiceTest {

    // getAllImages()
    @Test
    void shouldReturnListOfImages() {
        ImageDbModel imageDbModel1 = new ImageDbModel();
        imageDbModel1.setImageUrl("image1.com");
        ImageDbModel imageDbModel2 = new ImageDbModel();
        imageDbModel2.setImageUrl("image2.com");

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        Mockito.when(imageRepository.findAll()).thenReturn(new ArrayList<>(List.of(imageDbModel1, imageDbModel2)));

        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Collection<ImageDbModel> allTags = galleryService.getAllImages();

        Assertions.assertThat(allTags.size()).isEqualTo(2);
        Assertions.assertThat(allTags).contains(imageDbModel1, imageDbModel2);
    }

    // getAllTags()
    @Test
    void shouldReturnListOfTags() {
        TagDbModel tagDbModel1 = new TagDbModel();
        tagDbModel1.setTagName("tag1");
        TagDbModel tagDbModel2 = new TagDbModel();
        tagDbModel2.setTagName("tag2");

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);

        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.findAll()).thenReturn(new ArrayList<>(List.of(tagDbModel1, tagDbModel2)));

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Collection<TagDbModel> allTags = galleryService.getAllTags();

        Assertions.assertThat(allTags.size()).isEqualTo(2);
        Assertions.assertThat(allTags).contains(tagDbModel1, tagDbModel2);
    }

    // addImageToDatabase()
    @Test
    void shouldFailIfDTOFieldsAreNullBlankOrEmpty() {

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        AddImageDTO addImageDTOTagsNull = new AddImageDTO();
        addImageDTOTagsNull.setImageUrl("test.url");

        AddImageDTO addImageDTOUrlNull = new AddImageDTO();
        addImageDTOUrlNull.setTagNames(new ArrayList<>(List.of("tag")));

        AddImageDTO addImageDTOUrlBlank = new AddImageDTO();
        addImageDTOUrlBlank.setImageUrl("");
        addImageDTOUrlBlank.setTagNames(new ArrayList<>(List.of("tag")));

        AddImageDTO addImageDTOUrlEmpty = new AddImageDTO();
        addImageDTOUrlEmpty.setImageUrl("   ");
        addImageDTOUrlEmpty.setTagNames(new ArrayList<>(List.of("tag")));

        AddImageDTO addImageDTOTagsEmpty = new AddImageDTO();
        addImageDTOTagsEmpty.setImageUrl("test.url");
        addImageDTOTagsEmpty.setTagNames(new ArrayList<>(Collections.emptyList()));

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Assertions.assertThatThrownBy(() -> galleryService.addImageToDatabase(addImageDTOTagsNull))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("All fields have to exist");

        Assertions.assertThatThrownBy(() -> galleryService.addImageToDatabase(addImageDTOUrlNull))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("All fields have to exist");

        Assertions.assertThatThrownBy(() -> galleryService.addImageToDatabase(addImageDTOUrlBlank))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Cannot leave field empty");

        Assertions.assertThatThrownBy(() -> galleryService.addImageToDatabase(addImageDTOUrlEmpty))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Cannot leave field empty");

        Assertions.assertThatThrownBy(() -> galleryService.addImageToDatabase(addImageDTOTagsEmpty))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Cannot leave field empty");
    }

    @Test
    void shouldFailIfImageUrlAlreadyExists() {

        AddImageDTO addImageDTO = new AddImageDTO();
        addImageDTO.setImageUrl("test.url");
        addImageDTO.setTagNames(new ArrayList<>(List.of("tag")));

        ImageDbModel imageDbModel = new ImageDbModel();
        imageDbModel.setImageUrl("test.url");

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        Mockito.when(imageRepository.findByImageUrl("test.url")).thenReturn(Optional.of(imageDbModel));

        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Assertions.assertThatThrownBy(() -> galleryService.addImageToDatabase(addImageDTO))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Image url already exists");
    }

    @Test
    void shouldAddImageToDatabase() {

        TagDbModel tagDbModel1 = new TagDbModel();
        tagDbModel1.setTagName("tag1");

        TagDbModel tagDbModel2 = new TagDbModel();
        tagDbModel2.setTagName("tag2");

        ImageDbModel expected = new ImageDbModel();
        expected.setImageUrl("test.url");
        expected.setImageTags(List.of(tagDbModel1, tagDbModel2));

        AddImageDTO addImageDTO = new AddImageDTO();
        addImageDTO.setImageUrl("test.url");
        addImageDTO.setTagNames(List.of("Tag1", "Tag2"));

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);

        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.findByTagName("tag1")).thenReturn(Optional.of(tagDbModel1));
        Mockito.when(tagRepository.save(tagDbModel2)).thenReturn(tagDbModel2);

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);
        galleryService.addImageToDatabase(addImageDTO);

        Mockito.verify(imageRepository).save(expected);
    }

    // addTagToDatabase()
    @Test
    void shouldFailIfTagFieldIsNullOrEmptyOnCreation() {

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Assertions.assertThatThrownBy(() -> galleryService.addTagToDatabase(""))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("No tag name present");

        Assertions.assertThatThrownBy(() -> galleryService.addTagToDatabase("    "))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("No tag name present");

        Assertions.assertThatThrownBy(() -> galleryService.addTagToDatabase(null))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Tag name has to exist");
    }

    @Test
    void shouldFailIfTagAlreadyExistsOnCreation() {

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        TagDbModel tagDbModel = new TagDbModel();
        tagDbModel.setTagName("tag");

        Mockito.when(tagRepository.findByTagName("tag")).thenReturn(Optional.of(tagDbModel));

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Assertions.assertThatThrownBy(() -> galleryService.addTagToDatabase("TAG"))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Tag name already exists");
    }

    @Test
    void shouldCreateTagWithLowerCase() {

        TagDbModel tagDbModel = new TagDbModel();
        tagDbModel.setTagName("tagname");

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);

        TagRepository tagRepository = Mockito.mock(TagRepository.class);


        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        galleryService.addTagToDatabase("TaGnAme");

        Mockito.verify(tagRepository).findByTagName("tagname");
        Mockito.verify(tagRepository).save(tagDbModel);
    }

    // getAllTagsByImageId()
    @Test
    void shouldFailIfTagNotInDb() {

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);

        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.findAllByImageId(2L)).thenReturn(Collections.emptyList());

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Assertions.assertThatThrownBy(() -> galleryService.getAllTagsByImageId(2L))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("No tags found for this id");

    }
    @Test
    void shouldGetAllTagsByImageId() {

        TagDbModel tagDbModel1 = new TagDbModel();
        tagDbModel1.setTagName("tag1");

        TagDbModel tagDbModel2 = new TagDbModel();
        tagDbModel2.setTagName("tag2");

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);

        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.findAllByImageId(1L)).thenReturn(List.of(tagDbModel1, tagDbModel2));

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);
        List<TagDbModel> allTagsByImageId = galleryService.getAllTagsByImageId(1L);

        Assertions.assertThat(allTagsByImageId.size()).isEqualTo(2);
        Assertions.assertThat(allTagsByImageId).contains(tagDbModel1, tagDbModel2);
    }

    // getAllImagesByTags()
    @Test
    void shouldFailIfTagNameDoesNotExistInDb() {

        ImageByTagQueryDTO imageByTagQueryDTO = new ImageByTagQueryDTO();
        imageByTagQueryDTO.setTagNames(List.of("NoTag"));

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);

        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.findByTagName("NoTag")).thenReturn(Optional.empty());

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Assertions.assertThatThrownBy(() -> galleryService.getAllImagesByTags(imageByTagQueryDTO))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldGetAllImagesByTags() {

        ImageDbModel imageDbModel1 = new ImageDbModel();
        imageDbModel1.setImageUrl("test1.url");

        ImageDbModel imageDbModel2 = new ImageDbModel();
        imageDbModel2.setImageUrl("test2.url");

        Collection<ImageDbModel> expected = new ArrayList<>(List.of(imageDbModel1, imageDbModel2));

        ImageByTagQueryDTO imageByTagQueryDTO = new ImageByTagQueryDTO();
        imageByTagQueryDTO.setTagNames(List.of("Tag1", "Tag2"));

        TagDbModel tagDbModel1 = new TagDbModel();
        tagDbModel1.setId(1L);

        TagDbModel tagDbModel2 = new TagDbModel();
        tagDbModel2.setId(2L);

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        Mockito.when(imageRepository.findByImageTagsIn(List.of(1L, 2L), 2L))
                .thenReturn(List.of(imageDbModel1, imageDbModel2));

        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.findByTagName("tag1")).thenReturn(Optional.of(tagDbModel1));
        Mockito.when(tagRepository.findByTagName("tag2")).thenReturn(Optional.of(tagDbModel2));

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);
        Collection<ImageDbModel> actual = galleryService.getAllImagesByTags(imageByTagQueryDTO);

        Assertions.assertThat(actual).isEqualTo(expected);
    }




}