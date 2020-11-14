package com.chozoi.product.domain.services.mongo;

import com.chozoi.product.app.dtos.ProductActiveCodeDTO;
import com.chozoi.product.app.responses.ProductPrivateCodeResponse;
import com.chozoi.product.domain.entities.elasticsearch.AuctionParticipantEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.mongodb.ProductLike;
import com.chozoi.product.domain.entities.mongodb.ProductMongo;
import com.chozoi.product.domain.entities.mongodb.ProductViewed;
import com.chozoi.product.domain.entities.postgres.*;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionType;
import com.chozoi.product.domain.exceptions.ExceptionMessage;
import com.chozoi.product.domain.exceptions.HandlerException;
import com.chozoi.product.domain.exceptions.ProductNotFoundException;
import com.chozoi.product.domain.producers.ProductFavoriteProducer;
import com.chozoi.product.domain.producers.factories.SuggestionFactory;
import com.chozoi.product.domain.repositories.elasticsearch.AuctionParticipantEsRepository;
import com.chozoi.product.domain.repositories.elasticsearch.AuctionResultRepository;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import com.chozoi.product.domain.repositories.mongodb.ProductLikeMDRepository;
import com.chozoi.product.domain.repositories.mongodb.ProductMDRepository;
import com.chozoi.product.domain.repositories.mongodb.ProductViewMDRepository;
import com.chozoi.product.domain.repositories.postgres.*;
import com.chozoi.product.domain.values.AuctionPaymentRefuseId;
import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@Log4j2
public class BuyerProductService {
  @Autowired private ProductLikeMDRepository productLikeMDRepository;

  @Autowired private ProductViewMDRepository productViewMDRepository;

  @Autowired private ProductEsRepository productEsRepository;

  @Autowired private ProductMDRepository productMDRepository;

  @Autowired private AuctionRepository auctionRepository;

  @Autowired private SuggestionFactory suggestionFactory;
  @Autowired private AuctionParticipantEsRepository auctionParticipantEsRepository;
  @Autowired private AuctionOnlyRepository auctionOnlyRepository;
  @Autowired private AuctionResultRepository auctionResultRepository;
  @Autowired private AuctionPhaseRepository auctionPhaseRepository;
  @Autowired private AuctionPaymentRefuseRepository auctionPaymentRefuseRepository;

  @Autowired private ProductFavoriteProducer productFavoriteProducer;

  @Autowired private ProductActiveCodeRepository productActiveCodeRepository;

  /**
   * add product id to list product like by userId
   *
   * @param userId
   * @param productId
   */
  public ProductLike createLike(String userId, Long productId) throws Exception {
    ProductLike productLike;
    try {
      ProductMongo product = checkProduct(productId);
      productLike = productLikeMDRepository.findById(userId).orElse(new ProductLike());
      List<Long> productIds =
          ObjectUtils.defaultIfNull(productLike.getProductIds(), new ArrayList<>());
      Long createdAt =
          ObjectUtils.defaultIfNull(productLike.getCreatedAt(), System.currentTimeMillis());
      Long updatedAt = System.currentTimeMillis();
      if (!productIds.contains(productId)) productIds.add(productId);
      productLike.setId(userId);
      productLike.setProductIds(productIds);
      productLike.setCreatedAt(createdAt);
      productLike.setCreatedAt(updatedAt);
      productLikeMDRepository.save(productLike);
      suggestionFactory.likeFactory(userId, productId);

      productFavoriteProducer.save(product.getShop().getId(), Integer.valueOf(userId), productId, "like");
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }

    return productLike;
  }

  /**
   * check exist and public by product
   *
   * @param productId
   * @throws Exception
   * @return
   */
  private ProductMongo checkProduct(Long productId) throws Exception {
    ProductMongo product =
        productMDRepository
            .findById(productId)
            .orElseThrow(() -> new Exception(ExceptionMessage.PRODUCT_NOT_FOUND));
    if (!product.getState().equals("PUBLIC")) throw new Exception(ExceptionMessage.PRODUCT_NOT_PUBLIC);

    return product;
  }

