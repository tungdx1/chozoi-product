package com.chozoi.product.app.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class ProductCategoryDTO {
    @NotNull
    private String type;

    private String sort;

    @NotNull
    private List<Integer> valueIds;
}
