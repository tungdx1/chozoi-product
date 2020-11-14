package com.chozoi.product.app.dtos.elasticsearch;

import com.chozoi.product.app.dtos.ShopDTO;
import com.chozoi.product.domain.entities.elasticsearch.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class ShopProductDTO {
    private Long id;
    private String name;
    private Long price;
    private Long salePrice;
    private ProductStatsEs stats;
    private AuctionEs auction;
    private Long soldQuantity;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long remainingQuantity;

    private String type;
    private ShopDTO shop;
    private String description;
    private List<ClassifiersEs> classifiers;
    private List<ImageEs> images;
    private PromotionEs promotion;
    private List<VariantsDTO> variants;
}
