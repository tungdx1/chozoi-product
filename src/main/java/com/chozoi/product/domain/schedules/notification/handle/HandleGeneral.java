package com.chozoi.product.domain.schedules.notification.handle;

import com.chozoi.product.domain.entities.mongodb.NotificationHandle;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.factories.notification.AbstractFactory;
import com.chozoi.product.domain.factories.notification.MessageFactoryProducer;
import com.chozoi.product.domain.repositories.elasticsearch.AuctionParticipantEsRepository;
import com.chozoi.product.domain.repositories.mongodb.NotificationHandleRepository;
import com.chozoi.product.domain.repositories.postgres.AuctionOnlyRepository;
import com.chozoi.product.domain.repositories.postgres.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class HandleGeneral {
  public List<Product> products;
  public SegmentObjectType segmentType = SegmentObjectType.AUCTION;
  @Autowired protected AuctionOnlyRepository auctionRepository;
  @Autowired protected ProductRepository productRepository;
  @Autowired protected AuctionParticipantEsRepository auctionParticipantEsRepository;
  @Autowired protected NotificationHandleRepository notificationHandleRepository;
  protected AbstractFactory mailFactory;
  protected AbstractFactory notificationFactory;
  protected SegmentState segmentState;
  @Autowired protected MessageFactoryProducer messageFactoryProducer;
  @Autowired protected RedisTemplate redisTemplate;

  protected void checkHandleExist(Long id, SegmentState state) throws Exception {

    String key = "CZ_HANDLE_" + segmentType + "_" + state + "_" + id;
    if (redisTemplate.hasKey(key)) throw new Exception("Đã được xử lý : " + key);
    else
        redisTemplate.opsForValue().set(key, true, TimeUnit.MINUTES.toMinutes(1));
  };

  protected void checkHandleExist(Long id, SegmentState state, Integer mimute) throws Exception {

    String key = "CZ_HANDLE_" + segmentType + "_" + state + "_" + id + "_" + mimute;
    if (redisTemplate.hasKey(key)) throw new Exception("Đã được xử lý : " + key);
    else {
      redisTemplate.opsForValue().set(key, true, TimeUnit.MINUTES.toMinutes(1));
      notificationHandleRepository.save(NotificationHandle.init(key));
    }
  };
}
