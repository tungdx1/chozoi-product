package com.chozoi.product.app.dtos;

import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
public class AuctionDTO {

  // TODO: Provide validation message for each field
  @JsonProperty("id")
  private Long id;

  @JsonProperty(value = "state", access = JsonProperty.Access.READ_ONLY)
  private ProductAuctionState state;

  @JsonProperty("time_start")
  @Future
  private LocalDateTime timeStart;

  @JsonProperty("time_end")
  @Future
  private LocalDateTime timeEnd;

  @JsonProperty("price_step")
  @NotNull
  @Positive(message = "price_step must be positive")
  private Long priceStep;

  @JsonProperty("start_price")
  @NotNull
  @Positive(message = "start_price must be positive")
  private Long startPrice;

  @JsonProperty("buy_now_price")
  private Long buyNowPrice;

  @JsonProperty("original_price")
  private long originalPrice;

  @JsonProperty("expected_price")
  private Long expectedPrice;

  @JsonProperty("expected_max_price")
  private Long expectedMaxPrice;

  @JsonProperty("time_duration")
  @NotNull
  @Min(1)
  private Integer timeDuration;

  private AuctionResultDTO result;

  private BiddingDTO bidding;

  public AuctionResultDTO getResult() {

    if (Objects.isNull(result)) {
      result = new AuctionResultDTO();
      result.setId(id);
      result.setBiddersCount(0);
      result.setBidsCount(0);
      result.setWinnerId(result.getWinnerId());
      result.setCeilingPrice(startPrice);
      result.setCurrentPrice(startPrice);
    }

    return result;
  }
}
