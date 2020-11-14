package com.chozoi.product.domain.schedules.notification.handle;

import com.chozoi.product.domain.entities.elasticsearch.AuctionParticipantEs;
import com.chozoi.product.domain.entities.postgres.AuctionOnly;
import com.chozoi.product.domain.entities.postgres.NotificationSegment;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
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
public class SendToWinner extends HandleGeneral implements TypeHandle {
  private SegmentState segmentState = SegmentState.WINER;

  @Override
  public void handle(List<NotificationSegment> segments) throws Exception {
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = start.plusSeconds(10);
    // TODO : xu li truong hop so luong products qua lon
    List<AuctionOnly> auctions = auctionRepository.findByTimeEndBetween(start, end);
    //    List<AuctionOnly> auctions = Arrays.asList(auctionRepository.findById(2840L).get());

    List<Long> ids = auctions.stream().map(AuctionOnly::getId).collect(Collectors.toList());
    assert !ids.isEmpty();
    products = productRepository.findAllById(ids);
    for (Long id : ids) {
      super.checkHandleExist(id, segmentState);
      send(id);
    }
    ;
  }

  private void send(Long id) throws Exception {
    Product product = ProductStaticService.productInList(id, products);
    PageRequest pageRequest = PageRequest.of(0, 1);
    Page<AuctionParticipantEs> auctionParticipantEs =
        auctionParticipantEsRepository.findByAuctionIdOrderByUpdatedAtDesc(id, pageRequest);
    assert !auctionParticipantEs.getContent().isEmpty();
    AuctionParticipantEs participantEs = auctionParticipantEs.getContent().get(0);
    // nap factory
    mailFactory = messageFactoryProducer.getFactory(MessageFactoryProducer.Type.MAIL);
    notificationFactory =
        messageFactoryProducer.getFactory(MessageFactoryProducer.Type.NOTIFICATION);
    // send to winner
    String userId = String.valueOf(participantEs.getUserId());
    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);
    assert notificationFactory != null;
    notificationFactory
        .getEvent("BUYER")
        .send(product, segmentState, SegmentObjectType.AUCTION, map);
    assert mailFactory != null;
    mailFactory.getEvent("BUYER").send(product, segmentState, segmentType, map);
  }
}
