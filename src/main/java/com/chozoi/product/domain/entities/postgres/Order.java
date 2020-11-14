package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.types.ShopOrderState;
import com.chozoi.product.domain.utils.GenericArrayUserType;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "order", schema = "products")
@TypeDef(name = "pg-enum", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "pg-array", typeClass = GenericArrayUserType.class)
public class Order {
  @Id
  private Long id;


  @Column(name = "product_variant_id")
  private Long variant;

  @Column(name = "product_id")
  private Long productId;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pgsql_enum")
  @Column(name = "state")
  private ShopOrderState state;

  @Column(name = "buyer_id")
  private Integer buyerId;

}
