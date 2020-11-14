package com.chozoi.product.domain.values;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AuctionParticipantId implements Serializable {

    private static final long serialVersionUID = -9137465516547688848L;
    @Column(name = "auction_id")
    private Long auctionId;

    @Column(name = "user_id")
    private Integer userId;
}
