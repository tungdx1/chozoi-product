package com.chozoi.product.app.dtos;

import com.chozoi.product.data.request.AttributeVariant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
public class VariantDTO {
    private Long id;

    @Min(0)
    @Max(100000000000L)
    @NotNull
    @JsonProperty("sale_price")
    private Long salePrice;

    @Size(max = 2)
    private List<AttributeVariant> attributes;

    @JsonProperty("price")
    @Max(100000000000L)
    private Long price;

    @JsonProperty("sku")
    private String sku;

    @Valid
    private InventoryDTO inventory;


    @JsonProperty("image_id")
    private Long imageId;
}
