package com.chozoi.product.app.controllers.v1;

import com.chozoi.product.app.dtos.*;
import com.chozoi.product.app.dtos.elasticsearch.CategoriesDTO;
import com.chozoi.product.app.responses.*;
import com.chozoi.product.data.response.InventoryResponse;
import com.chozoi.product.data.response.ProductsPrivateResponse;
import com.chozoi.product.data.response.ProductsPublicResponse;
import com.chozoi.product.domain.entities.elasticsearch.CategoriesEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductDraftEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductImage;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.UserRole;
import com.chozoi.product.domain.entities.postgres.types.UserRoleState;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/v1")
@Log4j2
public class ShopProductController extends BaseProductController {
  @Value("${writable}")
  private Boolean writable;
  // ==========================PRIVATE===================================

  //  /**
  //   * Create product
  //   *
  //   * @param productDto
  //   * @param shopId
  //   * @return
  //   * @throws ResourceNotFoundException
  //   */
  //  @PostMapping(path = "/shops/{shop_id}/products")
  //  public Product create(
  //      @Valid @RequestBody ProductCreateDTO productDto,
  //      @RequestHeader(name = "X-Chozoi-Seller-State") String state,
  //      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
  //      @RequestHeader(name = "x-chozoi-user-id") Integer userId)
  //      throws Exception {
  //    this.checkWritable(writable);
  //    Product product = modelMapper.dtoToProduct(productDto);
  //    this.checkIsPeddingSeller(state);
  //    return productSellerService.store(
  //        product, UserRoleState.valueOf(state), userId, shopId, productDto.getIsPending());
  //  }

  /** create image product */
  @PostMapping(path = "/products/{product_id}/images")
  public ProductImage createImage(
      @Valid @RequestBody CreateImageDto dto,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      @PathVariable(name = "product_id") Long productId)
      throws Exception {
    this.checkWritable(writable);
    return productSellerService.createImage(dto, shopId);
  }

  /**
   * delete image product
   *
   * @return
   */
  @DeleteMapping(path = "/products/{product_id}/images")
  public ChangeStateResponse deleteImage(
      @Valid @RequestBody DeleteImageDto dto,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      @PathVariable(name = "product_id") Long productId)
      throws Exception {
    this.checkWritable(writable);
    return productSellerService.deleteImage(dto, shopId, productId);
  }

  /**
   * check state
   *
   * @param id
   * @return
   */
  @GetMapping(path = "/products/{id}/state")
  public ProductState checkState(@PathVariable("id") Long id) {
    return productSellerService.checkState(id);
  }

  //  /**
  //   * update product
  //   *
  //   * @return
  //   */
  //  @PutMapping(path = "/shops/{shop_id}/products/{product_id}")
  //  public ProductUpdateResponse update(
  //      @Valid @RequestBody ProductCreateDTO dto,
  //      @RequestHeader(name = "X-Chozoi-Seller-State") String state,
  //      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
  //      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
  //      @PathVariable(name = "shop_id") Integer reqShopId,
  //      @PathVariable(name = "product_id") Long productId)
  //      throws Exception {
  //    this.checkWritable(writable);
  //    Product product = modelMapper.dtoToProduct(dto);
  //    Product productUpdate =
  //        productSellerService.update(
  //            productId, shopId, product, dto.getIsPending(), UserRoleState.valueOf(state),
  // userId);
  //    return ProductUpdateResponse.builder()
  //        .id(productUpdate.getId())
  //        .status(true)
  //        .statusValidation(true)
  //        .message("success")
  //        .build();
  //  }

  /** update product */
  @PutMapping(path = "/shops/{shop_id}/products/{product_id}/partial")
  public boolean updatePartial(
      @Valid @RequestBody UpdatePartialProductDTO dto,
      @RequestHeader(name = "X-Chozoi-Seller-State") String state,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
      @PathVariable(name = "shop_id") Integer reqShopId,
      @PathVariable(name = "product_id") Long productId)
      throws Exception {
    this.checkWritable(writable);
    return productSellerService.updatePartial(dto, shopId, productId, userId);
  }

