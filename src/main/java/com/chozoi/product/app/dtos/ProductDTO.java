package com.chozoi.product.app.dtos;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.data.request.AttributeProduct;
import com.chozoi.product.data.request.ImageVariant;
import com.chozoi.product.data.request.ProductClassifier;
import com.chozoi.product.domain.entities.postgres.Category;
import com.chozoi.product.domain.entities.postgres.ProductReportIssue;
import com.chozoi.product.domain.entities.postgres.ProductStats;
import com.chozoi.product.domain.entities.postgres.types.ProductCondition;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.utils.ProductUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Log4j2
@Valid
public class ProductDTO {

  // TODO: provide validation message for each field

  protected Long id;

  @NotNull()
  @Size(min = 3, max = 150)
  @JsonProperty("name")
  protected String name;

  @Size(min = 3, max = 10)
  @JsonProperty("private_code")
  protected String privateCode;

  @Positive(message = "weight must be positive")
  @NotNull
  @JsonProperty("weight")
  protected Integer weight;

  protected String sku;

  @JsonProperty("free_ship_status")
  protected Boolean freeShipStatus;

  @NotNull()
  @NotEmpty()
  @JsonProperty("shipping_partner_ids")
  protected Integer[] shippingPartnerIds;

  @NotNull()
  @NotEmpty()
  @JsonProperty("packing_size")
  protected Integer[] packingSize;

  protected List<AttributeProduct> attributes;

  @NotNull()
  @NotEmpty()
  @Size(max = 10)
  protected List<@Valid ProductImageDTO> images;

  @NotNull() protected List<String> videos;

  @JsonProperty("is_quantity_limited")
  @NotNull()
  protected Boolean isQuantityLimited;

  protected ShopDTO shop;

  @JsonInclude(Include.NON_NULL)
  @Valid
  protected CategoryDTO category;

  @NotNull() protected ProductType type;

  @NotNull() protected ProductCondition condition;

  @NotNull()
  @Size(min = 5)
  protected String description;

  @JsonProperty("description_picking")
  protected String descriptionPicking;

  @JsonProperty("description_pickingin")
  protected String descriptionPickingIn;

  @JsonProperty("description_pickingout")
  protected String descriptionPickingOut;

  @JsonProperty("private_description")
  protected String privateDescription;

  // TODO: limit length of arrays
  protected List<ProductClassifier> classifiers;

  @JsonInclude(Include.NON_EMPTY)
  protected List<@Valid VariantDTO> variants;

  protected AuctionDTO auction;

  @JsonInclude(Include.NON_NULL)
  @Valid
  protected List<PromotionDTO> promotions;

  @JsonProperty("auto_public")
  @NotNull()
  protected Boolean autoPublic;

  @JsonProperty("is_pending")
  @NotNull
  protected Boolean isPending;

  protected ProductStats stats;

  protected List<Category> categories;

  @JsonProperty(value = "state", access = Access.READ_ONLY)
  protected ProductState state;

  @JsonProperty(value = "reportIssues", access = JsonProperty.Access.READ_ONLY)
  protected ProductReportIssue reportIssues;

  @Autowired private ModelMapper modelMapper;

  @JsonProperty(value = "out_quantity", access = JsonProperty.Access.READ_ONLY)
  protected Integer outQuantity() {
    int count = 0;
    if (!CollectionUtils.isEmpty(variants))
      count = variants.stream().mapToInt(variant -> variant.getInventory().getOutQuantity()).sum();
    return count;
  }

  @JsonProperty(value = "remaining_quantity", access = JsonProperty.Access.READ_ONLY)
  protected Integer remainingQuantity() {
    int count = 0;
    if (!CollectionUtils.isEmpty(variants)) count =
            variants.stream().mapToInt(variant -> variant.getInventory().getInQuantity()).sum()
                    + variants.stream()
                    .mapToInt(variant -> variant.getInventory().getInitialQuantity())
                    .sum();
    return count;
  }

