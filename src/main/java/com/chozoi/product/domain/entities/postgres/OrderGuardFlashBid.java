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
@Table(name = "order_guard_flash_bid", schema = "sales")
@TypeDef(name = "pg-enum", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "pg-array", typeClass = GenericArrayUserType.class)
public class OrderGuardFlashBid {
  @Id
  private Long id;

  @Column(name = "auction_id")
  private Long auctionId;

  @Column(name = "phase_id")
  private Long phaseId;

  @Column(name = "buyer_id")
  private Long buyerId;

  @Column(name = "type")
  private String type;

  @Column(name = "buy_now_index")
  private Boolean buyNowIndex;
}
