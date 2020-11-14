package com.chozoi.product.app.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryResponse<T> {
    private T category;
}
