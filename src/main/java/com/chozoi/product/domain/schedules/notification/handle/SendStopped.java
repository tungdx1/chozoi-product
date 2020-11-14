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

@Log4j2
@Component
public class SendStopped extends HandleGeneral implements TypeHandle {
  private SegmentState segmentState = SegmentState.STOPPED;

  @Override
  public void handle(List<NotificationSegment> segments) throws Exception {
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = start.plusSeconds(10);
    // TODO : xu li truong hop so luong qua lon
    List<AuctionOnly> auctions = auctionRepository.findByTimeEndBetween(start, end);
    //    List<AuctionOnly> auctions = Arrays.asList(auctionRepository.findById(2840L).get());
    List<Long> ids = auctions.stream().map(AuctionOnly::getId).collect(Collectors.toList());
    assert !ids.isEmpty();
    products = productRepository.findAllById(ids);
    // TODO: check redis
    for (Long id : ids) {
      super.checkHandleExist(id, segmentState);
      send(id, 0);
    }
  }

  private void send(Long id, int page) throws Exception {
    PageRequest pageable = PageRequest.of(page, 20);
    Product product = ProductStaticService.productInList(id, products);
    Page<AuctionParticipantEs> auctionParticipantEs =
        auctionParticipantEsRepository.findByAuctionIdOrderByUpdatedAtDesc(id, pageable);
    List<AuctionParticipantEs> participants = auctionParticipantEs.getContent();
    // nap factory
    mailFactory = messageFactoryProducer.getFactory(MessageFactoryProducer.Type.MAIL);
    notificationFactory =
        messageFactoryProducer.getFactory(MessageFactoryProducer.Type.NOTIFICATION);
    // send all user
    for (int i = 0; i < participants.size(); i++) {
      AuctionParticipantEs participant = participants.get(i);
      if (i == 0 && page == 0) continue;
      String userId = String.valueOf(participant.getUserId());

      Map<String, Object> map = new HashMap<>();
      map.put("userId", userId);
      // send to buyer
      sendToBuyer(product, map);
      // send to seller
      sendToSeller(product, map);
      i += 1;
    }
    if (participants.size() == 20) {
      page += 1;
      send(id, page);
    }
  }

  private void sendToSeller(Product product, Map<String, Object> map) throws Exception {
    assert notificationFactory != null;
    notificationFactory.getEvent("SELLER").send(product, SegmentState.STOPPED, segmentType, map);
    assert mailFactory != null;
    mailFactory.getEvent("SELLER").send(product, SegmentState.STOPPED, segmentType, map);
  }

  private void sendToBuyer(Product product, Map<String, Object> map) throws Exception {
    assert notificationFactory != null;
    notificationFactory.getEvent("BUYER").send(product, segmentState, segmentType, map);
    assert mailFactory != null;
    mailFactory.getEvent("BUYER").send(product, segmentState, segmentType, map);
  }
}
