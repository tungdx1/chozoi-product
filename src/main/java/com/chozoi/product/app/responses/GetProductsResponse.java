package com.chozoi.product.app.responses;

import com.chozoi.product.app.dtos.ProductDTO;
import lombok.Data;

import java.util.List;

@Data
public class GetProductsResponse {

    private final List<ProductDTO> products;
    private final Metadata metadata;

}
