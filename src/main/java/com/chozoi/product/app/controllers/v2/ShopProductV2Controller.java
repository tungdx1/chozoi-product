package com.chozoi.product.app.controllers.v2;

import com.chozoi.product.app.dtos.ProductCreateDTO;
import com.chozoi.product.app.dtos.RestartAuctionDTO;
import com.chozoi.product.app.responses.ProductUpdateResponse;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.UserRoleState;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
import com.chozoi.product.domain.services.version_2.ProductFacade;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController()
@RequestMapping("/v1/shops")
@Log4j2
public class ShopProductV2Controller {
  @Autowired ProductFacade productFacade;

  @Value("${writable}")
  private Boolean writable;

  protected void checkWritable(Boolean writable) throws Exception {
    if (!writable) throw new Exception("Hệ thống bảo trì");
  }

  /**
   * Create product
   *
   * @param productDto
   * @param shopId
   * @return
   * @throws ResourceNotFoundException
   */
  @PostMapping(path = "/{shop_id}/products")
  public Product create(
      @Valid @RequestBody ProductCreateDTO productDto,
      @PathVariable(name = "shop_id") Integer reqShopId,
      @RequestHeader(name = "x-chozoi-seller-state") String state,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      @RequestHeader(name = "x-chozoi-user-id") Integer userId)
      throws Exception {
    UserRoleState uState = UserRoleState.PENDING;
    if (!state.isEmpty()) {
      uState = UserRoleState.valueOf(state);
    }
    return productFacade.create(productDto, uState, userId, reqShopId);
  }

  /**
   * update product
   *
   * @param dto
   * @param state
   * @param shopId
   * @param userId
   * @param reqShopId
   * @param productId
   * @return
   * @throws Exception
   */
  @PutMapping(path = "/{shop_id}/products/{product_id}")
  public ProductUpdateResponse update(
      @Valid @RequestBody ProductCreateDTO dto,
      @RequestHeader(name = "x-chozoi-seller-state") String state,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
      @PathVariable(name = "shop_id") Integer reqShopId,
      @PathVariable(name = "product_id") Long productId)
      throws Exception {
    this.checkWritable(writable);
    UserRoleState uState = UserRoleState.PENDING;
    if (!state.isEmpty()) {
      uState = UserRoleState.valueOf(state);
    }
    productFacade.update(productId, shopId, dto, uState, userId);
    return ProductUpdateResponse.builder()
        .id(productId)
        .status(true)
        .statusValidation(true)
        .message("success")
        .build();
  }

  /**
   * update product
   *
   * @param dto
   * @param shopId
   * @param userId
   * @param reqShopId
   * @param productId
   * @return
   * @throws Exception
   */
  @PutMapping(
      path = "/{shop_id}/products/{product_id}",
      params = {"collection=re_auction"})
  public Product reAuction(
      @Valid @RequestBody RestartAuctionDTO dto,
      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
      @PathVariable(name = "shop_id") Integer reqShopId,
      @PathVariable(name = "product_id") Long productId)
      throws Exception {
    this.checkWritable(writable);
    return productFacade.reAuction(productId, shopId, userId, dto);
  }

  //  /**
  //   * Change state auction
  //   *
  //   * @return
  //   * @throws ResourceNotFoundException
  //   */
  //  @PutMapping(
  //      path = "/{shop_id}/products/{product_id}",
  //      params = {"collection=auction"})
  //  public boolean changeStateAuction(
  //      @Valid @RequestBody ChangeStateAuctionDto dto,
  //      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
  //      @PathVariable(name = "product_id") Long productId)
  //      throws Exception {
  //    return productFacade.changeStateAuction(shopId, productId, dto);
  //  }

