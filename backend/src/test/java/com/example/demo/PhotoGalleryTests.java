package com.example.demo;

import com.example.demo.DTOs.AddImageDTO;
import com.example.demo.DTOs.EditImageDTO;
import com.example.demo.DTOs.ImageByTagQueryDTO;
import com.example.demo.model.ImageDbModel;
import com.example.demo.model.TagDbModel;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PhotoGalleryTests {

	@Autowired
	private TestRestTemplate restTemplate;

//	@Container
//	public static PostgreSQLContainer container = new PostgreSQLContainer()
//			.withDatabaseName("postgresTest")
//			.withUsername("username")
//			.withPassword("password");

//	@DynamicPropertySource
//	static void properties(DynamicPropertyRegistry registry) {
//		registry.add("spring.datasource.url", container::getJdbcUrl);
//		registry.add("spring.datasource.password", container::getPassword);
//		registry.add("spring.datasource.username", container::getUsername);
//	}
	@Test
	@Order(0)
	void contextLoads() {
	}

	@Test
	@Order(1)
	void userAddsTagsAndImagesAndGetsAllTagsAndImages() {

		// user adds tags
		TagDbModel tagDbModelResponse1 = restTemplate.postForObject("/api/photo-gallery/add/tag/car"
				, new TagDbModel()
				, TagDbModel.class);
		Assertions.assertThat(tagDbModelResponse1.getTagName()).isEqualTo("car");

		TagDbModel tagDbModelResponse2 = restTemplate.postForObject("/api/photo-gallery/add/tag/mountain"
				, new TagDbModel()
				, TagDbModel.class);
		Assertions.assertThat(tagDbModelResponse2.getTagName()).isEqualTo("mountain");

		// user uploads images
		AddImageDTO addImageDTO1 = new AddImageDTO();
		addImageDTO1.setImageUrl("test1.url");
		addImageDTO1.setTagNames(new ArrayList<>(List.of("car", "summer")));

		AddImageDTO addImageDTO2 = new AddImageDTO();
		addImageDTO2.setImageUrl("test2.url");
		addImageDTO2.setTagNames(new ArrayList<>(List.of("mountain", "super", "car")));

		ImageDbModel imageDbModelResponse1 = restTemplate.postForObject("/api/photo-gallery/add/image"
				, addImageDTO1
				, ImageDbModel.class);
		Assertions.assertThat(imageDbModelResponse1.getImageUrl()).isEqualTo("test1.url");
		Assertions.assertThat(imageDbModelResponse1.getImageTags().size()).isEqualTo(2);

		ImageDbModel imageDbModelResponse2 = restTemplate.postForObject("/api/photo-gallery/add/image"
				, addImageDTO2
				, ImageDbModel.class);
		Assertions.assertThat(imageDbModelResponse2.getImageUrl()).isEqualTo("test2.url");
		Assertions.assertThat(imageDbModelResponse2.getImageTags().size()).isEqualTo(3);

		// extract TagModels that were created on image upload
		TagDbModel expectedTagForSummer = imageDbModelResponse1.getImageTags().get(1);
		TagDbModel expectedTagForSuper = imageDbModelResponse2.getImageTags().get(1);

		// user gets all tags
		TagDbModel[] allTagsResponse = restTemplate.getForObject("/api/photo-gallery/all/tags", TagDbModel[].class);
		Assertions.assertThat(allTagsResponse.length).isEqualTo(4);
		Assertions.assertThat(allTagsResponse).contains(tagDbModelResponse1, tagDbModelResponse2, expectedTagForSummer, expectedTagForSuper);

		// user gets all images
		ImageDbModel[] allImagesResponse = restTemplate.getForObject("/api/photo-gallery/all/images", ImageDbModel[].class);
		Assertions.assertThat(allImagesResponse.length).isEqualTo(2);
		Assertions.assertThat(allImagesResponse[0].getImageUrl()).isEqualTo("test1.url");
		Assertions.assertThat(allImagesResponse[1].getImageUrl()).isEqualTo("test2.url");
	}

	@Test
	@Order(2)
	void userGetsAllImagesByFilteredByTagsAndTagsByImageId() {

		// setup tag queries
		ImageByTagQueryDTO imageByTagQueryFor2Images = new ImageByTagQueryDTO();
		imageByTagQueryFor2Images.setTagNames(new ArrayList<>(List.of("car")));

		ImageByTagQueryDTO imageByTagQueryFor1Image = new ImageByTagQueryDTO();
		imageByTagQueryFor1Image.setTagNames(new ArrayList<>(List.of("car", "mountain")));

		// user gets images by tags
		ResponseEntity<ImageDbModel[]> twoImagesResponse = restTemplate.exchange("/api/photo-gallery/all/images/by-tags",
				HttpMethod.POST,
				new HttpEntity<>(imageByTagQueryFor2Images, new HttpHeaders()),
				ImageDbModel[].class);

		Assertions.assertThat(twoImagesResponse.getBody().length).isEqualTo(2);
		Assertions.assertThat(twoImagesResponse.getBody()[0].getImageUrl()).isEqualTo("test1.url");
		Assertions.assertThat(twoImagesResponse.getBody()[1].getImageUrl()).isEqualTo("test2.url");

		ResponseEntity<ImageDbModel[]> oneImageResponse = restTemplate.exchange("/api/photo-gallery/all/images/by-tags",
				HttpMethod.POST,
				new HttpEntity<>(imageByTagQueryFor1Image, new HttpHeaders()),
				ImageDbModel[].class);

		Assertions.assertThat(oneImageResponse.getBody().length).isEqualTo(1);
		Assertions.assertThat(oneImageResponse.getBody()[0].getImageUrl()).isEqualTo("test2.url");

		// setup get image ids
		long imageId1 = twoImagesResponse.getBody()[0].getId();
		long imageId2 = twoImagesResponse.getBody()[1].getId();

		// user gets tags by image id
		TagDbModel[] tag1Response = restTemplate.getForObject("/api/photo-gallery/all/tags/" + imageId1, TagDbModel[].class);
		Assertions.assertThat(tag1Response.length).isEqualTo(2);
		Assertions.assertThat(tag1Response[0].getTagName()).isEqualTo("car");
		Assertions.assertThat(tag1Response[1].getTagName()).isEqualTo("summer");

		TagDbModel[] tag2Response = restTemplate.getForObject("/api/photo-gallery/all/tags/" + imageId2, TagDbModel[].class);
		Assertions.assertThat(tag2Response.length).isEqualTo(3);
		Assertions.assertThat(tag2Response[0].getTagName()).isEqualTo("mountain");
		Assertions.assertThat(tag2Response[1].getTagName()).isEqualTo("super");
		Assertions.assertThat(tag2Response[2].getTagName()).isEqualTo("car");
	}

	@Test
	@Order(3)
	void userEditsAndDeletesImage() {

		//setup image to edit and delete
		ImageDbModel[] allImages = restTemplate.getForObject("/api/photo-gallery/all/images", ImageDbModel[].class);
		long currentImageId = allImages[0].getId();

		// edit image
		EditImageDTO editImageDTO = new EditImageDTO();
		editImageDTO.setId(String.valueOf(currentImageId));
		editImageDTO.setTagNames(List.of("car", "super", "mumbai"));

		ResponseEntity<ImageDbModel> editResponse = restTemplate.exchange(
				"/api/photo-gallery/edit/image",
				HttpMethod.PUT,
				new HttpEntity<>(editImageDTO, new HttpHeaders()),
				ImageDbModel.class);

		Assertions.assertThat(editResponse.getBody().getImageUrl()).isEqualTo("test1.url");

		ImageDbModel[] allImagesAfterEdit = restTemplate.getForObject("/api/photo-gallery/all/images", ImageDbModel[].class);
		List<TagDbModel> editedImageTags = allImagesAfterEdit[0].getImageTags();
		Assertions.assertThat(editedImageTags.get(0).getTagName()).isEqualTo("car");
		Assertions.assertThat(editedImageTags.get(1).getTagName()).isEqualTo("super");
		Assertions.assertThat(editedImageTags.get(2).getTagName()).isEqualTo("mumbai");

		// test for new tag mumbai
		TagDbModel[] allTagsAfterEdit = restTemplate.getForObject("/api/photo-gallery/all/tags", TagDbModel[].class);
		Assertions.assertThat(allTagsAfterEdit.length).isEqualTo(5);
		Assertions.assertThat(allTagsAfterEdit[4].getTagName()).isEqualTo("mumbai");

		// delete image
		restTemplate.delete("/api/photo-gallery/delete/" + currentImageId);

		ImageDbModel[] allImagesAfterDelete = restTemplate.getForObject("/api/photo-gallery/all/images", ImageDbModel[].class);
		Assertions.assertThat(allImagesAfterDelete.length).isEqualTo(1);
		Assertions.assertThat(allImagesAfterDelete[0].getImageUrl()).isEqualTo("test2.url");

	}
}
