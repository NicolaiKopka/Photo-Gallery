package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "images")
@EqualsAndHashCode
public class ImageDbModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Column(name = "image_url")
    private String imageUrl;
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "image_tag",
            joinColumns = @JoinColumn(name = "image_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<TagDbModel> imageTags = new ArrayList<>();

    public void addImageTags(List<TagDbModel> tags) {
        imageTags.addAll(tags);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<TagDbModel> getImageTags() {
        return imageTags;
    }

    public void setImageTags(List<TagDbModel> imageTags) {
        this.imageTags = imageTags;
    }
}
