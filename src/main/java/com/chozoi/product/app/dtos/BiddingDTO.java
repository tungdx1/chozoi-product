package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
public class BiddingDTO {

    @JsonInclude(Include.NON_NULL)
    private InstantBidDTO instantBid;

    @JsonInclude(Include.NON_NULL)
    private AutoBidDTO autoBid;
}
