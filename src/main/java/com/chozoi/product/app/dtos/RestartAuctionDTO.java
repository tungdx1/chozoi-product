package com.chozoi.product.app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Log4j2
@Valid
public class RestartAuctionDTO {
  @NotNull()
  @JsonProperty("start_price")
  protected Long startPrice;

  @NotNull()
  @JsonProperty("price_step")
  protected Long priceStep;

  @NotNull()
  @JsonProperty("duration_time")
  protected Integer durationTime;

  @JsonProperty("buy_now_Price")
  protected Long buyNowPrice;

  @JsonProperty("expected_price")
  protected Long expectedPrice;

  @JsonProperty("expected_max_price")
  protected Long expectedMaxPrice;

  @NotNull()
  @JsonProperty("is_public")
  protected Boolean isPublic;

  @JsonProperty("private_code")
  protected String privateDode;

  @JsonProperty("private_description")
  protected String privateDescription;
}
