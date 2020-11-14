package com.chozoi.product.domain.services.version_2.services;

import com.chozoi.product.domain.entities.postgres.Auction;
import com.chozoi.product.domain.entities.postgres.AuctionOnly;
import com.chozoi.product.domain.entities.postgres.AuctionPhase;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionType;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.repositories.postgres.AuctionOnlyRepository;
import com.chozoi.product.domain.repositories.postgres.AuctionPhaseRepository;
import com.chozoi.product.domain.repositories.postgres.AuctionRepository;
import com.chozoi.product.domain.utils.ProductUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuctionService {
  @Autowired private AuctionRepository auctionRepository;
  @Autowired private AuctionOnlyRepository auctionOnlyRepository;
  @Autowired private AuctionPhaseRepository auctionPhaseRepository;

  public void handleForAcceptAuction(Product productUpdate, Product product, ProductState isState) {
    List<Auction> auctions = new ArrayList<>();
    if (ProductUtils.AUCTION_TYPE.contains(product.getType())) {
      if (isState == ProductState.PUBLIC) {
        assert productUpdate.getAuction() != null;
        if (product.getAuction().getState() == ProductAuctionState.WAITING) {
          Auction auction = product.getAuction();
          auction.setState(ProductAuctionState.BIDING);
          ZoneId zid = ZoneId.of("GMT");
          LocalDateTime now = LocalDateTime.now(zid);
          auction.setTimeStart(now);
          auction.setCreatedAt(now);
          auction.setTimeEnd(now.plusHours(auction.getTimeDuration()));
          productUpdate.setAuction(auction);
          auctions.add(auction);
          setProductAndSavePhase(productUpdate);
        }
      }
      LocalDateTime now = LocalDateTime.now();
      product.getAuction().setCreatedAt(now);
      productUpdate.getAuction().setCreatedAt(now);
      productUpdate.getAuction().setUpdatedAt(now);
    }
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public void saveOnly(Product product, Auction auction) {
    AuctionOnly auctionOnly = new AuctionOnly();
    auction.setProduct(null);
    auctionOnly.setId(product.getId());
    auctionOnly.setPriceStep(auction.getPriceStep());
    auctionOnly.setRefusePayment(false);
    auctionOnly.setOriginalPrice(auction.getOriginalPrice());
    auctionOnly.setTimeDuration(auction.getTimeDuration());
    auctionOnly.setBuyNowPrice(auction.getBuyNowPrice());
    auctionOnly.setCreatedAt(LocalDateTime.now());
    auctionOnly.setUpdatedAt(LocalDateTime.now());
    auctionOnly.setExpectedPrice(auction.getExpectedPrice());
    auctionOnly.setExpectedMaxPrice(auction.getExpectedMaxPrice());
    auctionOnly.setStartPrice(auction.getStartPrice());
    auctionOnly.setState(auction.getState());
    auctionOnly.setTimeEnd(auction.getTimeEnd());
    auctionOnly.setTimeStart(auction.getTimeStart());
    auctionOnly.setPhaseId(auction.getPhaseId());
    if (product.getType().equals(ProductType.AUCTION_FLASH_BID)) auctionOnly.setType(ProductAuctionType.FLASH_BID);
    else
      auctionOnly.setType(ProductAuctionType.NORMAL);
    auctionOnlyRepository.save(auctionOnly);
    auction.setId(product.getId());
    product.setAuction(auction);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  private void setProductAndSavePhase(Product productUpdate) {
    // set product
    if (productUpdate.getAuction() != null) {
      Product product1 = new Product();
      product1.setId(productUpdate.getId());
      productUpdate.getAuction().setProduct(product1);
      savePhase(productUpdate);
    }
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  private void savePhase(Product productUpdate) {
    Auction auction = productUpdate.getAuction();
    int countPhase = productUpdate.getVariants().get(0).getInventory().getInQuantity();
    AuctionPhase auctionPhase =
        AuctionPhase.builder()
            .auctionId(productUpdate.getId())
            .countPhase(countPhase)
            .thisPhase(1)
            .startTime(auction.getTimeStart())
            .endTime(auction.getTimeEnd())
            .createdAt(LocalDateTime.now(ZoneId.of("GMT")))
            .updatedAt(LocalDateTime.now(ZoneId.of("GMT")))
            .build();
    auctionPhaseRepository.save(auctionPhase);
    productUpdate.getAuction().setPhaseId(auctionPhase.getId());
  }

  public boolean changeState(Auction auction, ProductAuctionState state) throws Exception {
    if (state.equals(ProductAuctionState.STOPPED)) {
      if (!auction.getState().equals(ProductAuctionState.BIDING)) throw new Exception("Phiên đấu chưa bắt đầu");
      auction.setState(ProductAuctionState.STOPPED);
    } else if (state.equals(ProductAuctionState.BIDING)) if (auction.getState().equals(ProductAuctionType.NORMAL)) {
    }
    ;
    auctionRepository.save(auction);
    return true;
  }
}
