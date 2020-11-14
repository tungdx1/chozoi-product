package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.data.request.AttributeProduct;
import com.chozoi.product.data.request.ProductClassifier;
import com.chozoi.product.domain.entities.postgres.types.*;
import com.chozoi.product.domain.utils.GenericArrayUserType;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.chozoi.product.domain.utils.ProductUtils;
import com.chozoi.product.domain.values.content.ProductContent;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@Table(name = "product", schema = "products")
@TypeDef(name = "pg-enum", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "pg-array", typeClass = GenericArrayUserType.class)
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
public class Product extends ProductContent implements Cloneable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "private_code")
  private String privateCode;

  @ManyToOne
  @JoinColumn(name = "shop_id")
  private Shop shop;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @Type(type = "pg-array")
  @Column(name = "shipping_partner_ids")
  private Integer[] shippingPartnerIds;

  @Type(type = "pg-array")
  @Column(name = "packing_size")
  private Integer[] packingSize;

  @Column(name = "weight")
  private Integer weight;

  @Column(name = "sku")
  private String sku;

  @Column(name = "currency")
  private String currency;

  @Column(name = "description")
  private String description;

  @Type(type = "string-array")
  @Column(name = "videos", columnDefinition = "text[]")
  private String[] videos;

  @Column(name = "description_pinking")
  private String descriptionPinking;

  @Column(name = "description_pinkingin")
  private String descriptionPinkingIn;

  @Column(name = "description_pinkingout")
  private String descriptionPinkingOut;

  @Column(name = "private_description")
  private String privateDescription;

  @Column(name = "free_ship_status")
  private Boolean freeShipStatus;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pg-enum")
  @Column(nullable = false, columnDefinition = "product_type", name = "type")
  private ProductType type;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pg-enum")
  @Column(nullable = false, columnDefinition = "product_condition", name = "condition")
  private ProductCondition condition;

  @Column(name = "is_quantity_limited")
  private Boolean isQuantityLimited;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pg-enum")
  @Column(nullable = false, columnDefinition = "product_state", name = "state")
  private ProductState state;

  @Type(type = "pg-jsonb")
  @Column(name = "classifiers", columnDefinition = "jsonb")
  private List<ProductClassifier> classifiers;

  @Type(type = "pg-jsonb")
  @Column(name = "attributes", columnDefinition = "jsonb")
  private List<AttributeProduct> attributes;

  @Column(name = "auto_public")
  private Boolean autoPublic;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  // relation
  @OneToOne @PrimaryKeyJoinColumn private Auction auction;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  @Fetch(FetchMode.SUBSELECT)
  private List<ProductImage> images;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  private List<ProductVariant> variants;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  @Fetch(FetchMode.SUBSELECT)
  private List<Promotion> promotions;

  @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
  private ProductStats stats;

  @Transient private List<Category> categories;

  @Transient private ProductReportIssue reportIssues;

  public void inferProperties() {
    this.currency = "VND";
    this.name = this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
    if (type == ProductType.CLASSIFIER) isQuantityLimited = true;
    if (variants != null) if (!isQuantityLimited) variants.forEach(ProductVariant::isLimitedQuantity);

    if (!CollectionUtils.isEmpty(promotions)) promotions.forEach(promotion -> promotion.setProduct(Product.this));
    if (!CollectionUtils.isEmpty(images)) images.forEach(ProductImage::inferProperties);

    if (ProductUtils.AUCTION_TYPE.contains(type) && !Objects.isNull(auction)) {
      Product product;
      if (id == null) product = Product.this;
      else {
        product = new Product();
        product.setId(id);
      }
      auction.setProduct(product);
      auction.setResult(null);
      if (variants != null) variants.forEach(
              variant -> {
//                  variant.getInventory().setInitialQuantity(0);
//                  variant.getInventory().setInQuantity(1);
                ProductUtils.setInventoryAuction(type, variant);
              });
      ProductAuctionState state =
          ObjectUtils.defaultIfNull(auction.getState(), ProductAuctionState.WAITING);
      auction.setState(state);
      auction.setRefusePayment(false);
    } else auction = null;

    //      variants.forEach(variant -> variant.getInventory().setProductId(Product.this.id));
    if (!CollectionUtils.isEmpty(variants)) variants.forEach(
            variant -> {
              Product product;
              ProductVariant variant1;
              if (id == null) product = Product.this;
              else {
                product = new Product();
                product.setId(id);
              }
              variant.setProduct(product);
              if (variant.getState() == null) variant.setState(VariantState.PUBLIC);
              Long price = ObjectUtils.defaultIfNull(variant.getPrice(), 0L);
              variant.setPrice(price);
              // set inventory
              if (variant.getId() == null) variant1 = variant;
              else {
                variant1 = new ProductVariant();
                variant1.setId(id);
              }
              variant.getInventory().setVariant(variant1);
            });

    if (!CollectionUtils.isEmpty(images)) images.forEach(
            image -> {
                Product product;
                if (id == null) product = Product.this;
                else {
                    product = new Product();
                    product.setId(id);
                }
                image.inferProperties();
                image.setProduct(product);
            });

    if (stats != null) {
      Product product;
      if (id == null) product = Product.this;
      else {
        product = new Product();
        product.setId(id);
      }
      stats.setProduct(product);
    }
    ;

    if (!CollectionUtils.isEmpty(variants)) variants.forEach(ProductVariant::inferProperties);
    if (freeShipStatus == null) freeShipStatus = false;
  }

  @SneakyThrows
  @Override
  public Product clone() throws CloneNotSupportedException {
    try {
      return (Product) super.clone();
    } catch (Exception e) {
      throw new Exception(e);
    }
  }

  public void fixRecursive() {
    variants.forEach(
        variant1 -> {
          ProductVariant productVariant = new ProductVariant();
          productVariant.setId(variant1.getId());
          variant1.getInventory().setVariant(productVariant);
        });
  }
}
