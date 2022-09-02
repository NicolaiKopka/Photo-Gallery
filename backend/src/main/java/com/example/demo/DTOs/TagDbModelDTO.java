package com.example.demo.DTOs;

import com.example.demo.model.TagDbModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagDbModelDTO {

    private long id;
    private String tagName;

}