  //  /** update product */
  //  @PutMapping(path = "/shops/{shop_id}/products/{product_id}/partial")
  //  public boolean updatePartial(
  //      @Valid @RequestBody UpdatePartialProductDTO dto,
  //      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
  //      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
  //      @PathVariable(name = "product_id") Long productId)
  //      throws Exception {
  //    this.checkWritable(writable);
  //    return productFacade.updatePartial(dto, shopId, productId, userId);
  //  }
  //
  //  /**
  //   * Get product by id role seller
  //   *
  //   * @return
  //   */
  //  @GetMapping(path = "/shops/{shop_id}/products/{product_id}")
  //  public void productRoleSeller( // ProductPrivateReponse
  //      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
  //      @PathVariable(name = "shop_id") Integer reqShopId,
  //      @PathVariable(name = "product_id") Long productId)
  //      throws Exception {
  //    // TODO: get product role seller
  //  }
  //
  //  /**
  //   * get product by shop
  //   *
  //   * @param pageable
  //   * @return
  //   * @throws ResourceNotFoundException
  //   */
  //  @GetMapping(
  //      path = "/shops/{shop_id}/products",
  //      headers = {"X-Chozoi-User-Role=SELLER"})
  //  public void getByShop( // GetProductsResponse
  //      @RequestHeader(name = "X-Chozoi-Seller-State") String state,
  //      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
  //      Pageable pageable,
  //      @PathVariable(name = "shop_id") Integer reqShopId)
  //      throws ResourceNotFoundException {
  //    // TODO: get product by shop
  //
  //  }
  //
  //  /**
  //   * Get product auction for seller
  //   *
  //   * @param shopId
  //   * @param pageable
  //   * @return
  //   * @throws ResourceNotFoundException
  //   */
  //  @GetMapping(
  //      value = "/shops/{shop_id}/products",
  //      params = "collection=auction",
  //      headers = {"X-Chozoi-User-Role=SELLER"})
  //  public void getProductAuction(
  //      @RequestHeader(name = "X-Chozoi-Seller-State") String state,
  //      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
  //      Pageable pageable,
  //      @PathVariable(name = "shop_id") Integer reqShopId)
  //      throws ResourceNotFoundException {
  //    // TODO: Get product auction for seller
  //  }
  //
  //  /**
  //   * Delete multi product role seller
  //   *
  //   * @param ids
  //   * @param shopId
  //   * @param pageable
  //   * @return
  //   * @throws ResourceNotFoundException
  //   */
  //  @DeleteMapping(path = "/shops/{shop_id}/products/_bulk")
  //  public void deleteMultiProduct( // ChangeStateResponse
  //      @RequestBody ProductIdDTO ids,
  //      @RequestHeader(name = "x-chozoi-user-role") String role,
  //      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
  //      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
  //      @PageableDefault(page = 0, size = 2000) Pageable pageable,
  //      @PathVariable(name = "shop_id") Integer reqShopId)
  //      throws Exception {
  //    // TODO: Delete multi product role seller
  //  }
  //
  //  /**
  //   * change ready to Public for multi product role seller change draft to pending for multi
  // product
  //   * role seller
  //   *
  //   * @param shopId
  //   * @param reqShopId
  //   * @return
  //   * @throws ResourceNotFoundException
  //   */
  //  @PutMapping(path = "/shops/{shop_id}/products/_bulk/state")
  //  public void bulkChangeState( // ChangeStateResponse
  //      @RequestBody ChangeStateDTO dto,
  //      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
  //      @RequestHeader(name = "x-chozoi-user-role") String role,
  //      @RequestHeader(name = "x-chozoi-user-id") Integer userId,
  //      @RequestHeader(name = "x-chozoi-seller-state") String state,
  //      @PathVariable(name = "shop_id") Integer reqShopId)
  //      throws Exception {
  //    // TODO : change state
  //  }
  //
  //  /**
  //   * Get search product
  //   *
  //   * @param pageable
  //   * @return
  //   * @throws ResourceNotFoundException
  //   */
  //  @GetMapping(
  //      path = "/shops/{shop_id}/products",
  //      params = {"collection=search"})
  //  public void search( // ProductsDraftEsResponse
  //      @RequestParam Map<String, String> params,
  //      @RequestHeader(name = "x-chozoi-shop-id") Integer shopId,
  //      Pageable pageable)
  //      throws ResourceNotFoundException {
  //    // TODO: search seller
  //  }
  //
  //  // =================================PUBLIC===============================
  //
  //  /**
  //   * Get products by shop id
  //   *
  //   * @param pageable
  //   * @param shopId
  //   * @return response
  //   */
  //  @GetMapping(path = "/shops/{shop_id}/products")
  //  public void getProductByShop( // GetProductsResponse
  //      Pageable pageable, @PathVariable("shop_id") Integer shopId) throws
  // ResourceNotFoundException {
  //    // TODO: Get products by shop id
  //  }
  //
  //  /**
  //   * Get product promotion by shop type Promotion and auction
  //   *
  //   * @param pageable
  //   * @param shopId
  //   * @return
  //   * @throws ResourceNotFoundException
  //   */
  //  @GetMapping(
  //      path = "/shops/{shop_id}/products",
  //      params = {"collection=soldOut"})
  //  public void getProductSoldOutByShop( // ProductsEsResponse
  //      Pageable pageable,
  //      @PathVariable("shop_id") Integer shopId,
  //      @RequestParam Map<String, String> params)
  //      throws ResourceNotFoundException {
  //    // TODO: Get product promotion by shop type Promotion and auction
  //  }
  //
  //  /**
  //   * Get all
  //   *
  //   * @param pageable
  //   * @param shopId
  //   * @return
  //   * @throws ResourceNotFoundException
  //   */
  //  @GetMapping(
  //      path = "/shops/{shop_id}/products",
  //      params = {"collection=all"})
  //  public void getProductAllByShop( // ProductsEsResponse
  //      Pageable pageable,
  //      @PathVariable("shop_id") Integer shopId,
  //      @RequestParam Map<String, String> params)
  //      throws ResourceNotFoundException {
  //    // TODO: get all
  //  }
  //
  //  @GetMapping(
  //      path = "/shops/{shop_id}/products",
  //      params = {"collection=new"})
  //  public void getProductNewByShop( // ProductsEsResponse
  //      Pageable pageable,
  //      @PathVariable("shop_id") Integer shopId,
  //      @RequestParam Map<String, String> params) {
  //    // TODO: get new for shop;
  //  }
  //
  //  /**
  //   * Get product Auction by shop
  //   *
  //   * @param pageable
  //   * @param shopId
  //   * @return
  //   * @throws ResourceNotFoundException
  //   */
  //  @GetMapping(
  //      path = "/shops/{shop_id}/products",
  //      params = {"collection=auction"})
  //  public void getProductAuctionByShop( // ProductsEsResponse
  //      Pageable pageable, @PathVariable("shop_id") Integer shopId) throws
  // ResourceNotFoundException {
  //    // TODO: Get product Auction by shop
  //  }
  //
  //  @GetMapping(path = "/shops/{shop_id}/products/categories")
  //  public void getShopCategories(@PathVariable(name = "shop_id") Integer shopId) //
  // ResponseEntity<?>
  //      throws IOException {
  //    //        List<CategoriesEs> categories = shopProductService.getShopCategories(shopId);
  //    //        List<CategoriesDTO> categoriesResponse = modelMapper.categoriesToDTO(categories);
  //    //        return ResponseEntity.ok(categoriesResponse);
  //  }
}
