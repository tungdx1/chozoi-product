package com.chozoi.product.app.dtos.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.AttributeEs;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
public class VariantsDTO {
    private Long id;
    private Long price;
    private Long salePrice;

    @Field(type = FieldType.Text)
    private String sku;

    private List<AttributeEs> attributes;
    private InventoryDTO inventory;

    private Long createdAt;
    private Long updatedAt;
}
