package com.example.demo.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddImageDTO {

    @JsonProperty("image-url")
    private String imageUrl;

    @JsonProperty("tag-names")
    private List<String> tagNames;

}
