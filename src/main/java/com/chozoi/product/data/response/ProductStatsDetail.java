package com.chozoi.product.data.response;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
public class ProductStatsDetail {
    private Long id;

    private Integer countViews;

    private Integer countQuestion;

    private Integer countAnswer;

    private Integer countReview;

    private Integer countOneStars;

    private Integer countTwoStars;

    private Integer countThreeStars;

    private Integer countFourStars;

    private Integer countFiveStars;

    private Integer sumRating;
    private Long lastReviewTime;

    private Double averageRating;


}