  /**
   * add product id to list product view by userId
   *
   * @param userId
   * @param productId
   */
  public void createView(String userId, Long productId) throws IOException {
    //    ProductViewed productViewed = productViewMDRepository.findById(userId).orElse(new
    // ProductViewed());
    //    List<Long> productIds = ObjectUtils.defaultIfNull(productViewed.getProductIds(), new
    // ArrayList<>());
    //    Long createdAt = ObjectUtils.defaultIfNull(productViewed.getCreatedAt(),
    // System.currentTimeMillis());
    //    Long updatedAt = System.currentTimeMillis();
    //    if ( !productIds.contains(productId) ) productIds.add(productId);
    //    else {
    //      productIds.remove(productId);
    //      productIds.add(productId);
    //    }
    //    if ( productIds.size() > 100 ) {
    //      int count = productIds.size() - 100;
    //      for (int i = 0; i < count; i++) productIds.remove(0);
    //    }
    //    productViewed.setId(userId);
    //    productViewed.setProductIds(productIds);
    //    productViewed.setCreatedAt(createdAt);
    //    productViewed.setCreatedAt(updatedAt);
    //    productViewMDRepository.save(productViewed);
    suggestionFactory.viewFactory(userId, productId);
  }

  /**
   * remove product id to list product view by userId
   *
   * @param userId
   * @param productId
   */
  public boolean removeLikeId(String userId, Long productId) throws IOException {
    try {
      ProductMongo product = checkProduct(productId);
      ProductLike productLike = productLikeMDRepository.findById(userId).orElse(new ProductLike());
      List<Long> productIds =
              ObjectUtils.defaultIfNull(productLike.getProductIds(), new ArrayList<>());
      Long createdAt =
              ObjectUtils.defaultIfNull(productLike.getCreatedAt(), System.currentTimeMillis());
      Long updatedAt = System.currentTimeMillis();
      productIds.remove(productId);
      productLike.setId(userId);
      productLike.setProductIds(productIds);
      productLike.setCreatedAt(createdAt);
      productLike.setCreatedAt(updatedAt);
      productLikeMDRepository.save(productLike);
      // suggestion event product
      suggestionFactory.unLikeFactory(userId, productId);

      productFavoriteProducer.save(product.getShop().getId(), Integer.valueOf(userId), productId, "dislike");
    } catch (Exception e) {
      Sentry.capture(e);
      return true;
    }

    return true;
  }

  /**
   * r get list product view by userId
   *
   * @param userId
   * @return
   */
  public Page<ProductEs> getProductView(String userId, Pageable pageable) {
    ProductViewed productViewed =
        productViewMDRepository.findById(userId).orElse(new ProductViewed());
    List<Long> ids =
        Objects.isNull(productViewed.getId()) ? new ArrayList<>() : productViewed.getProductIds();
    Collections.reverse(ids);
    return productEsRepository.findByIdInAndState(ids, "PUBLIC", pageable);
  }

  /**
   * get list product like by userId
   *
   * @param userId
   * @return
   */
  public Page<ProductEs> getProductLike(String userId, String type, Pageable pageable) {
    ProductLike productLike = productLikeMDRepository.findById(userId).orElse(new ProductLike());
    List<Long> ids =
        Objects.isNull(productLike.getId()) ? new ArrayList<>() : productLike.getProductIds();
    Collections.reverse(ids);
    return productEsRepository.findByIdInAndState(ids, "PUBLIC", pageable);
  }

  /**
   * Refuse payment
   *
   * @param userId
   * @param productId
   */
  public Boolean refusePayment(Integer userId, Long productId) throws Exception {
    // check auction stopped
    Auction auction =
        auctionRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
    if (auction.getState() != ProductAuctionState.STOPPED)
      throw new HandlerException(ExceptionMessage.AUCTION_NOT_OVER);
    // check userId is winner
    if (Objects.isNull(auction.getResult())) throw new HandlerException(ExceptionMessage.AUCTION_USER_NOT_WINNER);
    Integer winnerId = auction.getResult().getWinnerId();
    if (Objects.isNull(winnerId)) throw new HandlerException(ExceptionMessage.AUCTION_USER_NOT_WINNER);
    if (!userId.equals(winnerId)) throw new HandlerException(ExceptionMessage.AUCTION_USER_NOT_WINNER);
    // update refuse payment = true
    auction.setRefusePayment(true);
    auctionRepository.save(auction);
    return true;
    // return
  }