  @JsonProperty(value = "image_variants", access = JsonProperty.Access.READ_ONLY)
  protected List<ImageVariant> imageVariants() {
    List<ImageVariant> imageVariants = new ArrayList<>();
    if (!CollectionUtils.isEmpty(images)) images.forEach(
            image -> {
              ImageVariant imageVariant = new ImageVariant();
              List<String> arrOfStr = Arrays.asList(image.getImageUrl().split("product", 2));
              if (arrOfStr.size() > 1) {
                String image_600 = image.getImageUrl();
                String image_350 = arrOfStr.get(0) + "product/350x350" + arrOfStr.get(1);
                String image_180 = arrOfStr.get(0) + "product/180x180" + arrOfStr.get(1);
                String image_65 = arrOfStr.get(0) + "product/65x65" + arrOfStr.get(1);
                imageVariant.setImage_65(image_65);
                imageVariant.setImage_180(image_180);
                imageVariant.setImage_350(image_350);
                imageVariant.setImage_600(image_600);
                imageVariant.setId(image.getId());
                imageVariants.add(imageVariant);
              }
            });
    return imageVariants;
  }

  @AssertTrue(message = "buy_now_price must be greater than start_price")
  @JsonIgnore
  protected boolean isValid() {
    if (type == ProductType.AUCTION_SALE) {
      Long buyNowPrice = auction.getBuyNowPrice();
      return buyNowPrice != null && buyNowPrice >= auction.getStartPrice();
    }

    return true;
  }

  @AssertTrue(message = "Auction data fail")
  @JsonIgnore
  protected boolean isValidAuction() throws Exception {
    if (ProductUtils.AUCTION_TYPE.contains(type)) {
      if (Objects.isNull(auction)) throw new Exception("field 'auction' not null");
      if (Objects.isNull(auction.getStartPrice()) || auction.getStartPrice() < 0)
        throw new Exception("field 'start_price' not null and > 0");
      if (Objects.isNull(auction.getPriceStep()) || auction.getPriceStep() < 0)
        throw new Exception("field 'price_step' not null and > 0");
      if (Objects.isNull(auction.getTimeDuration()) || auction.getTimeDuration() < 1)
        throw new Exception("field 'time_duration' not null and > 0");
      if (type.equals(ProductType.AUCTION_FLASH_BID))
        if (!(variants.get(0).getInventory().getInQuantity() > 0)) throw new Exception("Số lượng phải lớn hơn 0");
    }
    return true;
  }

  // validate classifier
  @AssertTrue(message = "false count classifier")
  @JsonIgnore
  protected boolean checkClassifier() {
    if (type == ProductType.CLASSIFIER) {
      if (classifiers == null || classifiers.size() > 2 || classifiers.size() < 1) return false;
      return true;
    }
    return true;
  }

  // check count variants
  @AssertTrue(message = "variants size false")
  @JsonIgnore
  protected boolean isValidVariants() {
    if (type == ProductType.CLASSIFIER) return variants.size() >= 2;
    else return variants.size() == 1;
  }

  // check count variants attribute
  @AssertTrue(message = "variants.attributes not empty")
  @JsonIgnore
  protected boolean isValidVariantsAttribute() {
    if (type == ProductType.CLASSIFIER)
      for (VariantDTO variant : variants) return variant.getAttributes().size() == classifiers.size();
    return true;
  }

  // check classifier
  @AssertTrue(message = "classifiers not empty")
  @JsonIgnore
  protected boolean isValidClassifierNotNull() {
    if (type == ProductType.CLASSIFIER) return 0 < classifiers.size() && classifiers.size() < 3;
    return true;
  }

  // check count packing size
  @AssertTrue(message = "Packing size is 3")
  @JsonIgnore
  protected boolean isValidPackingSize() {
    List<Integer> list = new ArrayList<>(Arrays.asList(packingSize));
    if (list.size() != 3) return false;
    return true;
  }
  // check count variants
  @AssertTrue(message = "Size không hơp lệ")
  @JsonIgnore
  protected boolean isValidPackingSizeNotEquals() {
    for (Integer integer : packingSize) if (integer.equals(0)) return false;
    return true;
  }

  public ProductState getState() {
    if (state != null) return state;
    else {
      if (isPending == null) return ProductState.DRAFT;
      return !isPending ? ProductState.DRAFT : ProductState.PENDING;
    }
  }
}
