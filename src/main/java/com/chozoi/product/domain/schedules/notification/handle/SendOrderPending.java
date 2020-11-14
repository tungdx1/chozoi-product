package com.chozoi.product.domain.schedules.notification.handle;

import com.chozoi.product.domain.entities.elasticsearch.AuctionParticipantEs;
import com.chozoi.product.domain.entities.postgres.AuctionOnly;
import com.chozoi.product.domain.entities.postgres.NotificationSegment;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.factories.notification.MessageFactoryProducer;
import com.chozoi.product.domain.services.static_service.ProductStaticService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Log4j2
public class SendOrderPending extends HandleGeneral implements TypeHandle {
  private static final SegmentState segmentState = SegmentState.ORDER_PENDING;

  @Override
  public void handle(List<NotificationSegment> segments) {
    segments =
        segments.stream()
            .filter(segment -> segment.getObjectValue().equals(String.valueOf(segmentState)))
            .collect(Collectors.toList());
    List<Integer> times =
        segments.stream().map(NotificationSegment::getTimePending).collect(Collectors.toList());
    assert !times.isEmpty();
    // nap factory
    mailFactory = messageFactoryProducer.getFactory(MessageFactoryProducer.Type.MAIL);
    notificationFactory =
        messageFactoryProducer.getFactory(MessageFactoryProducer.Type.NOTIFICATION);
    times.forEach(
        minute -> {
          LocalDateTime start = LocalDateTime.now().minusMinutes(minute);
          LocalDateTime end = start.plusSeconds(10);
          List<AuctionOnly> auctions = auctionRepository.findByTimeEndBetween(start, end);
          List<Long> ids = auctions.stream().map(AuctionOnly::getId).collect(Collectors.toList());
          products = productRepository.findAllById(ids);
          ids.forEach(
              id -> {
                try {
                  super.checkHandleExist(id, segmentState, minute);
                  send(id, minute);
                } catch (Exception exception) {
                  exception.printStackTrace();
                }
              });
        });
  }

  private void send(Long id, int pendingTime) throws Exception {
    PageRequest pageable = PageRequest.of(0, 1);
    Product product = ProductStaticService.productInList(id, products);
    Page<AuctionParticipantEs> auctionParticipantEs =
        auctionParticipantEsRepository.findByAuctionIdOrderByUpdatedAtDesc(id, pageable);
    List<AuctionParticipantEs> participants = auctionParticipantEs.getContent();
    assert !participants.isEmpty();
    AuctionParticipantEs participant = participants.get(0);
    // send all user
    String userId = String.valueOf(participant.getUserId());
    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);
    map.put("pendingTime", pendingTime);
    // send to buyer
    assert notificationFactory != null;
    notificationFactory.getEvent("BUYER").send(product, segmentState, segmentType, map);
    assert mailFactory != null;
    mailFactory.getEvent("BUYER").send(product, segmentState, segmentType, map);
  }
}
