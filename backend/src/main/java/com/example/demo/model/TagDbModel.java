package com.example.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Data
public class TagDbModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "tag_name")
    private String tagName;

    @ManyToMany
    @JoinTable(
            name = "image_ids",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private Set<ImageDbModel> imageIds = new HashSet<>();


}
