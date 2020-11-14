package com.chozoi.product.domain.entities.mongodb;

import com.chozoi.product.data.request.AttributeProduct;
import com.chozoi.product.data.request.ImageVariant;
import com.chozoi.product.data.request.ProductClassifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Document(value = "products.product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class ProductMongo {

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
  private Boolean freeShipStatus;

  private Integer remainingQuantity;

  private Integer soldQuantity;

  private Integer soldQuantityFake;

  private String description;

  private String descriptionPinking;

  private String descriptionPinkingin;

  private String descriptionPinkingout;

  private String privateDescription;

  private List<String> videos;

  private String type;

  private Boolean isQuantityLimited;

  private String state;

  private List<ProductClassifier> classifiers;

  private List<AttributeProduct> attributes;

  private Category category;

  private List<Category> categories;

  private List<ProductImage> images;

  private Shop shop;

  private AuctionMongo auction;

  private Promotion promotion;

  private List<ProductVariant> variants;

  private ProductStatsMongo stats;

  private Long createdAt;

  private Long updatedAt;

  @JsonProperty(value = "image_variants", access = JsonProperty.Access.READ_ONLY)
  private List<ImageVariant> imageVariants() {
    List<ImageVariant> imageVariants = new ArrayList<>();
    if ( !CollectionUtils.isEmpty(images) ) images.forEach(
            image -> {
              ImageVariant imageVariant = new ImageVariant();
              List<String> arrOfStr = Arrays.asList(image.getImageUrl().split("product", 2));
              if ( arrOfStr.size() > 1 ) {
                String image_65 = arrOfStr.get(0) + "product/65x65" + arrOfStr.get(1);
                String image_350 = arrOfStr.get(0) + "product/350x350" + arrOfStr.get(1);
                String image_600 = arrOfStr.get(0) + "product/600x600" + arrOfStr.get(1);
                imageVariant.setImage_350(image_350);
                imageVariant.setImage_65(image_65);
                imageVariant.setImage_600(image_600);
                imageVariant.setId(image.getId());
                imageVariants.add(imageVariant);
              }
            });
    return imageVariants;
  }
}
