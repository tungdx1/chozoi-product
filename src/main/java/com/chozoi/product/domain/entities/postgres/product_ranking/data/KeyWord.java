package com.chozoi.product.domain.entities.postgres.product_ranking.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class KeyWord implements Serializable {
  private String title;
  private String image;
  private String link;
  private String linkId;
  private String screen;
  private String numberSearch;
}
