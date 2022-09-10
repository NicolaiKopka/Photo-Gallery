package com.example.demo;

import com.example.demo.model.ImageDbModel;
import com.example.demo.model.TagDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.HTML;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagDbModel, Long> {

    Optional<TagDbModel> findByTagName(String tagName);
    @Query("select t from TagDbModel t join t.imageIds i where i.id = :imageId")
    List<TagDbModel> findAllByImageId(@Param("imageId")Long imageId);

}
