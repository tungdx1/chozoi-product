package com.chozoi.product.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductStatsResponse {
  private Long id;

  @JsonProperty(value = "average_rating", access = JsonProperty.Access.READ_ONLY)
  private Double averageRating;

  @JsonProperty(value = "sum_rating", access = JsonProperty.Access.READ_ONLY)
  private Integer sumRating;

  @JsonProperty(value = "count_review", access = JsonProperty.Access.READ_ONLY)
  private Integer countReview;;
}
