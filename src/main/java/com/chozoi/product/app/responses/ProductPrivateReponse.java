package com.chozoi.product.app.responses;

import com.chozoi.product.data.request.AttributeProduct;
import com.chozoi.product.data.request.ImageVariant;
import com.chozoi.product.data.request.ProductClassifier;
import com.chozoi.product.data.response.ProductVariantDetail;
import com.chozoi.product.domain.entities.elasticsearch.AuctionEs;
import com.chozoi.product.domain.entities.postgres.*;
import com.chozoi.product.domain.entities.postgres.types.ProductCondition;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductPrivateReponse {

  private Long id;
  private String name;
  private String privateCode;
  private Shop shop;
  private Category category;
  private Integer[] shippingPartnerIds;
  private Integer[] packingSize;
  private Integer weight;
  private String sku;
  private String currency;
  private Boolean freeShipStatus;
  private String description;
  private String[] videos;

  @JsonProperty("description_picking")
  private String descriptionPinking;

  @JsonProperty("description_pickingin")
  private String descriptionPinkingIn;

  @JsonProperty("description_pickingout")
  private String descriptionPinkingOut;

  @JsonProperty("private_description")
  private String privateDescription;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pg-enum")
  private ProductType type;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pg-enum")
  private ProductCondition condition;

  @JsonProperty("is_quantity_limited")
  private Boolean isQuantityLimited;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pg-enum")
  private ProductState state;

  private List<ProductClassifier> classifiers;
  private List<AttributeProduct> attributes;

  @JsonProperty("auto_public")
  private Boolean autoPublic;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;

  private AuctionEs auction;

  private List<ProductImage> images;

  private List<ProductVariantDetail> variants;

  private List<Promotion> promotions;

  private ProductStats stats;

  private ProductReportIssue reportIssues;

  private List<Category> categories;
  private List<ImageVariant> imageVariants;
}
