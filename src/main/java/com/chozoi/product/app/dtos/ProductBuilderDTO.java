package com.chozoi.product.app.dtos;

import com.chozoi.product.domain.entities.postgres.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductBuilderDTO extends ProductDTO {
  public static ProductCreateDTO builder(Product product1, RestartAuctionDTO dto) {
    ProductCreateDTO productDTO = new ProductCreateDTO();
    productDTO.setAttributes(product1.getAttributes());
    productDTO.setAuction(builderAuction(dto));
    productDTO.setAutoPublic(true);
    productDTO.setCategory(buildCategoryDto(product1));
    productDTO.setClassifiers(product1.getClassifiers());
    productDTO.setCondition(product1.getCondition());
    productDTO.setDescription(product1.getDescription());
    productDTO.setDescriptionPicking(product1.getDescriptionPinking());
    productDTO.setDescriptionPickingIn(product1.getDescriptionPinkingIn());
    productDTO.setDescriptionPickingOut(product1.getDescriptionPinkingOut());
    productDTO.setFreeShipStatus(product1.getFreeShipStatus());
    productDTO.setImages(builderImages(product1));
    productDTO.setIsQuantityLimited(product1.getIsQuantityLimited());
    productDTO.setName(product1.getName());
    productDTO.setPackingSize(product1.getPackingSize());
    productDTO.setShippingPartnerIds(product1.getShippingPartnerIds());
    productDTO.setShop(builderShop(product1));
    productDTO.setSku(product1.getSku());
    productDTO.setType(product1.getType());
    productDTO.setVariants(builderVariants(product1));
    productDTO.setIsPending(dto.getIsPublic());
    productDTO.setWeight(product1.getWeight());
      productDTO.setPrivateCode(dto.getPrivateDode());
      productDTO.setPrivateDescription(dto.getPrivateDescription());
    return productDTO;
  }

  private static List<VariantDTO> builderVariants(Product product1) {
    List<VariantDTO> variantDTOS = new ArrayList<>();
    product1
        .getVariants()
        .forEach(
            variant -> {
              VariantDTO variantDTO = new VariantDTO();
              variantDTO.setAttributes(variant.getAttributes());
              variantDTO.setPrice(variant.getPrice());
              variantDTO.setSalePrice(variant.getSalePrice());
              InventoryDTO inventoryDTO = new InventoryDTO();
              inventoryDTO.setInQuantity(variant.getInventory().getInQuantity());
              inventoryDTO.setInitialQuantity(0);
              inventoryDTO.setOutQuantity(0);
              variantDTO.setInventory(inventoryDTO);
              variantDTOS.add(variantDTO);
            });
    return variantDTOS;
  }

  private static ShopDTO builderShop(Product product1) {
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setId(product1.getShop().getId());
    return shopDTO;
  }

  private static List<ProductImageDTO> builderImages(Product product1) {
    List<ProductImageDTO> imageDto = new ArrayList<>();
    product1
        .getImages()
        .forEach(
            image -> {
              ProductImageDTO dto = new ProductImageDTO();
              dto.setImageUrl(image.getImageUrl());
              imageDto.add(dto);
            });
    return imageDto;
  }

  private static CategoryDTO buildCategoryDto(Product product1) {
    CategoryDTO categoryDTO = new CategoryDTO();
    categoryDTO.setId(product1.getCategory().getId());
    return categoryDTO;
  }

  private static AuctionDTO builderAuction(RestartAuctionDTO dto) {
    AuctionDTO auctionDTO = new AuctionDTO();
    auctionDTO.setBuyNowPrice(dto.getBuyNowPrice());
    auctionDTO.setTimeDuration(dto.getDurationTime());
    auctionDTO.setPriceStep(dto.getPriceStep());
    auctionDTO.setStartPrice(dto.getStartPrice());
    auctionDTO.setExpectedPrice(dto.getExpectedPrice());
    auctionDTO.setExpectedMaxPrice(dto.getExpectedMaxPrice());
    return auctionDTO;
  }
}
