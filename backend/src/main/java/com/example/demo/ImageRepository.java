package com.example.demo;

import com.example.demo.model.ImageDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;

@Repository
public interface ImageRepository extends JpaRepository<ImageDbModel, Integer> {


}
