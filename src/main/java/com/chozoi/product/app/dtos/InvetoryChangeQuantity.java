package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class InvetoryChangeQuantity {

    @JsonProperty("quantity")
    @NotNull
    @Min(1)
    @Max(999)
    private Integer quantity;

    @NotNull
    private Long variantId;
}
