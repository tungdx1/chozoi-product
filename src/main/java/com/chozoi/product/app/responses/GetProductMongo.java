package com.chozoi.product.app.responses;

import com.chozoi.product.domain.entities.mongodb.ProductMongo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductMongo {
    private ProductMongo product;
}

