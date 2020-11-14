package com.chozoi.product.data.response;

import com.chozoi.product.data.request.ImageVariant;
import com.chozoi.product.domain.entities.elasticsearch.AuctionEs;
import com.chozoi.product.domain.entities.elasticsearch.PromotionEs;
import com.chozoi.product.domain.utils.Calculate;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class ProductsPublicResponse2 {
  private Long id;
  private ShopResponse shop;
  private String name;
  private AuctionEs auction;
  private String condition;
  private CategoryData category;
  private PromotionEs promotion;
  private ProductStatsResponse stats;
  private String type;
  private List<ProductImageResponse> images;
  private Boolean isQuantityLimited;
  private Boolean isLiked;

  private List<ProductVariantDetail> variants;
  private Long price;;
  private Long salePrice;;
  private String description;

  @JsonProperty(value = "sold_quantity", access = JsonProperty.Access.READ_ONLY)
  private Integer soldQuantity() {
    return Calculate.soldQuantity(variants);
  }

  @JsonProperty(value = "remaining_quantity", access = JsonProperty.Access.READ_ONLY)
  private Integer remainingQuantity() {
    return Calculate.remainingQuantity(variants);
  }

  @JsonProperty(value = "image_variants")
  private List<ImageVariant> imageVariants() {
    List<ImageVariant> imageVariants = new ArrayList<>();
    if (!CollectionUtils.isEmpty(images)) images.forEach(
            image -> {
                ImageVariant imageVariant = new ImageVariant();
                List<String> arrOfStr = Arrays.asList(image.getImageUrl().split("product", 2));
                if (arrOfStr.size() > 1) {
                    String image_350 = arrOfStr.get(0) + "product/350x350" + arrOfStr.get(1);
                    String image_180 = arrOfStr.get(0) + "product/180x180" + arrOfStr.get(1);
                    String image_65 = arrOfStr.get(0) + "product/65x65" + arrOfStr.get(1);
                    imageVariant.setImage_65(image_65);
                    imageVariant.setImage_180(image_180);
                    imageVariant.setImage_350(image_350);
                    imageVariant.setId(image.getId());
                    imageVariants.add(imageVariant);
                }
            });

    return imageVariants;
  }
}
