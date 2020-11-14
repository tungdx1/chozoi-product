package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class AuctionCreateDTO {

    @JsonProperty("price_step")
    private Long priceStep;

    @JsonProperty("start_price")
    private Long startPrice;

    @JsonProperty("buy_now_price")
    private Long buyNowPrice;

    @JsonProperty("original_price")
    private long originalPrice;

    @JsonProperty("time_duration")
    private Integer timeDuration;
}
