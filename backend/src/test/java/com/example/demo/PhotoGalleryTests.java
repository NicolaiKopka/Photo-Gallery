package com.example.demo;

import com.example.demo.DTOs.AddImageDTO;
import com.example.demo.model.ImageDbModel;
import com.example.demo.model.TagDbModel;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
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

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PhotoGalleryTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Container
	public static PostgreSQLContainer container = new PostgreSQLContainer()
			.withDatabaseName("postgresTest")
			.withUsername("username")
			.withPassword("password");

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", container::getJdbcUrl);
		registry.add("spring.datasource.password", container::getPassword);
		registry.add("spring.datasource.username", container::getUsername);
	}
	@Test
	void contextLoads() {
	}

	@Test
	void shouldAddTagsAndImagesAndGetAllTagsAndImages() {

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



}
