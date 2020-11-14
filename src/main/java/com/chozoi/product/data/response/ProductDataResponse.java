package com.chozoi.product.data.response;

import com.chozoi.product.data.request.ImageVariant;
import com.chozoi.product.data.request.ProductClassifier;
import com.chozoi.product.domain.entities.mongodb.AuctionMongo;
import com.chozoi.product.domain.entities.mongodb.AuctionResultMongo;
import com.chozoi.product.domain.entities.mongodb.Promotion;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.utils.Calculate;
import com.chozoi.product.domain.utils.ProductUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.data.annotation.Id;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
public class ProductDataResponse {
  @Id private Long id;
  private String name;
  private List<Integer> shippingPartnerIds;
  private List<Integer> packingSize;
  private Long price;
  private Long salePrice;
  private String condition;
  private String sku;
  private String currency;
  private Integer weight;
  private String description;
  private Boolean freeShipStatus;

  @JsonProperty(value = "description_picking")
  private String descriptionPinking;

  @JsonProperty(value = "description_pickingin")
  private String descriptionPinkingin;

  @JsonProperty(value = "description_pickingout")
  private String descriptionPinkingOut;

  @JsonProperty(value = "private_description")
  private String privateDescription;

  private List<String> videos;
  private String type;
  private Boolean isQuantityLimited;
  private String state;
  private List<ProductClassifier> classifiers;
  private List<Object> attributes;
  private CategoryData category;
  private List<ProductImageResponse> images;
  private ShopResponse shop;
  private AuctionMongo auction;
  private Promotion promotion;
  private List<ProductVariantDetail> variants;
  private ProductStatsDetail stats;
  /* Qty fake */
  @JsonIgnore private Integer soldQuantityFake;
  private Boolean isLiked;
  private Boolean isPrivate;
  private Boolean privateActive;
  private Long createdAt;
  private Long updatedAt;

  @JsonProperty(value = "sold_quantity", access = JsonProperty.Access.READ_ONLY)
  private Integer soldQuantity() {
    return Calculate.soldQuantity(variants)
        + (Objects.isNull(soldQuantityFake) ? 0 : soldQuantityFake);
  }

  @JsonProperty(value = "remaining_quantity", access = JsonProperty.Access.READ_ONLY)
  private Integer remainingQuantity() {
    return Calculate.remainingQuantity(variants);
  }

  @JsonProperty(value = "image_variants", access = JsonProperty.Access.READ_ONLY)
  private List<ImageVariant> imageVariants() {
    List<ImageVariant> imageVariants = new ArrayList<>();
    if (!CollectionUtils.isEmpty(images)) images.forEach(
            image -> {
              ImageVariant imageVariant = new ImageVariant();
              List<String> arrOfStr = Arrays.asList(image.getImageUrl().split("product", 2));
              if (arrOfStr.size() > 1) {
                String image_65 = arrOfStr.get(0) + "product/65x65" + arrOfStr.get(1);
                String image_350 = arrOfStr.get(0) + "product/350x350" + arrOfStr.get(1);
                String image_600 = arrOfStr.get(0) + "product/600x600" + arrOfStr.get(1);
                imageVariant.setImage_350(image_350);
                imageVariant.setImage_65(image_65);
                imageVariant.setImage_600(image_600);
                imageVariant.setId(image.getId());
                imageVariants.add(imageVariant);
              }
            });
    return imageVariants;
  }

  public void inferProperties() {
    if (Objects.nonNull(stats)) {
      long a = Math.round(stats.getAverageRating() * 10);
      stats.setAverageRating((double) a / 10);
    }
    if (shop.getFreeShipStatus() != null) if (shop.getFreeShipStatus().equals("OFF")) freeShipStatus = false;
    if (!isQuantityLimited) variants.forEach(
            variant -> {
              variant.getInventory().setRemainingQuantity(999);
            });
    else variants.forEach(
            variant -> {
              int inQuantity = variant.getInventory().getInQuantity();
              int outQuantity = variant.getInventory().getOutQuantity();
              variant.getInventory().setRemainingQuantity(inQuantity - outQuantity);
            });
    ProductType type1 = ProductType.valueOf(type);
    if (ProductUtils.AUCTION_TYPE.contains(type1)) {
      assert auction != null;
      ModelMapper modelMapper = new ModelMapper();
      AuctionResultMongo result;
      try {
        result = modelMapper.map(auction.getResult(), AuctionResultMongo.class);
      } catch (Exception e) {
        result = AuctionResultMongo.create(id);
      }
      if (auction != null && result != null && auction.getStartPrice() != null && result.getCurrentPrice() != null  && result.getCurrentPrice() < auction.getStartPrice()) {
        result.setCurrentPrice(auction.getStartPrice());
        result.setBidPrice(0L);
        auction.setResult(result);
      }
    }
  }
}
