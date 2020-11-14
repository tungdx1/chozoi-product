package com.chozoi.product.domain.entities.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "ProductStats", timeToLive = 60)
@Builder
@Data
public class ProductStatsRedis {
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

    private Double averageRating;

    private Long lastReviewTime;

    private Long createdAt;

    private Long updatedAt;
}
