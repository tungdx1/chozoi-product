package com.chozoi.product.domain.entities.postgres.product_ranking.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class Task implements Serializable {
  private Integer id;
  private String title;
  private String link;
  private String linkId;
  private String screen;
}
