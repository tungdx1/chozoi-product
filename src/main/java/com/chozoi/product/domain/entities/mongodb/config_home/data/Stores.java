package com.chozoi.product.domain.entities.mongodb.config_home.data;

import lombok.Data;

import java.util.List;

@Data
public class Stores {
  public List<OfficialStore> priority;
  public List<OfficialStore> nonPriority;

  @Data
  public static class OfficialStore {
    protected Integer id;
    protected String logo;
    protected String type; // NON_PRIORITY, PRIORITY
    protected String banner;
    protected String showLogo;
    protected String showBaner; // SYSTEM, SHOP
    protected String logoMobile; // SYSTEM, SHOP
    protected String bannerMobile; // SYSTEM, SHOP
  }
}
