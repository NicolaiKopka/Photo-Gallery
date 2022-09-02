package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "images")
@Data
public class ImageDbModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "imageUrl")
    private String imageUrl;

    @JsonIgnore
    @ManyToMany(mappedBy = "imageIds")
    private Set<TagDbModel> imageTags = new HashSet<>();

}
