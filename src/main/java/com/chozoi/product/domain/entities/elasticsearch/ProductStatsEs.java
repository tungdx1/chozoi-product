package com.chozoi.product.domain.entities.elasticsearch;

import lombok.Data;

@Data
public class ProductStatsEs {
  private Float averageRating;
  private Long countFiveStars;
  private Long countFourStars;
  private Long countOneStars;
  private Long countQuestion;
  private Long countReview;
  private Long countThreeStars;
  private Long countTwoStars;
  private Long countViews;
  private Long id;
  private Long sumRating;
  //  private Long updatedAt;
  //  private Long createdAt;

}
