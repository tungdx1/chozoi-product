package com.chozoi.product.domain.entities.elasticsearch;

import com.chozoi.product.data.request.ImageVariant;
import com.chozoi.product.domain.entities.postgres.EventContent;
import com.chozoi.product.domain.entities.postgres.types.InstantBidType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.util.CollectionUtils;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Data
@Document(indexName = "chozoi_products", type = "_doc")
public class ProductEs extends EventContent {
  @Id private Long id;
  private ShopEs shop;
  private Boolean isLiked;

  @Field(type = FieldType.Text)
  private String name;

  @Field(type = FieldType.Keyword)
  private String privateCode;

  private Long price;
  private Long salePrice;
  private AuctionEs auction;
  private String condition;
  private List<AttributeEs> attributes;
  private FacetEs facet;
  private List<ImageEs> images;
  private List<VariantEs> variants;
  private CategoryEs category;
  private List<CategoryEs> categories;
  private PromotionEs promotion;
  private Boolean freeShipStatus;

  @Field(type = FieldType.Keyword)
  private String state;

  private ProductStatsEs stats;

  @Field(type = FieldType.Keyword)
  private String type;

  private Integer commentsCount;

  @Field(type = FieldType.Keyword)
  private String currency;

  @Field(type = FieldType.Text)
  private String description;

  @Field(type = FieldType.Text)
  private String descriptionPicking;

  @Field(type = FieldType.Text)
  private String descriptionPickingin;

  @Field(type = FieldType.Text)
  private String descriptionPickingout;

  @Field(type = FieldType.Text)
  private String privateDescription;

  private Boolean isQuantityLimited;
  private Integer remainingQuantity;
  private List<Integer> packingSize;
  private Long quantity;
  private Long questionsCount;
  private ProductReportIssue reportIssues;
  private List<Integer> shippingPartnerIds;
  private List<ClassifiersEs> classifiers;

  @Field(type = FieldType.Keyword)
  private String sku;

  private Long soldQuantity;
  private Long weight;
  private Long createdAt;
  private Long updatedAt;

  @JsonProperty(value = "sort", access = JsonProperty.Access.READ_ONLY)
  private int sort;

  @JsonProperty(value = "image_variants", access = JsonProperty.Access.READ_ONLY)
  private List<ImageVariant> imageVariants() {
    List<ImageVariant> imageVariants = new ArrayList<>();
    if (!CollectionUtils.isEmpty(images)) {
        images.sort(Comparator.comparingInt(ImageEs::getSort));
        images.forEach(
            image -> {
              ImageVariant imageVariant = new ImageVariant();
              List<String> arrOfStr = Arrays.asList(image.getImageUrl().split("product", 2));
              if (arrOfStr.size() > 1) {
                String image_180 = arrOfStr.get(0) + "product/180x180" + arrOfStr.get(1);
                String image_65 = arrOfStr.get(0) + "product/65x65" + arrOfStr.get(1);
                imageVariant.setImage_65(image_65);
                imageVariant.setImage_180(image_180);
                imageVariant.setId(image.getId());
                imageVariants.add(imageVariant);
              }
            });
    }

    return imageVariants;
  }

  @Override
  public ProductEs clone() {
    ProductEs result = null;
    try {
      result = (ProductEs) super.clone();
    } catch (CloneNotSupportedException e) {
      result = new ProductEs();
    }
    return result;
  }

  public void sync(ProductEs productEs, AuctionEs auctionEs, int remainingQuantity) {
    this.setId(productEs.getId());
    this.setShop(productEs.getShop());
    this.setIsLiked(productEs.getIsLiked());
    this.setName(productEs.getName());
    this.price = productEs.price;
    this.setSalePrice(productEs.getSalePrice());
    this.auction = auctionEs;
    this.setCondition(productEs.getCondition());
    this.setAttributes(productEs.getAttributes());
    this.setFacet(productEs.getFacet());
    this.setImages(productEs.getImages());
    if (remainingQuantity == 0) {
      if (productEs.getVariants().size() > 0) {
        List<VariantEs> variantEsList = new ArrayList<>();
        for (VariantEs variantEs: productEs.getVariants()) {
          VariantEs variantEs1 = new VariantEs();
          variantEs1.sync(variantEs, remainingQuantity);
          variantEsList.add(variantEs1);
        }
        this.setVariants(variantEsList);
      }
    } else {
      this.setVariants(productEs.getVariants());
    }
    this.setCategory(productEs.getCategory());
    this.setCategories(productEs.getCategories());
    this.setPromotion(productEs.getPromotion());
    this.setFreeShipStatus(productEs.getFreeShipStatus());
    this.setState(productEs.getState());
    this.setStats(productEs.getStats());
    this.setType(productEs.getType());
    this.setCommentsCount(productEs.getCommentsCount());
    this.setCurrency(productEs.getCurrency());
    this.setDescription(productEs.getDescription());
    this.setDescriptionPicking(productEs.getDescriptionPicking());
    this.setDescriptionPickingin(productEs.getDescriptionPickingin());
    this.setDescriptionPickingout(productEs.getDescriptionPickingout());
    this.setIsQuantityLimited(productEs.getIsQuantityLimited());
    this.setRemainingQuantity(remainingQuantity);
    this.setPackingSize(productEs.getPackingSize());
    this.setQuantity(productEs.getQuantity());
    this.setQuestionsCount(productEs.getQuestionsCount());
    this.setReportIssues(productEs.getReportIssues());
    this.setShippingPartnerIds(productEs.getShippingPartnerIds());
    this.setClassifiers(productEs.getClassifiers());
    this.setSku(productEs.getSku());
    this.setSoldQuantity(productEs.getSoldQuantity());
    this.setWeight(productEs.getWeight());
    this.setCreatedAt(productEs.getCreatedAt());
    this.setUpdatedAt(productEs.getUpdatedAt());
    this.setSort(productEs.getSort());
  }
}