  /**
   * Get product by id role seller
   *
   * @return
   */
  @GetMapping(path = "/shops/{shop_id}/products/{product_id}")
  public ProductPrivateReponse productRoleSeller(
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
      @PathVariable(name = "shop_id") Integer reqShopId,
      @PathVariable(name = "product_id") Long productId)
      throws Exception {
//    this.checkShop(shopId, reqShopId);
    this.validShop(reqShopId, userId);
    Product product = productSellerService.productRoleSeller(productId, reqShopId);
    ModelMapper mapper = new ModelMapper();
    ProductPrivateReponse productPrivateReponse = mapper.map(product, ProductPrivateReponse.class);
    productPrivateReponse
        .getVariants()
        .forEach(
            variant -> {
              try {
                variant
                    .getInventory()
                    .setRemainingQuantity(
                        variant.getInventory().getInQuantity()
                            - variant.getInventory().getOutQuantity());
              } catch (Exception e) {
                InventoryResponse inventory = new InventoryResponse();
                inventory.setId(variant.getId());
                inventory.setRemainingQuantity(0);
                inventory.setInitialQuantity(0);
                inventory.setInQuantity(0);
                inventory.setOutQuantity(0);
                variant.setInventory(inventory);
              }
            });
    return productPrivateReponse;
  }

  /**
   * get product by shop
   *
   * @param pageable
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/shops/{shop_id}/products",
      headers = {"X-Chozoi-User-Role=*"})
  public GetProductsResponse getByShop(
      @RequestHeader(name = "X-Chozoi-Seller-State") String state,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      @PathVariable(name = "shop_id") Integer reqShopId,
      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
      Pageable pageable)
          throws Exception {
//    this.checkIsPeddingSeller(state);
//    this.checkShop(shopId, reqShopId);
    this.validShop(reqShopId, userId);
    Page<Product> page = productSellerService.getByShopSeller(reqShopId, pageable);
    return getProductsResponse(page);
  }

  /**
   * Get product auction for seller
   *
   * @param shopId
   * @param pageable
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      value = "/shops/{shop_id}/products",
      params = "collection=auction",
      headers = {"X-Chozoi-User-Role=*"})
  public GetProductsResponse getProductAuction(
      @RequestHeader(name = "X-Chozoi-Seller-State") String state,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      Pageable pageable,
      @PathVariable(name = "shop_id") Integer reqShopId,
      @RequestHeader(name = "x-chozoi-user-id") Integer userId)
          throws Exception {
//    this.checkIsPeddingSeller(state);
//    this.checkShop(shopId, reqShopId);
    this.validShop(reqShopId, userId);
    Page<Product> page = productSellerService.getShopAuction(shopId, pageable);
    return getProductsResponse(page);
  }

  /**
   * Delete multi product role seller
   *
   * @param ids
   * @param shopId
   * @param pageable
   * @return
   * @throws ResourceNotFoundException
   */
  @DeleteMapping(path = "/shops/{shop_id}/products/_bulk")
  public ChangeStateResponse deleteMultiProduct(
      @RequestBody ProductIdDTO ids,
      @RequestHeader(name = "x-chozoi-user-role") String role,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
      @PageableDefault(page = 0, size = 2000) Pageable pageable,
      @PathVariable(name = "shop_id") Integer reqShopId)
      throws Exception {
    this.checkWritable(writable);
//    this.checkIsSeller(role);
//    this.checkShop(shopId, reqShopId);
    this.validShop(reqShopId, userId);
    return productSellerService.changeStateForProducts(
        userId, shopId, ids.getIds(), null, ProductState.DELETED);
  }

