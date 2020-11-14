package com.chozoi.product.app.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangeQuantityDTO {
    @NotNull
    private Long productId;

    @NotNull
    private Long variantId;

    @NotNull
    private Integer quantity;
}
