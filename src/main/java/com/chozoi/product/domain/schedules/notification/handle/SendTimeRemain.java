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
public class SendTimeRemain extends HandleGeneral implements TypeHandle {
  private SegmentState segmentState = SegmentState.TIME_REMAIN;

  @Override
  public void handle(List<NotificationSegment> segments) throws Exception {

    segments =
        segments.stream()
            .filter(segment -> segment.getObjectValue().equals(String.valueOf(segmentState)))
            .collect(Collectors.toList());
    List<Integer> times =
        segments.stream().map(NotificationSegment::getTimeRemain).collect(Collectors.toList());

    assert !times.isEmpty();
    // nap factory
    mailFactory = messageFactoryProducer.getFactory(MessageFactoryProducer.Type.MAIL);
    notificationFactory =
        messageFactoryProducer.getFactory(MessageFactoryProducer.Type.NOTIFICATION);
    for (int minute : times) {
      LocalDateTime start = LocalDateTime.now().plusMinutes(minute);
      LocalDateTime end = start.plusSeconds(10);
      List<AuctionOnly> auctions = auctionRepository.findByTimeEndBetween(start, end);
      List<Long> ids = auctions.stream().map(AuctionOnly::getId).collect(Collectors.toList());
      products = productRepository.findAllById(ids);
      for (Long id : ids) {
        super.checkHandleExist(id, segmentState, minute);
        send(id, minute, 0);
      }
    }
  }

  private void send(Long id, int timeRemain, int page) throws Exception {
    PageRequest pageable = PageRequest.of(page, 20);
    Product product = ProductStaticService.productInList(id, products);
    Page<AuctionParticipantEs> auctionParticipantEs =
        auctionParticipantEsRepository.findByAuctionIdOrderByCreatedAtDesc(id, pageable);
    List<AuctionParticipantEs> participants = auctionParticipantEs.getContent();
    // nap factory
    mailFactory = messageFactoryProducer.getFactory(MessageFactoryProducer.Type.MAIL);
    notificationFactory =
        messageFactoryProducer.getFactory(MessageFactoryProducer.Type.NOTIFICATION);
    // send all user
    for (AuctionParticipantEs participant : participants) {
      String userId = String.valueOf(participant.getUserId());
      Map<String, Object> map = new HashMap<>();
      map.put("userId", userId);
      map.put("timeRemain", timeRemain);
      // send to buyer
      log.info("send time remain " + id + "|" + userId);
      assert notificationFactory != null;
      notificationFactory.getEvent("BUYER").send(product, segmentState, segmentType, map);
      assert mailFactory != null;
      mailFactory.getEvent("BUYER").send(product, segmentState, segmentType, map);
    }
    if (participants.size() == 20) {
      page += 1;
      send(id, timeRemain, page);
    }
  }
}
