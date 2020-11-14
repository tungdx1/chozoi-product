package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuctionResultDTO {

    private Long id;

    private Integer biddersCount;

    private Integer bidsCount;

    private Integer winnerId;

    private Long currentPrice;

    private Long ceilingPrice;
}
