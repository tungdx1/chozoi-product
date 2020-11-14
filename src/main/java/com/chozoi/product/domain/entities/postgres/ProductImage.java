package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.ProductImageState;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@Table(name = "product_image", schema = "products")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
// @Where(clause = "state = 'PUBLIC'")
public class ProductImage implements Cloneable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id")
  @JsonIgnore
  private Product product;

  @Column(name = "product_variant_id")
  private Long productVariantId;

  @Column(name = "image_url")
  private String imageUrl;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pgsql_enum")
  @Column(name = "state")
  private ProductImageState state;

  @Column(name = "sort", columnDefinition = "int default 0")
  private Integer sort;

  @Column(name = "update_status", columnDefinition = "int default 0")
  private Boolean updateStatus;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  public static void setDelete(ProductImage productImage) {
    productImage.setState(ProductImageState.DELETED);
  }

  public static void setPublic(ProductImage image) {
    image.setState(ProductImageState.PUBLIC);
    image.setUpdateStatus(true);
  }

  public void inferProperties() {
    this.state = Objects.isNull(state) ? ProductImageState.PENDING : state;
    this.updateStatus = Objects.isNull(updateStatus) ? false : updateStatus;
    this.sort = Objects.isNull(sort) ? 0 : sort;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @SneakyThrows
  @Override
  public ProductImage clone() {
    try {
      return (ProductImage) super.clone();
    } catch (Exception e) {
      throw new Exception(e);
    }
  }
}
