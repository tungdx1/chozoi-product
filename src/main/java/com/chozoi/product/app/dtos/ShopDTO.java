package com.chozoi.product.app.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ShopDTO {

    @NotNull
    private Integer id;

    private String name;
}
