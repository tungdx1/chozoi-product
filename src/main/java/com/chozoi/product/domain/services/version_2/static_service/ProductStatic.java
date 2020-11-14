package com.chozoi.product.domain.services.version_2.static_service;

import com.chozoi.product.app.dtos.UpdatePartialProductDTO;
import com.chozoi.product.domain.entities.mongodb.*;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductVariant;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.entities.postgres.types.UserRoleState;
import com.chozoi.product.domain.entities.postgres.types.VariantState;
import com.chozoi.product.domain.entities.redis.AuctionResultRedis;
import com.chozoi.product.domain.exceptions.ExceptionMessage;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
import javassist.NotFoundException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductStatic {
  private static List<ProductState> statesAcceptUpdate =
      Arrays.asList(
          ProductState.DRAFT,
          ProductState.REJECT,
          ProductState.PUBLIC,
          ProductState.READY,
          ProductState.PENDING);
  /**
   * handle and set state for product
   *
   * @param product
   * @param state
   * @param isPending
   */
  public static void setState(Product product, UserRoleState state, @NotNull Boolean isPending) {
    ProductState productState = product.getState();
    if (state == null) {
//      productState = ProductState.DRAFT;
      if (product.getState() != ProductState.PUBLIC && product.getState() != ProductState.READY) {
        productState = isPending ? ProductState.PENDING : ProductState.DRAFT;
      }
//        throw new ResourceNotFoundException(ExceptionMessage.NOT_SELLER);
    } else {
      if (state == UserRoleState.REJECT) {
        throw new ResourceNotFoundException(ExceptionMessage.USER_IS_REJECT);
      } else if (state == UserRoleState.APPROVED || state == UserRoleState.PENDING) {
        if (product.getState() != ProductState.PUBLIC && product.getState() != ProductState.READY) {
          productState = isPending ? ProductState.PENDING : ProductState.DRAFT;
        }
      } else if (product.getState() != ProductState.PUBLIC && product.getState() != ProductState.READY) {
        productState = ProductState.DRAFT;
      }
    }

    product.setState(productState);
  }

  /**
   * mapping product
   *
   * @param productOld
   * @param productNew
   */
  public static void mappingProduct(Product productOld, Product productNew) {
    productNew.setId(productOld.getId());
    productNew.setVariants(null);
    productNew.setShop(productOld.getShop());
    productNew.setAttributes(productOld.getAttributes());
    productNew.setCategory(productOld.getCategory());
    productNew.setCategories(productOld.getCategories());
    productNew.setClassifiers(productOld.getClassifiers());
    productNew.setAutoPublic(productOld.getAutoPublic());
    productNew.setAuction(productOld.getAuction());
    productNew.setWeight(productNew.getWeight());
    productNew.setCondition(productOld.getCondition());
    productNew.setDescription(productOld.getDescription());
    productNew.setDescriptionPinking(productOld.getDescriptionPinking());
    productNew.setDescriptionPinkingIn(productOld.getDescriptionPinkingIn());
    productNew.setDescriptionPinkingOut(productOld.getDescriptionPinkingOut());
    productNew.setName(productOld.getName());
    productNew.setShippingPartnerIds(productOld.getShippingPartnerIds());
    productNew.setType(productOld.getType());
    productNew.setVideos(productOld.getVideos());
    productNew.setCreatedAt(productOld.getCreatedAt());
    productNew.setStats(productOld.getStats());
    productNew.setState(productOld.getState());
  }

  /**
   * check the right to product updates
   *
   * @param product
   * @throws NotFoundException
   */
  public static void checkStateUpdate(Product product) throws NotFoundException {
    if (!statesAcceptUpdate.contains(product.getState())) {
        throw new NotFoundException("state " + product.getState() + " not update");
    }
  }

  public static void setDataCannotApproved(UpdatePartialProductDTO dto, Product product) {
    if (Objects.nonNull(dto.getIsQuantityLimited())) {
        product.setIsQuantityLimited(dto.getIsQuantityLimited());
    }
    product.setPackingSize(dto.getPackingSize());
    product.setWeight(dto.getWeight());
    product.setFreeShipStatus(dto.getFreeShipStatus());
    if (Objects.nonNull(dto.getClassifiers())) {
        product.setClassifiers(dto.getClassifiers());
    }
  }

  public static void validation(Product product) throws Exception {
    List<ProductVariant> variants =
        product.getVariants().stream()
            .filter(v -> v.getState().equals(VariantState.PUBLIC))
            .collect(Collectors.toList());
    if (product.getType() == ProductType.CLASSIFIER) {
      if (variants.size() < 2) {
          throw new Exception("variant failed");
      }
    } else if (variants.size() > 1) {
        throw new Exception("variant failed");
    }
  }

  public static ProductMongo buildProductMongo(
      ProductMongo productMongo,
      List<InventoryMongo> inventories,
      ProductStatsMongo productStats,
      List<ProductImage> images,
      AuctionResultRedis auctionResult)
      throws Exception {
    if (productMongo.getType().equals("AUCTION") || productMongo.getType().equals("AUCTION_SALE")) {
      AuctionMongo auction = productMongo.getAuction();
      if (auction != null) {
        auction.setResult(auctionResult);
        productMongo.setAuction(auction);
      }
    }
    List<com.chozoi.product.domain.entities.mongodb.ProductVariant> variants =
        productMongo.getVariants();
    if (!Objects.isNull(variants)) {
        variants.forEach(
            variant -> {
              List<InventoryMongo> inventoryMongoList =
                  inventories.stream()
                      .filter(inven -> inven.getId().equals(variant.getId()))
                      .collect(Collectors.toList());
              InventoryMongo inventoryMongo = new InventoryMongo();
              try {
                inventoryMongo = inventoryMongoList.get(0);
              } catch (Exception ignored) {
                inventoryMongo.setInQuantity(0);
                inventoryMongo.setInitialQuantity(0);
                inventoryMongo.setOutQuantity(0);
                inventoryMongo.setProductId(productMongo.getId());
              }
              variant.setInventory(inventoryMongo);
            });
    }
    // sort images
    images = ObjectUtils.defaultIfNull(images, new ArrayList<>());
    images.sort(Comparator.comparing(ProductImage::getSort));
    productMongo.setImages(images);
    //
    productMongo.setVariants(variants);
    productMongo.setStats(productStats);
    return productMongo;
  }
}
