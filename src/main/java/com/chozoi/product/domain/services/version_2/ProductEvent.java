package com.chozoi.product.domain.services.version_2;

import com.chozoi.product.domain.entities.postgres.DomainLogEvent;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductVariant;
import com.chozoi.product.domain.entities.postgres.types.EventType;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.factories.DomainEventFactory;
import com.chozoi.product.domain.producers.factories.MailFactory;
import com.chozoi.product.domain.producers.factories.NotificationFactory;
import com.chozoi.product.domain.repositories.postgres.DomainEventRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
public class ProductEvent {
  @Autowired private DomainEventRepository domainEventRepository;
  @Autowired private DomainEventFactory domainEventFactory;
  @Autowired private NotificationFactory notificationFactory;
  @Autowired private MailFactory mailFactory;

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public void productCreatedEvent(Product product, Integer userId) throws IOException {
    // save log
    DomainLogEvent domainLogEvent =
        domainEventFactory.createProductLog(product, EventType.ProductCreated, userId);
    domainEventRepository.save(domainLogEvent);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public void productUpdatedEvent(Product product, Integer userId) throws Exception {
    // save log
    DomainLogEvent domainLogEvent =
        domainEventFactory.createProductLog(product, EventType.ProductUpdated, userId);

    domainEventRepository.save(domainLogEvent);
  }

  /**
   * event change state for product
   *
   * @param product
   * @param userSystemId
   * @param userId
   * @throws IOException
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void productStateChangedEvent(
      Product preProduct, Product product, Integer userSystemId, Integer userId, String reason)
      throws Exception {
    log.error("================== " + preProduct.getState() + "=========> " + product.getState());
    // white log
    DomainLogEvent domainLogEvent =
        domainEventFactory.changeStateLog(
            product, preProduct.getState(), product.getState(), userSystemId, userId);
    domainEventRepository.save(domainLogEvent);

    // send notification
    if ((preProduct.getState().equals(ProductState.PENDING)
            && (product.getState().equals(ProductState.PUBLIC)
                || product.getState().equals(ProductState.READY)))
        || product.getState().equals(ProductState.REJECT)
        || product.getState().equals(ProductState.REJECTPRODUCT)
        || product.getState().equals(ProductState.REPORT)) {
      notificationFactory.sendEvent(
          product, String.valueOf(product.getState()), SegmentObjectType.PRODUCT, reason);
      mailFactory.sendEvent(
          product, String.valueOf(product.getState()), SegmentObjectType.PRODUCT, reason);
    }
  }

  /**
   * handle price for log
   *
   * @param productOld
   * @param product
   * @param userId
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  void eventChangePrice(Product productOld, Product product, Integer userId) {
    List<ProductVariant> newVariants = product.getVariants();
    List<ProductVariant> oldVariants = productOld.getVariants();
    Map<Long, Long> mapSalePrice = new HashMap<>();
    Map<Long, Long> mapPrice = new HashMap<>();
    oldVariants.forEach(
        v -> {
          mapSalePrice.put(v.getId(), v.getSalePrice());
          mapPrice.put(v.getId(), v.getPrice());
        });
    newVariants.forEach(
        v -> {
          if (!Objects.isNull(mapSalePrice.get(v.getId()))) if (!mapSalePrice.get(v.getId()).equals(v.getSalePrice())
                  || !mapPrice.get(v.getId()).equals(v.getPrice())) {
              long preSalePrice = mapSalePrice.get(v.getId());
              long prePrice = mapPrice.get(v.getId());
              long price = Objects.isNull(v.getPrice()) ? 0 : v.getPrice();
              long salePrice = v.getSalePrice();
              priceChangedEvent(prePrice, price, preSalePrice, salePrice, product, userId);
          }
        });
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  protected void priceChangedEvent(
      Long prePrice,
      Long price,
      Long preSalePrice,
      Long salePrice,
      Product product,
      Integer userId) {
    DomainLogEvent domainLogEvent =
        domainEventFactory.priceChangedLog(
            product, prePrice, price, preSalePrice, salePrice, userId);
    domainEventRepository.save(domainLogEvent);
  }
}
