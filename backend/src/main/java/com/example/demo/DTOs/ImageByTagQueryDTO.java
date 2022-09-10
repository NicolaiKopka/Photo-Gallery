package com.example.demo.DTOs;

import com.example.demo.model.TagDbModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageByTagQueryDTO {

    @JsonProperty("tag-names")
    private List<String> tagNames;

}
