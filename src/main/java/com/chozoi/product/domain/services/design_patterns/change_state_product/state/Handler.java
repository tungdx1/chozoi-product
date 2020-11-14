package com.chozoi.product.domain.services.design_patterns.change_state_product.state;

import com.chozoi.product.domain.entities.postgres.*;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import com.chozoi.product.domain.entities.postgres.types.ProductImageState;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.repositories.postgres.*;
import com.chozoi.product.domain.services.design_patterns.change_state_product.data.DataHandle;
import com.chozoi.product.domain.services.version_2.services.AuctionService;
import com.chozoi.product.domain.services.version_2.services.ImageService;
import com.chozoi.product.domain.utils.ProductUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class Handler {
  @Autowired protected ProductRepository productRepository;
  @Autowired protected ProductDraftRepository productDraftRepository;
  @Autowired protected ProductImageRepository productImageRepository;
  @Autowired protected ProductVariantRepository productVariantRepository;
  @Autowired protected AuctionPhaseRepository auctionPhaseRepository;
  @Autowired protected ImageService imageService;
  @Autowired protected AuctionService auctionService;
  protected List<ProductState> statesAcceptDelete =
      Arrays.asList(
          ProductState.DRAFT, ProductState.REJECT, ProductState.READY, ProductState.PENDING);

  /**
   * Filter product in list
   *
   * @param id
   * @param products
   * @return
   */
  protected static Product hanldeProduct(Long id, List<Product> products) {
    List<Product> productList =
        products.stream().filter(v -> v.getId().equals(id)).collect(Collectors.toList());
    return ObjectUtils.defaultIfNull(productList.get(0), new Product());
  }

  /**
   * Accept Auction
   *
   * @param productUpdate
   * @param product
   * @param isState
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
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
          if (productUpdate.getType().equals(ProductType.AUCTION_FLASH_BID))
            auction.setTimeEnd(now.plusMinutes(auction.getTimeDuration()));
          else
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
  protected void setProductAndSavePhase(Product productUpdate) {
    // set product
    if (productUpdate.getAuction() != null) {
      Product product1 = new Product();
      product1.setId(productUpdate.getId());
      productUpdate.getAuction().setProduct(product1);
      savePhase(productUpdate);
    }
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public void savePhase(Product productUpdate) {
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

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public void handleForStopFlashBid(Product productUpdate, Product product, ProductState isState) {
    List<Auction> auctions = new ArrayList<>();
    if (product.getType() == ProductType.AUCTION_FLASH_BID) {
      if (isState == ProductState.STOPPED) {
        assert productUpdate.getAuction() != null;
        if (product.getAuction().getState() == ProductAuctionState.BIDING) {
          Auction auction = product.getAuction();
          auction.setState(ProductAuctionState.STOPPED);
          ZoneId zid = ZoneId.of("GMT");
          LocalDateTime now = LocalDateTime.now(zid);
          auction.setUpdatedAt(now);
          auction.setTimeEnd(now);

          productUpdate.setAuction(auction);
          auctions.add(auction);
//          setProductAndSavePhase(productUpdate);
        }
      }
      LocalDateTime now = LocalDateTime.now();
      product.getAuction().setUpdatedAt(now);
      productUpdate.getAuction().setUpdatedAt(now);
    }
  }

  /**
   * check preState is state handler
   *
   * @param productDrafts
   * @param state
   * @throws Exception
   */
  public void checkState(List<ProductDraft> productDrafts, ProductState state) throws Exception {
    List<Long> errorIds = new ArrayList<>();
    productDrafts.forEach(
        productDraft -> {
          if (productDraft.getState() != state) errorIds.add(productDraft.getId());
        });
    if (errorIds.size() > 0) throw new Exception("status is invalid in :" + errorIds);
  }

  /**
   * Change state image to public
   *
   * @param productDraft
   * @param product
   */
  protected void acceptImage(ProductDraft productDraft, Product product, DataHandle data) {
    List<ProductImage> imagesOld = product.getImages();
    List<ProductImage> imagesNew = productDraft.getData().getImages();
    imagesNew.forEach(
        image -> {
          List<ProductImage> img =
              imagesOld.stream()
                  .filter(productImage -> productImage.getId().equals(image.getId()))
                  .collect(Collectors.toList());
          if (img.size() > 0) {
            imagesOld.remove(img.get(0));
            image.setState(ProductImageState.PUBLIC);
          }
        });
    imagesOld.forEach(ProductImage::setDelete);
    imagesNew.addAll(imagesOld);
    data.setImages(imagesNew);
  }

  /**
   * Save list product draft
   *
   * @param productsUpdate
   * @param variants
   */
  @Transactional(propagation = Propagation.MANDATORY)
  protected void saveProductsUpdate(
      List<ProductDraft> productsUpdate, List<ProductVariant> variants) {
    productsUpdate.forEach(
        productDraft -> {
          List<ProductVariant> variantList =
              variants.stream()
                  .filter(variant -> variant.getProduct().getId().equals(productDraft.getId()))
                  .collect(Collectors.toList());
          productDraft.getData().setVariants(variantList);
        });
    productDraftRepository.saveAll(productsUpdate);
  }

  /**
   * Start Auction product
   *
   * @param product
   */
  protected void updateAuction(Product product, DataHandle data) {
    Auction auction = product.getAuction();
    auction.setState(ProductAuctionState.BIDING);
    ZoneId zid = ZoneId.of("GMT");
    LocalDateTime now = LocalDateTime.now(zid);
    auction.setTimeStart(now);
    auction.setCreatedAt(now);
    auction.setTimeEnd(now.plusHours(auction.getTimeDuration()));
    product.setAuction(auction);
    List<Auction> auctions =
        Objects.isNull(data.getAuctions()) ? new ArrayList<>() : data.getAuctions();
    auctions.add(auction);
    data.setAuctions(auctions);
  }
}
