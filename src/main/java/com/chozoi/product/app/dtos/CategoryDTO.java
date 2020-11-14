package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CategoryDTO {

    private int id;

    private String name;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private Integer sort;

    private Integer level;
}
