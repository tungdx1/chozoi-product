package com.chozoi.product.domain.entities.postgres.product_ranking.data;

import lombok.Data;

import java.util.List;

@Data
public class Stores {
  private List<OfficitalStore> priority;
  private List<OfficitalStore> nonPriority;

  @Data
  public static class OfficitalStore {
    private Integer id;
    private String logo;
    private String type; // NON_PRIORITY, PRIORITY
    private String banner;
    private String showLogo;
    private String showBaner; // SYSTEM, SHOP
    private String logoMobile; // SYSTEM, SHOP
    private String bannerMobile; // SYSTEM, SHOP
  }
}
