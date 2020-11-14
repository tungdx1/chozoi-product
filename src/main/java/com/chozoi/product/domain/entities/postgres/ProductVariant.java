package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.data.request.AttributeVariant;
import com.chozoi.product.domain.entities.abstracts.ProductVariantAbstract;
import com.chozoi.product.domain.entities.postgres.types.VariantState;
import com.chozoi.product.domain.utils.GenericArrayUserType;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@Table(name = "product_variant", schema = "products")
@TypeDef(name = "pg-enum", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "pg-array", typeClass = GenericArrayUserType.class)
@Where(clause = "state = 'PUBLIC'")
public class ProductVariant extends ProductVariantAbstract implements Cloneable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id")
  @JsonIgnore
  private Product product;

  @Column(name = "price")
  private Long price;

  @Column(name = "sale_price")
  private Long salePrice;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pg-enum")
  @Column(nullable = false, columnDefinition = "variant_state", name = "state")
  private VariantState state;

  @Column(name = "sku")
  private String sku;

  @Type(type = "jsonb")
  @Column(name = "attributes", columnDefinition = "jsonb")
  private List<AttributeVariant> attributes;

  @Column(name = "image_id")
  private Long imageId;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  // relation
  @OneToOne(mappedBy = "variant", cascade = CascadeType.ALL)
  @JoinColumn(referencedColumnName = "id")
  private Inventory inventory;

  @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
  @Fetch(FetchMode.SUBSELECT)
  private List<InventoryHistory> inventoryHistory;

  @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
  @Fetch(FetchMode.SUBSELECT)
  private List<Order> orders;

  public void inferProperties() {
    if (inventory != null) {
      inventory.setProduct(ProductVariant.this.product);
      inventory.setVariant(ProductVariant.this);
      inventory.setId(ProductVariant.this.id);
      inventory.setInitialQuantity(0);
      if (Objects.isNull(createdAt)) createdAt = LocalDateTime.now();
      if (Objects.isNull(updatedAt)) updatedAt = LocalDateTime.now();
    } else {
      ProductVariant variant;
      if (id == null) variant = ProductVariant.this;
      else {
        variant = new ProductVariant();
        variant.setId(id);
      }
      inventory.setVariant(variant);
    }
    if (!CollectionUtils.isEmpty(inventoryHistory))
        inventoryHistory.forEach(history -> history.setVariant(ProductVariant.this));
    price = ObjectUtils.defaultIfNull(price, 0L);
  }

  public void isLimitedQuantity() {
    inventory.setInitialQuantity(0);
    inventory.setInQuantity(0);
  }

  @Override
  public ProductVariant clone() throws CloneNotSupportedException {
    return (ProductVariant) super.clone();
  }
}
