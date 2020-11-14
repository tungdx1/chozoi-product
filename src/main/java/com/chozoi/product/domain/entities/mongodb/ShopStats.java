package com.chozoi.product.domain.entities.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "shops.stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopStats {
    private Integer id;
    private Integer countFollow;
    private Integer countFavorite;
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
