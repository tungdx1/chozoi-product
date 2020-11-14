package com.chozoi.product.domain.services;

import com.chozoi.product.domain.entities.elasticsearch.*;
import com.chozoi.product.domain.entities.postgres.*;
import com.chozoi.product.domain.entities.postgres.types.InstantBidType;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.repositories.elasticsearch.*;
import com.chozoi.product.domain.repositories.postgres.*;
import com.chozoi.product.domain.utils.ProductUtils;
import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AuctionParticipantService {

  @Autowired private AuctionParticipantEsRepository auctionParticipantRepository;
  @Autowired private AuctionParticipantFlashBidRepository auctionParticipantFlashBidRepository;
  @Autowired private AuctionOnlyRepository auctionOnlyRepository;

  @Autowired private AuctionParticipantEsRepository auctionParticipantEsRepository;

  @Autowired private InstantBidEsRepository instantBidEsRepository;
  @Autowired private AuctionResultFlashBidRepository auctionResultFlashBidRepository;
  @Autowired private AuctionResultRepository auctionResultRepository;

  @Autowired private ProductEsRepository productEsRepository;
  @Autowired private AuctionPhaseRepository auctionPhaseRepository;
  @Autowired private AuctionPaymentRefuseRepository auctionPaymentRefuseRepository;
  @Autowired private OrderGuardFlashBidRepository orderGuardFlashBidRepository;
  @Autowired private AutoBidRepository autoBidRepository;

  /**
   * * Find my auciton for user
   *
   * @param userId
   * @param pageable
   * @return
   */
  public Page<ProductEs> findByUser(Integer userId, Pageable pageable) throws Exception {
    Page<AuctionParticipantEs> auctionParticipantEsPage =
            auctionParticipantEsRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    List<AuctionParticipantEs> participantEs = auctionParticipantEsPage.getContent();
    List<Long> ids =
            participantEs.stream()
            .map(AuctionParticipantEs::getAuctionId)
            .collect(Collectors.toList());

    List<ProductEs> productEsList = new ArrayList<>();
    if (ids.size() > 0) {
      productEsList = productEsRepository.findByIdInAndState(ids, "PUBLIC");
    }
    return this.metaDataAuctionForUser(userId, participantEs, productEsList, pageable, auctionParticipantEsPage.getTotalElements());
  }

  /**
   * Find product winner for user
   *
   * @param userId user id
   * @param pageable page able
   * @return
   */
  public Page<ProductEs> findByWinner(Integer userId, Pageable pageable) throws Exception {
    List<AuctionResultData> auctionResultData =
        auctionResultRepository.findByWinnerIdOrderByCreatedAtDesc(Long.valueOf(userId));
    List<Long> phaseIds =
        auctionResultData.stream()
            .map(AuctionResultData::getPhaseId)
            .collect(Collectors.toList());

    long total = 0;
    List<AuctionParticipantEs> participantEs = new ArrayList<>();
    List<ProductEs> productEsList = new ArrayList<>();
    if (phaseIds.size() > 0) {
      Page<AuctionParticipantEs> auctionParticipantEsPage = auctionParticipantEsRepository.findByPhaseIdInAndUserIdOrderByCreatedAtDesc(phaseIds, userId, pageable);
      participantEs = auctionParticipantEsPage.getContent();
      total = auctionParticipantEsPage.getTotalElements();

      List<Long> productIds =
              auctionParticipantEsPage.getContent().stream().map(AuctionParticipantEs::getAuctionId).collect(Collectors.toList());
      if (productIds.size() > 0) {
        productEsList = productEsRepository.findByIdInAndState(productIds, "PUBLIC");
      }
    }

    return this.metaDataAuctionForUser(userId, participantEs, productEsList, pageable, total);
  }

  /**
   * Find product bidding for user
   *
   * @param userId user id
   * @param pageable page
   * @return
   */
  public Page<ProductEs> findAuctionBiding(Integer userId, Pageable pageable) throws Exception {
    List<AuctionOnly> auctionEsList = auctionOnlyRepository.findByState(ProductAuctionState.BIDING);
    List<Long> phaseIds = new ArrayList<>();
    auctionEsList.forEach(auctionOnly -> {
      if (auctionOnly.getPhaseId() != null) {
        phaseIds.add(auctionOnly.getPhaseId());
      }
    });

    long total = 0;
    List<AuctionParticipantEs> participantEs = new ArrayList<>();
    List<ProductEs> productEsList = new ArrayList<>();
    if (phaseIds.size() > 0) {
      Page<AuctionParticipantEs> auctionParticipantEsPage = auctionParticipantEsRepository.findByPhaseIdInAndUserIdOrderByCreatedAtDesc(phaseIds, userId, pageable);
      participantEs = auctionParticipantEsPage.getContent();
      total = auctionParticipantEsPage.getTotalElements();

      List<Long> productIds =
              participantEs.stream().map(AuctionParticipantEs::getAuctionId).collect(Collectors.toList());
      if (productIds.size() > 0)
        productEsList = productEsRepository.findByIdInAndState(productIds, "PUBLIC");
      addCurrentPhaseForFlashBid(productEsList);
    }

    return this.metaDataAuctionForUser(userId, participantEs, productEsList, pageable, total);
  }

  public Page<ProductEs> findAuctionRefused(Integer userId, Pageable pageable) throws Exception {
    Page<AuctionPaymentRefuse> auctionPaymentRefusePage = auctionPaymentRefuseRepository.findByUserId(userId, pageable);
    List<Long> phaseIds = new ArrayList<>();
    List<Long> productIds = new ArrayList<>();
    auctionPaymentRefusePage.getContent().forEach(paymentRefuse -> {
      phaseIds.add(paymentRefuse.getId().getPhaseId());
      productIds.add(paymentRefuse.getId().getAuctionId());
    });

    long total = 0;
    List<AuctionParticipantEs> participantEs = new ArrayList<>();
    List<ProductEs> productEsList = new ArrayList<>();
    if (phaseIds.size() > 0) {
      participantEs = auctionParticipantEsRepository.findAllByPhaseIdInAndUserId(phaseIds, userId);
      total = auctionPaymentRefusePage.getTotalElements();

      if (productIds.size() > 0)
        productEsList = productEsRepository.findByIdInAndState(productIds, "PUBLIC");
      addCurrentPhaseForFlashBid(productEsList);
    }

    return this.metaDataAuctionForUser(userId, participantEs, productEsList, pageable, total);
  }

  private List<AuctionParticipantEs> getParticipant(Integer userId) {
    List<AuctionParticipantFlashBid> participantFlashBids =
        auctionParticipantFlashBidRepository.findById_UserIdOrderByCreatedAtDesc(userId);
    return ProductUtils.auctionParcitipantPgToEs(participantFlashBids);
  }

  private void addCurrentPhaseForFlashBid(List<ProductEs> productEsList) {
    for (ProductEs productEs : productEsList)
      if (productEs.getType().equals(String.valueOf(ProductType.AUCTION_FLASH_BID))) {
        Optional<AuctionOnly> auctionOnly = auctionOnlyRepository.findById(productEs.getId());
        if (auctionOnly.isPresent()) {
          Long phaseId = auctionOnly.get().getPhaseId();
          Optional<AuctionPhase> auctionPhase = auctionPhaseRepository.findById(phaseId);
          if (auctionPhase.isPresent()) {
            AuctionPhase auctionPhase1 = auctionPhase.get();
            productEs.getAuction().setPhaseId(auctionPhase1.getId());
            productEs.getAuction().setTimeEnd(auctionPhase1.getEndTime());
            productEs.getAuction().setTimeStart(auctionPhase1.getStartTime());
          }
        }
      }
  }

  private Page<ProductEs> metaDataAuctionForUser(
          Integer userId, List<AuctionParticipantEs> auctionParticipantEs, List<ProductEs> list, Pageable pageable, long totalElements)
      throws Exception {
    List<ProductEs> productEsList = new ArrayList<>();
    // add last price and type
    List<Long> instantIds =
        auctionParticipantEs.stream()
            .map(AuctionParticipantEs::getInstantBidId)
            .collect(Collectors.toList());
    List<Long> phaseIds =
            auctionParticipantEs.stream()
                    .map(AuctionParticipantEs::getPhaseId)
                    .collect(Collectors.toList());
    if (auctionParticipantEs.size() > 0) {
      for (int i = 0; i < auctionParticipantEs.size(); i++) auctionParticipantEs.get(i).setSort(i);
      Iterable<InstantBidEs> instantBidEsIterable = instantBidEsRepository.findAllById(instantIds);
      List<AuctionRessultFlashBid> auctionResultFlashBids = auctionResultFlashBidRepository.findAllByIdPhaseIdIn(phaseIds);
      List<AuctionPaymentRefuse> auctionPaymentRefuseList = auctionPaymentRefuseRepository.findByIdPhaseIdIn(phaseIds);
      List<OrderGuardFlashBid> orderGuardFlashBidList = orderGuardFlashBidRepository.findByPhaseIdIn(phaseIds);
      List<AutoBid> autoBidList = autoBidRepository.findByUserIdAndPhaseIdIn(userId, phaseIds);
      List<InstantBidEs> instantBidEsList = new ArrayList<>();
      instantBidEsIterable.forEach(instantBidEsList::add);

      Map<Long, ProductEs> productEs2Id = list.stream().collect(Collectors.toMap(ProductEs::getId, Function.identity()));
      Map<Long, InstantBidEs> instantBidEs2Id = instantBidEsList.stream().collect(Collectors.toMap(InstantBidEs::getId, Function.identity()));

      auctionParticipantEs.forEach(
          auctionParticipantEs1 -> {
            Long auctionId = auctionParticipantEs1.getAuctionId();
            ProductEs product = productEs2Id.get(auctionId);
            if (product != null) {
              Long instantBidId = auctionParticipantEs1.getInstantBidId();
              InstantBidEs instantBidEs = instantBidEs2Id.get(instantBidId);
              long price = 0L;
              int lastMinuteBidCount = 0;
              InstantBidType typeBid = InstantBidType.MANUAL;
              if (instantBidEs != null) {
                price = instantBidEs.getPrice();
                lastMinuteBidCount = instantBidEs.getLastMinuteBidCount();
                typeBid = InstantBidType.valueOf(instantBidEs.getType());
              }

              Long phaseId = auctionParticipantEs1.getPhaseId();
              Optional<AuctionRessultFlashBid> auctionResultFlashBids1 =
                  auctionResultFlashBids.stream()
                      .filter(
                          auctionResultFlashBid ->
                              auctionResultFlashBid
                                  .getId()
                                  .getPhaseId()
                                  .equals(phaseId))
                      .findFirst();
              AuctionRessultFlashBid auctionRessultFlashBid = auctionResultFlashBids1.orElse(null);

              List<AuctionPaymentRefuse> auctionPaymentRefuse =
                      auctionPaymentRefuseList.stream()
                              .filter(
                                      paymentRefuse ->
                                              paymentRefuse
                                                      .getId()
                                                      .getPhaseId()
                                                      .equals(phaseId))
                              .collect(Collectors.toList());
              boolean paymentRefuse = product.getAuction().getRefusePayment();
              if (product.getType().equals(ProductType.AUCTION_FLASH_BID.toString())) {
                paymentRefuse = auctionPaymentRefuse.size() > 0;
              }

              List<OrderGuardFlashBid> orderGuardFlashBids =
                      orderGuardFlashBidList.stream()
                              .filter(
                                      orderGuardFlashBid ->
                                              orderGuardFlashBid
                                                      .getPhaseId()
                                                      .equals(phaseId))
                              .collect(Collectors.toList());
              int remainQuantity = orderGuardFlashBids.size() > 0 ? 0 : product.getRemainingQuantity();

              Optional<AutoBid> autoBidOptional = autoBidList.stream().filter(autoBid -> autoBid.getPhaseId().equals(phaseId)).findFirst();
              AutoBid autoBid = autoBidOptional.orElse(null);

              AuctionEs auctionResponse = new AuctionEs();
              auctionResponse.sync(
                  product.getAuction(),
                  auctionParticipantEs1.getPhaseId(),
                  typeBid,
                  price,
                  lastMinuteBidCount,
                  auctionRessultFlashBid,
                  paymentRefuse);
              if (autoBid != null) {
                auctionResponse.setPriceAutoBid(autoBid.getCeilingPrice());
              }

              ProductEs productResponse = new ProductEs();
              productResponse.sync(product, auctionResponse, remainQuantity);
              if (productResponse.getType().equals(String.valueOf(ProductType.AUCTION_FLASH_BID))) {
                Optional<AuctionPhase> phaseOptional = auctionPhaseRepository.findById(phaseId);
                if (phaseOptional.isPresent()) {
                  AuctionPhase auctionPhase = phaseOptional.get();
                  productResponse
                          .getAuction()
                          .setTimeEnd(auctionPhase.getEndTime().atZone(ZoneId.of("GMT")).toEpochSecond() * 1000);
                  productResponse
                          .getAuction()
                          .setTimeStart(auctionPhase.getStartTime().atZone(ZoneId.of("GMT")).toEpochSecond() * 1000);
                  try {
                    if (auctionPhase.getWinnerId() != null) {
                      productResponse.getAuction().getResult().setWinnerId(auctionPhase.getWinnerId());
                      productResponse.getAuction().setState("STOPPED");
                    }
                  } catch (Exception e) {
                    Sentry.capture(e);
                  }

                  try {
                    if (auctionRessultFlashBid != null)
                      productResponse.getAuction().getResult().setCurrentPrice(auctionRessultFlashBid.getCurrentPrice());
                  } catch (Exception e) {
                    Sentry.capture(e);
                  }
                }
              }

              productEsList.add(productResponse);
            }
          });
    }

    Page<ProductEs> data = new PageImpl<>(productEsList, pageable, totalElements);
    return data;
  }
}
