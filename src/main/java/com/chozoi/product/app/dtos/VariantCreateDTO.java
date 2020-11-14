package com.chozoi.product.app.dtos;

import com.chozoi.product.data.request.AttributeVariant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class VariantCreateDTO {
    private Long id;

    @Min(0)
    @JsonProperty("sale_price")
    private Long salePrice;

    @Size(max = 2)
    private List<AttributeVariant> attributes;

    @JsonProperty("price")
    private Long price;

    @JsonProperty("sku")
    private String sku;

    private InventoryCreateDTO inventory;


    @JsonProperty("image_id")
    private Long imageId;
}
