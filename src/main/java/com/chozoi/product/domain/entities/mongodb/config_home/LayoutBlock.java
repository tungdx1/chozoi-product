package com.chozoi.product.domain.entities.mongodb.config_home;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public abstract class LayoutBlock {
  @Id public Integer id;
  String title;
  String site;
  String state;
  String banner;
  String bannerLink;
  List<LayoutBlock.Keyword> keyWords;
  List<LayoutBlock.Spotlight> spotlights;
  List<LayoutBlock.Task> tasks;
  List<Long> shops;
  String type;
  String background;
  Integer sort;
  LayoutBlock.Stores stores;
  String bannerMobile;
  String bannerScreen;
  String titleLink;
  String titleScreen;
  Integer productSize;
  String titleLinkId;
  String productSort;
  String bannerLinkId;
  Long createdAt;
  Long updatedAt;
  List<LayoutBlockGroupMongo> productGroups;

  @Data
  public static class Keyword {
    protected String link;
    protected String image;
    protected String title;
    protected String linkId;
    protected String screen;
    protected String numberSearch;
  }

  @Data
  public static class Spotlight {
    protected String link;
    protected String image;
    protected String title;
    protected String linkId;
    protected String screen;
  }

  @Data
  public static class Task {
    protected Integer id;
    protected String link;
    protected String title;
    protected String linkId;
    protected String screen;
  }

  @Data
  public static class Stores {
    public List<LayoutBlock.Stores.OfficialStore> priority;
    public List<LayoutBlock.Stores.OfficialStore> nonPriority;
    public List<Banner> bannerList;

    @Data
    public static class OfficialStore {
      protected Integer id;
      protected String logo;
      protected String type; // NON_PRIORITY, PRIORITY
      protected String banner;
      protected String title;
      protected String showLogo;
      protected String showBaner; // SYSTEM, SHOP
      protected String logoMobile; // SYSTEM, SHOP
      protected String bannerMobile; // SYSTEM, SHOP
    }

    @Data
    public static class Banner {
      protected Integer id;
      protected String banner;
      protected String bannerMobile;
      protected String link;
      protected String linkId;
      protected String screen;
    }
  }
}
