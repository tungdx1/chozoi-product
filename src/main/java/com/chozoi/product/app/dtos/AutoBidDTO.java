package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AutoBidDTO {

    private Long id;

    @JsonProperty("ceiling_price")
    private Long ceilingPrice;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