  /**
   * change ready to Public for multi product role seller change draft to pending for multi product
   * role seller
   *
   * @param shopId
   * @param reqShopId
   * @return
   * @throws ResourceNotFoundException
   */
  @PutMapping(path = "/shops/{shop_id}/products/_bulk/state")
  public ChangeStateResponse bulkChangeState(
      @RequestBody ChangeStateDTO dto,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      @RequestHeader(name = "x-chozoi-user-role") String role,
      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
      @RequestHeader(name = "x-chozoi-seller-state") String state,
      @PathVariable(name = "shop_id") Integer reqShopId)
      throws Exception {
    this.checkWritable(writable);

//    if (!(UserRole.valueOf(role) == UserRole.SELLER)) throw new ResourceNotFoundException("Bạn chưa được phê duyệt thành nhà bán hàng!");

    this.checkShop(shopId, reqShopId);
//    if (UserRoleState.valueOf(state) != UserRoleState.APPROVED) {
//        throw new ResourceNotFoundException("Bạn chưa được phê duyệt thành nhà bán hàng!");
//    }

    if (dto.getState() == ProductState.PUBLIC) {
        return productSellerService.changeStateForProducts(
            userId, shopId, dto.getIds(), ProductState.READY, ProductState.PUBLIC);
    } else if (dto.getState() == ProductState.PENDING) {
        return productSellerService.changeStateForProducts(
            userId, shopId, dto.getIds(), ProductState.DRAFT, ProductState.PENDING);
    } else if (dto.getState() == ProductState.READY) {
        return productSellerService.changeStateForProducts(
            userId, shopId, dto.getIds(), ProductState.PUBLIC, ProductState.READY);
    } else if (dto.getState() == ProductState.DRAFT) {
        return productSellerService.changeStateForProducts(
            userId, shopId, dto.getIds(), ProductState.PENDING, ProductState.DRAFT);
    } else if (dto.getState() == ProductState.STOPPED) {
      return productSellerService.changeStateForProducts(
              userId, shopId, dto.getIds(), ProductState.PUBLIC, ProductState.STOPPED);
    } else {
        throw new ResourceNotFoundException("State required READY, DRAFT, PENDING, PUBLIC");
    }
  }

  /**
   * Add quantity for product
   *
   * @return
   * @throws ResourceNotFoundException
   */
  @PutMapping(
      path = "/shops/{shop_id}/products",
      params = {"collection=changeQuantity"})
  public Boolean addQuantity(
      @RequestBody ChangeQuantityDTO dto,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
      @RequestHeader(name = "x-chozoi-user-role") String userRoler,
      @RequestHeader(name = "x-chozoi-seller-state") String sellerState)
      throws Exception {
    this.checkWritable(writable);
    checkIsSeller(userRoler);
    if (UserRoleState.valueOf(sellerState) != UserRoleState.APPROVED) {
        throw new ResourceNotFoundException("User state dont is: APPROVED");
    }
    return productSellerService.addQuantity(dto, shopId, userId);
  }

  /**
   * Get search product
   *
   * @param pageable
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/shops/{shop_id}/products",
      params = {"collection=search"})
  public ProductsDraftEsResponse search(
      @RequestParam Map<String, String> params,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      Pageable pageable)
      throws ResourceNotFoundException {
    Page<ProductDraftEs> page = shopProductService.search(params, shopId, pageable);
    return new ProductsDraftEsResponse<>(page, ProductsPrivateResponse.class);
  }

  /**
   * Get search product
   *
   * @param pageable
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/shops/{shop_id}/products",
      params = {"collection=coupon"})
  public ProductsEsResponse search(
      String name,
      String categoryIds,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      Pageable pageable)
      throws ResourceNotFoundException {
    Page<ProductEs> page = shopProductService.forCoupon(shopId, name, categoryIds, pageable);
    return new ProductsEsResponse<>(page, ProductsPublicResponse.class);
  }

  // =================================PUBLIC===============================

  /**
   * Get products by shop id
   *
   * @param pageable
   * @param shopId
   * @return response
   */
  @GetMapping(path = "/shops/{shop_id}/products")
  public GetProductsResponse getProductByShop(
      Pageable pageable, @PathVariable("shop_id") Integer shopId) throws ResourceNotFoundException {
    Page<Product> page = productService.getByShop(shopId, pageable);
    return getProductsResponse(page);
  }

