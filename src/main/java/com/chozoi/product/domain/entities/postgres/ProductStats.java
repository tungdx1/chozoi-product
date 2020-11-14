package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.utils.GenericArrayUserType;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "product_stats", schema = "products")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "postgres_array", typeClass = GenericArrayUserType.class)
public class ProductStats {

  @Id private Long id;

  @OneToOne
  @JoinColumn(name = "id")
  @JsonIgnore
  @MapsId
  private Product product;

  @Column(name = "count_views")
  private Integer countViews = 0;

  @Column(name = "count_review")
  private Integer countReview = 0;

  @Column(name = "count_question")
  private Integer countQuestion = 0;

  @Column(name = "count_one_stars")
  private Integer countOneStars = 0;

  @Column(name = "count_two_stars")
  private Integer countTwoStars = 0;

  @Column(name = "count_three_stars")
  private Integer countThreeStars = 0;

  @Column(name = "count_four_stars")
  private Integer countFourStars = 0;

  @Column(name = "count_five_stars")
  private Integer countFiveStars = 0;

  @Column(name = "sum_rating")
  private Integer sumRating = 0;

  @Column(name = "average_rating")
  private Double averageRating = 0.0;

  @Column(name = "last_review_time")
  private Long lastReviewTime = 0L;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  public static ProductStats create() {
    ProductStats stats = new ProductStats();
    stats.setCountViews(0);
    stats.setCountQuestion(0);
    stats.setCountReview(0);
    stats.setCountOneStars(0);
    stats.setCountTwoStars(0);
    stats.setCountThreeStars(0);
    stats.setCountFourStars(0);
    stats.setCountFiveStars(0);
    stats.setSumRating(0);
    stats.setAverageRating(0D);
    stats.setAverageRating(0D);
    stats.setLastReviewTime(0L);
    return stats;
  }

  public void assign() {
    countViews = 0;
    countQuestion = 0;
    countReview = 0;
    countOneStars = 0;
    countTwoStars = 0;
    countThreeStars = 0;
    countFourStars = 0;
    countFiveStars = 0;
    sumRating = 0;
    averageRating = (double) 0;
    lastReviewTime = (long) 0;
  }
}
