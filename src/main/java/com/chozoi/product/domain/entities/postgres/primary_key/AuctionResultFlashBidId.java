package com.chozoi.product.domain.entities.postgres.primary_key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AuctionResultFlashBidId implements Serializable {
  @Column(name = "id")
  private Long id;

  @Column(name = "phase_id")
  private Long phaseId;
}
