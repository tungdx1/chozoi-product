package com.chozoi.product.data.response;

import com.chozoi.product.data.request.ImageVariant;
import com.chozoi.product.domain.entities.elasticsearch.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class ProductsPrivateResponse {
    private Long id;
    private ShopResponse shop;
    private String name;
    private String privateCode;
    private Long price;
    private Long salePrice;
    private AuctionEs auction;
    private String condition;
    private List<AttributeEs> attributes;
    private FacetEs facet;
    private List<ImageEs> images;
    private List<ProductVariantDetail> variants;
    private CategoryData category;
    private List<CategoryData> categories;
    private PromotionEs promotion;
    private String state;
    private ProductStatsEs stats;
    private String type;
    private Integer commentsCount;
    private String currency;
    private String description;
    private String descriptionPicking;
    private String descriptionPickingin;
    private String descriptionPickingout;
    private Boolean isQuantityLimited;
    private ProductReportIssue reportIssues;
    private List<Integer> packingSize;
    private List<Integer> sort;
    private List<Integer> shippingPartnerIds;
    private List<ClassifiersEs> classifiers;
    private Boolean isPublic;
    private String sku;
    private Long weight;
    private Long createdAt;
    private Long updatedAt;

    @JsonProperty(value = "image_variants", access = JsonProperty.Access.READ_ONLY)
    private List<ImageVariant> imageVariants() {
        List<ImageVariant> imageVariants = new ArrayList<>();
        if (!CollectionUtils.isEmpty(images)) {
            images.forEach(image -> {
                ImageVariant imageVariant = new ImageVariant();
                List<String> arrOfStr = Arrays.asList(image.getImageUrl().split("product", 2));
                String image_180 = arrOfStr.size() > 1 ? (arrOfStr.get(0) + "product/180x180" + arrOfStr.get(1)) : String.valueOf(image);
                String image_65 = arrOfStr.size() > 1 ? (arrOfStr.get(0) + "product/65x65" + arrOfStr.get(1)) : String.valueOf(image);
                imageVariant.setImage_65(image_65);
                imageVariant.setImage_180(image_180);
                imageVariant.setId(image.getId());
                imageVariants.add(imageVariant);
            });
        }
        return imageVariants;
    }
}
