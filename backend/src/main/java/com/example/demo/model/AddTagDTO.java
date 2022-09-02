package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddTagDTO {

    @JsonProperty("tag-name")
    private String tagName;

}