  public Boolean refusePayment(Integer userId, Long productId, Long phaseId) throws Exception {
    // check auction stopped
    Auction auction =
            auctionRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

    Optional<AuctionPhase> auctionPhaseOptional = auctionPhaseRepository.findById(phaseId);
    if (!auctionPhaseOptional.isPresent())
      throw new HandlerException("Phiên đấu giá không tồn tại!");

    AuctionPhase auctionPhase = auctionPhaseOptional.get();
    LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);
    if (auctionPhase.getStartTime().isAfter(currentTime)) {
      throw new HandlerException("Phiên đấu giá chưa bắt đầu!");
    }
    if (auctionPhase.getEndTime().isAfter(currentTime)) {
      throw new HandlerException("Phiên đấu giá chưa kết thúc!");
    }

    Integer winnerId = auctionPhase.getWinnerId();
    if (!auction.getType().equals(ProductAuctionType.FLASH_BID)) {
      if (auction.getState() != ProductAuctionState.STOPPED)
        throw new HandlerException(ExceptionMessage.AUCTION_NOT_OVER);

      if (Objects.isNull(auction.getResult())) throw new HandlerException(ExceptionMessage.AUCTION_USER_NOT_WINNER);
      winnerId = auction.getResult().getWinnerId();
    }

    // check userId is winner
    if (Objects.isNull(auction.getResult())) throw new HandlerException(ExceptionMessage.AUCTION_USER_NOT_WINNER);
    if (Objects.isNull(winnerId)) throw new HandlerException(ExceptionMessage.AUCTION_USER_NOT_WINNER);
    if (!userId.equals(winnerId)) throw new HandlerException(ExceptionMessage.AUCTION_USER_NOT_WINNER);
    // update refuse payment = true
    auction.setRefusePayment(true);
    auctionRepository.save(auction);

    AuctionPaymentRefuse auctionPaymentRefuse = AuctionPaymentRefuse.builder()
            .id(new AuctionPaymentRefuseId(auctionPhase.getId(), auction.getId()))
            .userId(userId)
            .paymentRefused(true)
            .refusedAt(currentTime)
            .build();
    auctionPaymentRefuseRepository.save(auctionPaymentRefuse);
    return true;
  }

  public Long countAuctionByUser(Integer userId) {
    List<AuctionOnly> auctionEsList = auctionOnlyRepository.findByState(ProductAuctionState.BIDING);
    List<Long> phaseIds = new ArrayList<>();
    auctionEsList.forEach(auctionOnly -> {
      if (auctionOnly.getPhaseId() != null) {
        phaseIds.add(auctionOnly.getPhaseId());
      }
    });

    long total = 0;
    if (phaseIds.size() > 0) {
      Page<AuctionParticipantEs> auctionParticipantEsPage = auctionParticipantEsRepository.findByPhaseIdInAndUserIdOrderByCreatedAtDesc(phaseIds, userId, new PageRequest(0,1));
      total = auctionParticipantEsPage.getTotalElements();
    }

    return total;
  }

    public ProductPrivateCodeResponse activeCode(Integer userId, Integer productId, ProductActiveCodeDTO productActiveCodeDTO) {
      try {
        if (productActiveCodeDTO.getActiveCode() == null || productActiveCodeDTO.getActiveCode().isEmpty()) {
          return ProductPrivateCodeResponse.builder().status(false).message("Mã tham gia không hợp lệ!").build();
        }

        ProductMongo product = checkProduct(Long.valueOf(productId));
        ProductActiveCode exist = productActiveCodeRepository.findFirstByUserIdAndProductId(userId, productId);
        if (exist != null) {
          return ProductPrivateCodeResponse.builder().status(true).message("Bạn đã được tham gia đấu giá sản phẩm này!").build();
        }

        if (product.getPrivateCode() == null || product.getPrivateCode().isEmpty()) {
          return ProductPrivateCodeResponse.builder().status(false).message("Sản phẩm không giới hạn tham gia!").build();
        }

        if (!product.getPrivateCode().equals(productActiveCodeDTO.getActiveCode())) {
          return ProductPrivateCodeResponse.builder().status(false).message("Mã tham gia không hợp lệ!").build();
        }

        ProductActiveCode productActiveCode = new ProductActiveCode();
        productActiveCode.setUserId(userId);
        productActiveCode.setProductId(productId);

        productActiveCodeRepository.save(productActiveCode);
        return ProductPrivateCodeResponse.builder().status(true).message("Bạn đã được tham gia đấu giá sản phẩm này!").build();
      } catch (Exception e) {
        e.printStackTrace();
      }

      return null;
    }
}
