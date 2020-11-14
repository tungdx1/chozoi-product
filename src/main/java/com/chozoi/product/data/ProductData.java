package com.chozoi.product.data;

import com.chozoi.product.data.request.AttributeProduct;
import com.chozoi.product.data.request.ProductClassifier;
import com.chozoi.product.domain.entities.postgres.*;
import com.chozoi.product.domain.entities.postgres.types.ProductCondition;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import lombok.Data;

import javax.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductData {
  private Long id;
  private String name;

  @JoinColumn(name = "shop_id")
  private Shop shop;

  private Category category;
  private Integer[] shippingPartnerIds;
  private Integer[] packingSize;
  private Integer weight;
  private String sku;
  private String currency;
  private String description;
  private String[] videos;
  private String descriptionPinking;
  private String descriptionPinkingIn;
  private String descriptionPinkingOut;
  private ProductType type;
  private ProductCondition condition;
  private Boolean isQuantityLimited;
  private ProductState state;
  private List<ProductClassifier> classifiers;
  private List<AttributeProduct> attributes;
  private Boolean autoPublic;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Auction auction;
  private List<ProductImage> images;
  private List<ProductVariant> variants;
  private List<Promotion> promotions;
  private ProductStats stats;
}
