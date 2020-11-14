package com.chozoi.product.app.dtos;

import com.chozoi.product.domain.entities.postgres.InstantBid;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class InstantBidDTO {

    private Long id;

    private Long price;

    private InstantBid.Type type;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