  /**
   * Get product promotion by shop type Promotion and auction
   *
   * @param pageable
   * @param shopId
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/shops/{shop_id}/products",
      params = {"collection=soldOut"})
  public ProductsEsResponse getProductSoldOutByShop(
      Pageable pageable,
      @PathVariable("shop_id") Integer shopId,
      @RequestParam Map<String, String> params)
      throws ResourceNotFoundException {
    Page<ProductEs> page = shopProductService.getProductSoldOutByShop(shopId, pageable, params);
    return new ProductsEsResponse<>(page, ProductsPublicResponse.class);
  }

  /**
   * Get product promotion by shop type Promotion and auction
   *
   * @param pageable
   * @param shopId
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/shops/{shop_id}/products",
      params = {"collection=promotion"})
  public GetProductsResponse getProductPromotionByShop(
      Pageable pageable, @PathVariable("shop_id") Integer shopId) throws ResourceNotFoundException {
    Page<Product> page = productService.getProductPromotionByShop(shopId, pageable);
    return getProductsResponse(page);
  }

  /**
   * Get product promotion by shop type Promotion and auction
   *
   * @param pageable
   * @param shopId
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/shops/{shop_id}/products",
      params = {"collection=all"})
  public ProductsEsResponse getProductAllByShop(
      Pageable pageable,
      @PathVariable("shop_id") Integer shopId,
      @RequestParam Map<String, String> params)
      throws ResourceNotFoundException {
    Page<ProductEs> page = shopProductService.getProductAllByShop(shopId, pageable, params);
    return new ProductsEsResponse<>(page, ProductsPublicResponse.class);
  }

  @GetMapping(
      path = "/shops/{shop_id}/products",
      params = {"collection=new"})
  public ProductsEsResponse getProductNewByShop(
      Pageable pageable,
      @PathVariable("shop_id") Integer shopId,
      @RequestParam Map<String, String> params) {
    Page<ProductEs> page = shopProductService.getProductAllByShop(shopId, pageable, params);
    return new ProductsEsResponse<>(page, ProductsPublicResponse.class);
  }

  /**
   * Get product Auction by shop type Promotion and auction
   *
   * @param pageable
   * @param shopId
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/shops/{shop_id}/products",
      params = {"collection=auction"})
  public ProductsEsResponse getProductAuctionByShop(
      Pageable pageable, @PathVariable("shop_id") Integer shopId) throws ResourceNotFoundException {
    Page<ProductEs> page = shopProductService.getProductAuctionByShop(shopId, pageable);
    return new ProductsEsResponse<>(page, ProductsPublicResponse.class);
  }

  @GetMapping(path = "/shops/{shop_id}/products/categories")
  public ResponseEntity<?> getShopCategories(@PathVariable(name = "shop_id") Integer shopId)
      throws IOException {
    List<CategoriesEs> categories = shopProductService.getShopCategories(shopId);
    List<CategoriesDTO> categoriesResponse = modelMapper.categoriesToDTO(categories);
    return ResponseEntity.ok(categoriesResponse);
  }

  @GetMapping(path = "/shops/{shop_id}/products/categories/level/1")
  public ResponseEntity<?> getShopCategoriesLevel1(
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId) throws IOException {
    log.info("chay vao level 1");
    List<CategoriesEs> categories = shopProductService.getShopCategoriesLevel1(shopId);
    List<CategoriesDTO> categoriesResponse = modelMapper.categoriesToDTO(categories);
    return ResponseEntity.ok(categoriesResponse);
  }

  // TODO: remove api
  @GetMapping(path = "/shops/{shop_id}/products/categories/{categories_id}")
  public ProductsEsResponse getShopProduct(
      Pageable pageable,
      @PathVariable(name = "shop_id") Integer shopId,
      @PathVariable(name = "categories_id") Integer categoriesId,
      @RequestParam Map<String, String> params) {
    Page<ProductEs> page =
        shopProductService.getProductCategoriesByShop(
            shopId, categoriesId, pageable, params); // ShopProductDTO
    return new ProductsEsResponse<>(page, ProductsPublicResponse.class);
  }
}
