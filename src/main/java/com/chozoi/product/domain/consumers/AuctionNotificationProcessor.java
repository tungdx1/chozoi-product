package com.chozoi.product.domain.consumers;

import com.chozoi.auction.avro.event.Value;
import com.chozoi.product.domain.constants.ConfigRetry;
import com.chozoi.product.domain.consumers.content.UserJoinedEventContent;
import com.chozoi.product.domain.consumers.content.WinnerChangedEventContent;
import com.chozoi.product.domain.entities.elasticsearch.AuctionEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.postgres.Auction;
import com.chozoi.product.domain.entities.postgres.AuctionResult;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.producers.factories.MailFactory;
import com.chozoi.product.domain.producers.factories.NotificationFactory;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import com.chozoi.product.domain.utils.MessagePack;
import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Component
@Log4j2
public class AuctionNotificationProcessor extends BaseProcessor {
  @Autowired private ProductEsRepository productRepository;
  @Autowired private NotificationFactory notificationFactory;
  @Autowired private MailFactory mailFactory;

  @Override
  @Recover
  public void connectionException(Exception e) {

    log.error("==========================Retry done===========================: " + e.getMessage());
    Sentry.capture("Retry: " + e.getMessage());
  }

  @KafkaListener(topics = "chozoi.bidding.events", groupId = "chozoi_notification")
  @Retryable(
      value = {Exception.class, ListenerExecutionFailedException.class},
      maxAttempts = ConfigRetry.MAX_ATTEMPTS,
      backoff = @Backoff(delay = ConfigRetry.DELAY_RETRY))
  public void process(@Payload(required = false) Value value) throws Exception {
    if (value.getType().equals("WinnerChangedEvent")) handlerWinnerChanged(value.getContent());
    //    else if (value.getType().equals("UserJoinedEvent")) handlerUserJoined(value.getContent());
  }

  private void handlerUserJoined(ByteBuffer content) throws Exception {
    byte[] arr = content.array();
    UserJoinedEventContent eventContent =
        MessagePack.byteaToObject(arr, UserJoinedEventContent.class);
    log.info("================" + eventContent.getUserId());
    try {
      Thread.sleep(2000);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
    ProductEs productEs =
        productRepository
            .findById(eventContent.getAuctionId())
            .orElseThrow(
                () -> new Exception("Product id : " + eventContent.getAuctionId() + " not found"));
    Product product = mappingProduct(productEs);
    product
        .getAuction()
        .getResult()
        .setCurrentPrice(eventContent.getPrice() - product.getAuction().getPriceStep());
    product.getAuction().getResult().setBidPrice(eventContent.getPrice());
    mailFactory.sendEventBuyer(
        product, String.valueOf(eventContent.getUserId()), SegmentState.BIDING);
    notificationFactory.sendEventBuyer(
        product, String.valueOf(eventContent.getUserId()), SegmentState.BIDING);
  }

  private void handlerWinnerChanged(ByteBuffer content) throws Exception {
    byte[] arr = content.array();
    WinnerChangedEventContent eventContent =
        MessagePack.byteaToObject(arr, WinnerChangedEventContent.class);
    ProductEs productEs =
        productRepository
            .findById(eventContent.getAuctionId())
            .orElseThrow(
                () -> new Exception("Product id : " + eventContent.getAuctionId() + " not found"));
    Product product = mappingProduct(productEs);
    product.getAuction().getResult().setCurrentPrice(eventContent.getNewWinner().getPrice());
    mailFactory.sendEventBuyer(
        product, String.valueOf(eventContent.getOldWinner().getUserId()), SegmentState.OUTBID);
    notificationFactory.sendEventBuyer(
        product, String.valueOf(eventContent.getOldWinner().getUserId()), SegmentState.OUTBID);
  }

  private Product mappingProduct(ProductEs productEs) {
    Product product = new Product();
    product.setId(productEs.getId());
    product.setName(productEs.getName());
    product.setType(ProductType.valueOf(productEs.getType()));
    AuctionEs auctionEs = productEs.getAuction();
    Auction auction = new Auction();
    auction.setState(ProductAuctionState.valueOf(productEs.getAuction().getState()));
    auction.setStartPrice(productEs.getAuction().getStartPrice());
    auction.setPriceStep(productEs.getAuction().getPriceStep());
    LocalDateTime timeEnd =
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli((long) auctionEs.getTimeEnd()), TimeZone.getDefault().toZoneId());
    LocalDateTime timeStart =
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli((long) auctionEs.getTimeStart()),
            TimeZone.getDefault().toZoneId());
    auction.setTimeEnd(timeEnd);
    auction.setTimeStart(timeStart);
    AuctionResult result =
        AuctionResult.builder()
            .currentPrice(auctionEs.getResult().getCurrentPrice())
            .bidsCount(auctionEs.getResult().getBidsCount())
            .build();
    auction.setResult(result);
    product.setAuction(auction);
    return product;
  }
}
