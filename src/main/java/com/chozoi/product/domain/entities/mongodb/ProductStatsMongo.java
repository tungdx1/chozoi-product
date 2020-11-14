package com.chozoi.product.domain.entities.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "products.product.stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatsMongo {

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
