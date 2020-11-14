package com.chozoi.product.app.controllers.v1;

import com.chozoi.product.app.dtos.ProductActiveCodeDTO;
import com.chozoi.product.app.dtos.ProductCreateDTO;
import com.chozoi.product.app.responses.ProductBuyerResponse;
import com.chozoi.product.app.responses.ProductPrivateCodeResponse;
import com.chozoi.product.app.responses.ProductsEsResponse;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.mongodb.ProductLike;
import com.chozoi.product.domain.entities.postgres.ProductActiveCode;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
import com.chozoi.product.domain.services.mongo.BuyerProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController()
@RequestMapping("/v1/buyer")
@Log4j2
public class BuyerProductController extends BaseProductController {
  @Autowired private BuyerProductService service;

  /**
   * like product
   *
   * @return
   * @throws ResourceNotFoundException
   */
  @PostMapping(
      path = "/{user_id}/products/{product_id}",
      params = {"collection=like"})
  public ProductLike createLike(
      @RequestHeader(name = "x-chozoi-user-id") String userId,
      @PathVariable(name = "product_id") Long productId)
      throws Exception {
    return service.createLike(userId, productId);
  }

  @PostMapping(
          path = "/{user_id}/products/{product_id}",
          params = {"collection=active_code"})
  public ProductPrivateCodeResponse activeCode(
          @Valid @RequestBody ProductActiveCodeDTO productActiveCodeDTO,
          @RequestHeader(name = "x-chozoi-user-id") Integer userId,
          @PathVariable(name = "product_id") Integer productId)
          throws Exception {
    return service.activeCode(userId, productId, productActiveCodeDTO);
  }

  /**
   * Create product
   *
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/{user_id}/products",
      params = {"collection=count_auction"})
  public Long getCountAuction(@RequestHeader(name = "x-chozoi-user-id") Integer userId)
      throws Exception {
    return service.countAuctionByUser(userId);
  }

  /**
   * remove product
   *
   * @return
   * @throws ResourceNotFoundException
   */
  @DeleteMapping(
      path = "/{user_id}/products/{product_id}",
      params = {"collection=like"})
  public boolean removeLike(
      @RequestHeader(name = "x-chozoi-user-id") String userId,
      @PathVariable(name = "product_id") Long productId)
      throws Exception {
    return service.removeLikeId(userId, productId);
  }

  /**
   * get product like
   *
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/{user_id}/products",
      params = {"collection=like"})
  public ProductBuyerResponse getByProductLike(
      @RequestHeader(name = "x-chozoi-user-id") String userId, String type, Pageable pageable)
      throws Exception {
    Page<ProductEs> page = service.getProductLike(userId, type, pageable);
    return new ProductBuyerResponse(page);
  }

  /**
   * get product like
   *
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/{user_id}/products/_auth",
      params = {"collection=view"})
  public ProductBuyerResponse getProductViewPrivate(
      @RequestHeader(name = "x-chozoi-user-id") String userId, Pageable pageable) throws Exception {
    Page<ProductEs> page = service.getProductView(userId, pageable);
    return new ProductBuyerResponse(page);
  }

  /**
   * get product like
   *
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(
      path = "/{user_id}/products",
      params = {"collection=view"})
  public ProductBuyerResponse getProductView(
      @PathVariable(name = "user_id") String userId, Pageable pageable) throws Exception {
    Page<ProductEs> page = service.getProductView(userId, pageable);
    return new ProductBuyerResponse(page);
  }

  /**
   * get product like
   *
   * @return
   * @throws ResourceNotFoundException
   */

  @PutMapping(
          path = "/{user_id}/products/{product_id}",
          params = {"collection=refuse_payment"})
  public boolean refusePaymentPhase(
          @RequestHeader("x-chozoi-user-id") Integer userId, @PathVariable("product_id") Long productId, @RequestParam Long phaseId)
          throws Exception {
    if (phaseId != null) {
      return service.refusePayment(userId, productId, phaseId);
    } else {
      return service.refusePayment(userId, productId);
    }
  }

  @GetMapping(
      path = "{user_id}/products",
      params = {"collection=my_auction"})
  public Object getMyAuctionProducts(
      @RequestHeader("X-Chozoi-User-Id") Integer userId, Pageable pageable) throws Exception {
    Page<ProductEs> page = auctionParticipantService.findByUser(userId, pageable);
    return new ProductsEsResponse<>(page, ProductEs.class);
  }

  @GetMapping(
      path = "{user_id}/products",
      params = {"collection=my_auction", "state=WINNER"})
  public ProductsEsResponse getMyAuctionWinner(
      @RequestHeader("X-Chozoi-User-Id") Integer userId, Pageable pageable) throws Exception {
    Page<ProductEs> page = auctionParticipantService.findByWinner(userId, pageable);
    return new ProductsEsResponse<>(page, ProductEs.class);
  }

  @GetMapping(
      path = "{user_id}/products",
      params = {"collection=my_auction", "state=BIDDING"})
  public ProductsEsResponse getMyAuctionBidding(
      @RequestHeader("X-Chozoi-User-Id") Integer userId, Pageable pageable) throws Exception {
    Page<ProductEs> page = auctionParticipantService.findAuctionBiding(userId, pageable);
    return new ProductsEsResponse<>(page, ProductEs.class);
  }

  @GetMapping(
      path = "{user_id}/products",
      params = {"collection=my_auction", "state=REFUSED"})
  public ProductsEsResponse getAuctionRefused(
      @RequestHeader("X-Chozoi-User-Id") Integer userId, Pageable pageable) throws Exception {
    Page<ProductEs> page = auctionParticipantService.findAuctionRefused(userId, pageable);
    return new ProductsEsResponse<>(page, ProductEs.class);
  }
}
