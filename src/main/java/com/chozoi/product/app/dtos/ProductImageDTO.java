package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class ProductImageDTO {
    private Long id;

    @JsonProperty("image_url")
    @NotNull
    @Length(max = 1000)
    private String imageUrl;

    private Integer sort;

}
