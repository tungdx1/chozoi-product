package com.chozoi.product.app.dtos;

import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangeStateAuctionDto {

  @NotNull private ProductAuctionState state;
}
