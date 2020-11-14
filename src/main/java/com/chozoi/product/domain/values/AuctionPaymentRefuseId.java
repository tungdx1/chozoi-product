package com.chozoi.product.domain.values;

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
public class AuctionPaymentRefuseId implements Serializable {
    private static final long serialVersionUID = -9137465516547688848L;
    @Column(name = "phase_id")
    private Long phaseId;

    @Column(name = "auction_id")
    private Long auctionId;
}
