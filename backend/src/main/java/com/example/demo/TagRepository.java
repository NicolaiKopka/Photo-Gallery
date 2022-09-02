package com.example.demo;

import com.example.demo.model.TagDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagDbModel, Long> {

    Optional<TagDbModel> findByTagName(String tagName);
}
