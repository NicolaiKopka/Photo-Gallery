package com.example.demo.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EditImageDTO {

    @JsonProperty("image-id")
    private String id;

    @JsonProperty("tag-names")
    private List<String> tagNames;

}
