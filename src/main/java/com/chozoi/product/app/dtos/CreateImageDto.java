package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateImageDto {
    @NotNull
    @JsonProperty("product_id")
    private Long productId;

    @NotNull
    @JsonProperty("image_url")
    private String imageUrl;

    @NotNull
    @JsonProperty("sort")
    private Integer sort;
}
