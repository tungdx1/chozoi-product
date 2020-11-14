package com.chozoi.product.domain.entities.elasticsearch;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;


@Document(indexName = "chozoi_products_draft", type = "_doc")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class ProductDraftEs {
    @Id
    private Long id;
    private String name;
    private String privateCode;
    private List<Integer> shippingPartnerIds;
    private List<Integer> packingSize;
    private Long price;
    private Long salePrice;
    private String condition;
    private String sku;
    private String currency;
    private Integer weight;
    private boolean autoPublic;
    private Object promotions;
    private String[] videos;
    //    private Integer remainingQuantity;
//    private Integer soldQuantity;
    private String description;
    private String descriptionPinking;
    private String descriptionPinkingIn;
    private String descriptionPinkingOut;
    private String privateDescription;
    private String type;
    private Boolean isQuantityLimited;
    private String state;
    private List<ClassifiersEs> classifiers;
    private List<AttributeEs> attributes;
    private CategoryEs category;
    private List<CategoriesEs> categories;
    private List<ImageEs> images;
    private ShopEs shop;
    private AuctionEs auction;
    private PromotionEs promotion;
    private List<VariantEs> variants;
    private Object facet;
    private ProductReportIssue reportIssues;
    private ProductStatsEs stats;
    private Boolean isPublic;
//    private List<Integer> createdAt;
//    private List<Integer> updatedAt;


}
