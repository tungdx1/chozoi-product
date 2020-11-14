package com.chozoi.product.app.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SortDTO {
    private String field;
    private String sort;
}
