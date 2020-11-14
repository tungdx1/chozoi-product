package com.chozoi.product.domain.entities.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@RedisHash(value = "ShopStats", timeToLive = 60)
public class ShopStatsRedis {
    private Integer id;
    private Integer countFollowers;
    private Integer countProduct;
    private Integer countZeroStars;
    private Integer countOneStars;
    private Integer countTwoStars;
    private Integer countThreeStars;
    private Integer countFourStars;
    private Integer countFiveStars;
    private Long sumRating;
    private Double averageRating;
    private Integer countQuestion;
    private Integer countAnswer;
    private Integer countOrder;
    private Integer countOrderCancel;
    private Double sumResponseTimeByHour;
    private Double responseRate;
    private Double cancelOrderRate;
    private Long createdAt;
    private Long updatedAt;
}
