package com.chozoi.product.domain.schedules.notification;

import com.chozoi.product.domain.entities.elasticsearch.AuctionParticipantEs;
import com.chozoi.product.domain.entities.mongodb.AuctionNotification;
import com.chozoi.product.domain.entities.postgres.AuctionOnly;
import com.chozoi.product.domain.entities.postgres.NotificationSegment;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.producers.factories.MailFactory;
import com.chozoi.product.domain.producers.factories.NotificationFactory;
import com.chozoi.product.domain.repositories.elasticsearch.AuctionParticipantEsRepository;
import com.chozoi.product.domain.repositories.mongodb.AuctionActionLogRepository;
import com.chozoi.product.domain.repositories.postgres.AuctionOnlyRepository;
import com.chozoi.product.domain.repositories.postgres.NotificationSegmentRepository;
import com.chozoi.product.domain.repositories.postgres.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j2
public class Schedule {

  @Autowired private NotificationSegmentRepository notificationSegmentRepository;
  @Autowired private AuctionOnlyRepository auctionRepository;
  @Autowired private AuctionParticipantEsRepository auctionParticipantEsRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private NotificationFactory notificationFactory;
  @Autowired private MailFactory mailFactory;

  @Autowired private AuctionActionLogRepository auctionActionLogRepository;

  //  @Scheduled(cron = "0/10 * * * * *")
  public void sendNotification() throws Exception {
    List<String> states =
        Arrays.asList(
            String.valueOf(SegmentState.TIME_REMAIN), String.valueOf(SegmentState.ORDER_PENDING));
    List<NotificationSegment> segments =
        notificationSegmentRepository.findByObjectTypeAndObjectValueIn(
            SegmentObjectType.AUCTION, states);

    List<NotificationSegment> segmentTimeRemain =
        segments.stream()
            .filter(
                segment -> segment.getObjectType().equals(String.valueOf(SegmentState.TIME_REMAIN)))
            .collect(Collectors.toList());
    List<NotificationSegment> segmentOrderPending =
        segments.stream()
            .filter(
                segment ->
                    segment.getObjectType().equals(String.valueOf(SegmentState.ORDER_PENDING)))
            .collect(Collectors.toList());
    List<Integer> times =
        segmentTimeRemain.stream()
            .map(NotificationSegment::getTimeRemain)
            .collect(Collectors.toList());
    for (int minute : times) sendTimeRemain(minute);
    sendStopped();
    sendOrderPending(segmentOrderPending);
  }

  private void sendOrderPending(List<NotificationSegment> segmentOrderPending) {
    assert segmentOrderPending != null;
    if (segmentOrderPending.size() == 0) return;
    List<Integer> times =
        segmentOrderPending.stream()
            .map(NotificationSegment::getTimeRemain)
            .collect(Collectors.toList());
    times.forEach(
        minute -> {
          LocalDateTime start = LocalDateTime.now().minusMinutes(minute);
          LocalDateTime end = start.plusSeconds(10);
          List<AuctionOnly> auctions = auctionRepository.findByTimeEndBetween(start, end);
          List<Long> ids = auctions.stream().map(AuctionOnly::getId).collect(Collectors.toList());
          if (ids.size() > 0) {
            List<AuctionNotification> auctionNotifications =
                auctionActionLogRepository.findByAuctionIdInAndActionAndTimeRemain(
                    ids, "ORDER_PENDING", minute);
            auctionNotifications.forEach(
                auctionNotification -> {
                  List<AuctionOnly> auctionOnlyList =
                      auctions.stream()
                          .filter(
                              auctionOnly ->
                                  auctionOnly.getId().equals(auctionNotification.getAuctionId()))
                          .collect(Collectors.toList());
                  if (auctionOnlyList.size() > 0) auctions.removeAll(auctionOnlyList);
                });
          }
          if (ids.size() > 0) {
            List<AuctionNotification> auctionNotifications = new ArrayList<>();
            ids.forEach(
                id -> {
                  AuctionNotification auctionNotification =
                      AuctionNotification.builder()
                          .id(UUID.randomUUID())
                          .action(String.valueOf(SegmentState.ORDER_PENDING))
                          .auctionId(id)
                          .timeRemain(0)
                          .build();
                  auctionNotifications.add(auctionNotification);
                });
            auctionActionLogRepository.saveAll(auctionNotifications);
          }
        });
  }

