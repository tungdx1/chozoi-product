package com.chozoi.product.app.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesResponse<T> {
    private List<T> categories;
    private Metadata metadata;
}
