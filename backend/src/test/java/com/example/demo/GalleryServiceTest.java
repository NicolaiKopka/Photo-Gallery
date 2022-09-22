package com.example.demo;

import com.example.demo.DTOs.AddImageDTO;
import com.example.demo.DTOs.AddTagDTO;
import com.example.demo.DTOs.EditImageDTO;
import com.example.demo.DTOs.ImageByTagQueryDTO;
import com.example.demo.model.ImageDbModel;
import com.example.demo.model.TagDbModel;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.nullness.Opt;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testcontainers.shaded.org.yaml.snakeyaml.nodes.Tag;

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

    // deleteImageById()

    @Test
    void shouldFailIfNoImageExistsOnDelete() {

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        Mockito.when(imageRepository.findById(1L)).thenReturn(Optional.empty());

        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Assertions.assertThatThrownBy(() -> galleryService.deleteImageById("1"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldDeleteImageById() {

        ImageDbModel imageDbModel = new ImageDbModel();
        imageDbModel.setImageUrl("test1.url");

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        Mockito.when(imageRepository.findById(1L)).thenReturn(Optional.of(imageDbModel));

        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);
        galleryService.deleteImageById("1");

        Mockito.verify(imageRepository).delete(imageDbModel);
    }

    // editImage()
    @Test
    void shouldFailOnRequestBodyAttributeMismatch() {
        EditImageDTO editImageDTOIdNull = new EditImageDTO();
        editImageDTOIdNull.setTagNames(new ArrayList<>(List.of("tag")));

        EditImageDTO editImageDTOTagsNull = new EditImageDTO();
        editImageDTOTagsNull.setId("1");

        EditImageDTO editImageDTOIdEmpty = new EditImageDTO();
        editImageDTOIdEmpty.setId("");
        editImageDTOIdEmpty.setTagNames(new ArrayList<>(List.of("tag")));

        EditImageDTO editImageDTOIdBlank = new EditImageDTO();
        editImageDTOIdBlank.setId(" ");
        editImageDTOIdBlank.setTagNames(new ArrayList<>(List.of("tag")));

        EditImageDTO editImageDTOTagsEmpty = new EditImageDTO();
        editImageDTOTagsEmpty.setTagNames(new ArrayList<>(Collections.emptyList()));
        editImageDTOTagsEmpty.setId("1");

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Assertions.assertThatThrownBy(() -> galleryService.editImage(editImageDTOIdNull))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("All fields have to exist");

        Assertions.assertThatThrownBy(() -> galleryService.editImage(editImageDTOTagsNull))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("All fields have to exist");

        Assertions.assertThatThrownBy(() -> galleryService.editImage(editImageDTOIdEmpty))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Cannot leave field empty");

        Assertions.assertThatThrownBy(() -> galleryService.editImage(editImageDTOIdBlank))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Cannot leave field empty");

        Assertions.assertThatThrownBy(() -> galleryService.editImage(editImageDTOTagsEmpty))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Cannot leave field empty");
    }

    @Test
    void shouldFailIfNoImageExistsOnEdit() {

        EditImageDTO editImageDTO = new EditImageDTO();
        editImageDTO.setId("1");
        editImageDTO.setTagNames(new ArrayList<>(List.of("tag")));

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        Mockito.when(imageRepository.findById(1L)).thenReturn(Optional.empty());

        TagRepository tagRepository = Mockito.mock(TagRepository.class);

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);

        Assertions.assertThatThrownBy(() -> galleryService.editImage(editImageDTO))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldEditAndUpdateImage() {

        EditImageDTO editImageDTO = new EditImageDTO();
        editImageDTO.setId("1");
        editImageDTO.setTagNames(new ArrayList<>(List.of("Car", "Mountain", "Super", "Best")));

        TagDbModel tagCar = new TagDbModel();
        tagCar.setTagName("car");

        TagDbModel tagMountain = new TagDbModel();
        tagMountain.setTagName("mountain");

        TagDbModel tagSuper = new TagDbModel();
        tagSuper.setTagName("super");

        TagDbModel tagBest = new TagDbModel();
        tagBest.setTagName("best");

        ImageDbModel imageDbModel = new ImageDbModel();
        imageDbModel.setId(1L);
        imageDbModel.setImageUrl("test.url");
        imageDbModel.setImageTags(new ArrayList<>(List.of(tagCar, tagMountain)));

        ImageDbModel expectedImage = new ImageDbModel();
        expectedImage.setId(1L);
        expectedImage.setImageUrl("test.url");
        expectedImage.setImageTags(new ArrayList<>(List.of(tagCar, tagMountain, tagSuper, tagBest)));

        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        Mockito.when(imageRepository.findById(1L)).thenReturn(Optional.of(imageDbModel));

        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.findByTagName("car")).thenReturn(Optional.of(tagCar));
        Mockito.when(tagRepository.findByTagName("mountain")).thenReturn(Optional.of(tagMountain));
        Mockito.when(tagRepository.findByTagName("super")).thenReturn(Optional.of(tagSuper));
        Mockito.when(tagRepository.findByTagName("best")).thenReturn(Optional.empty());
        Mockito.when(tagRepository.save(tagBest)).thenReturn(tagBest);

        GalleryService galleryService = new GalleryService(imageRepository, tagRepository);
        galleryService.editImage(editImageDTO);

        Mockito.verify(imageRepository).save(expectedImage);
    }




}