  private void send(List<AuctionOnly> auctions, SegmentState state) {
    List<Long> ids = auctions.stream().map(AuctionOnly::getId).collect(Collectors.toList());

    List<Product> products = productRepository.findAllById(ids);
    ids.forEach(
        id -> {
          List<AuctionParticipantEs> auctionParticipantEs =
              auctionParticipantEsRepository.findByAuctionId(id);
          List<Product> productList =
              products.stream()
                  .filter(product1 -> product1.getId().equals(id))
                  .collect(Collectors.toList());

          if (productList.size() > 0) {
            Product product = productList.get(0);
            // gui cho ng thang
            if (state.equals(SegmentState.STOPPED)) try {
                sendWinner(auctionParticipantEs, product);
            } catch (Exception exception) {
            }
            auctionParticipantEs.forEach(
                auctionParticipant -> {
                  String userId = String.valueOf(auctionParticipant.getUserId());
                  try {
                    notificationFactory.sendEventBuyer(product, userId, state);
                    mailFactory.sendEventBuyer(product, userId, state);
                    // send seller

                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                });
            if (state.equals(SegmentState.STOPPED)) try {
                mailFactory.sendEvent(
                        product, String.valueOf(state), SegmentObjectType.AUCTION, null);
                notificationFactory.sendEvent(
                        product, String.valueOf(state), SegmentObjectType.AUCTION, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
          }
        });
  }

  private void sendWinner(List<AuctionParticipantEs> auctionParticipantEs, Product product)
      throws Exception {
    auctionParticipantEs.sort(Comparator.comparing(AuctionParticipantEs::getCreatedAt).reversed());
    AuctionParticipantEs auctionParticipantEs1 = auctionParticipantEs.get(0);
    Integer userId = auctionParticipantEs1.getUserId();
    notificationFactory.sendEventBuyer(product, String.valueOf(userId), SegmentState.WINER);
    mailFactory.sendEventBuyer(product, String.valueOf(userId), SegmentState.WINER);
    auctionParticipantEs.remove(auctionParticipantEs.get(0));
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void sendStopped() {
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = start.plusSeconds(10);
    List<AuctionOnly> auctions = auctionRepository.findByTimeEndBetween(start, end);
    List<Long> ids = auctions.stream().map(AuctionOnly::getId).collect(Collectors.toList());
    if (ids.size() > 0) {
      List<AuctionNotification> auctionNotifications =
          auctionActionLogRepository.findByAuctionIdInAndAction(ids, "STOPPED");
      auctionNotifications.forEach(
          auctionNotification -> {
            List<AuctionOnly> auctionOnlyList =
                auctions.stream()
                    .filter(
                        auctionOnly ->
                            auctionOnly.getId().equals(auctionNotification.getAuctionId()))
                    .collect(Collectors.toList());
            if (auctionOnlyList.size() > 0) auctions.removeAll(auctionOnlyList);
          });
    }
    send(auctions, SegmentState.STOPPED);
    if (ids.size() > 0) {
      List<AuctionNotification> auctionNotifications = new ArrayList<>();
      ids.forEach(
          id -> {
            AuctionNotification auctionNotification =
                AuctionNotification.builder()
                    .id(UUID.randomUUID())
                    .action(String.valueOf(SegmentState.STOPPED))
                    .auctionId(id)
                    .timeRemain(0)
                    .build();
            auctionNotifications.add(auctionNotification);
          });
      auctionActionLogRepository.saveAll(auctionNotifications);
    }
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void sendTimeRemain(int minute) {
    LocalDateTime start = LocalDateTime.now().plusMinutes(minute);
    LocalDateTime end = start.plusSeconds(10);
    List<AuctionOnly> auctions = auctionRepository.findByTimeEndBetween(start, end);
    List<Long> ids = auctions.stream().map(AuctionOnly::getId).collect(Collectors.toList());
    if (ids.size() > 0) {
      List<AuctionNotification> auctionNotifications =
          auctionActionLogRepository.findByAuctionIdInAndActionAndTimeRemain(
              ids, "TIME_REMAIN", minute);
      auctionNotifications.forEach(
          auctionNotification -> {
            List<AuctionOnly> auctionOnlyList =
                auctions.stream()
                    .filter(
                        auctionOnly ->
                            auctionOnly.getId().equals(auctionNotification.getAuctionId()))
                    .collect(Collectors.toList());
            if (auctionOnlyList.size() > 0) auctions.removeAll(auctionOnlyList);
          });
    }
    send(auctions, SegmentState.TIME_REMAIN);

    // save log

    if (ids.size() > 0) {
      List<AuctionNotification> auctionNotifications = new ArrayList<>();
      ids.forEach(
          id -> {
            AuctionNotification auctionNotification =
                AuctionNotification.builder()
                    .id(UUID.randomUUID())
                    .action(String.valueOf(SegmentState.TIME_REMAIN))
                    .auctionId(id)
                    .timeRemain(minute)
                    .build();
            auctionNotifications.add(auctionNotification);
          });
      auctionActionLogRepository.saveAll(auctionNotifications);
    }
  }
}
