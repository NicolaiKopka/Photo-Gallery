package com.example.demo;

import com.example.demo.model.ImageDbModel;
import com.example.demo.model.TagDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ImageRepository extends JpaRepository<ImageDbModel, Integer> {

    Optional<ImageDbModel> findByImageUrl(String url);
    @Query("select i from ImageDbModel i join i.imageTags t " +
            "where t.id in :imageTags group by i.id having count(i.id) = :tagCount")
    List<ImageDbModel> findByImageTagsIn(@Param("imageTags") Collection<Long> imageTags
            , @Param("tagCount") Long count);

}
