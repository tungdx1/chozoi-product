package com.chozoi.product.domain.producers;

import chozoi.passport.evaluate.ShopFavorite;
import com.chozoi.emailservice.domain.avro.MailMessage;
import com.chozoi.product.domain.producers.content.EmailContent;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Log4j2
@Component
public class ProductFavoriteProducer {
  @Autowired
  private KafkaTemplate<String, ShopFavorite> productMessageKafkaTemplate;

  public void save(Integer shopId, Integer userId, Long productId, String type) {
    try {
      ShopFavorite shopFavorite = new ShopFavorite();
      shopFavorite.setShopId(shopId);
      shopFavorite.setUserId(userId);
      shopFavorite.setProductId(productId);
      shopFavorite.setType(type);
      this.productMessageKafkaTemplate.send(TopicConfig.TOPIC_PRODUCT_FAVORITE, shopFavorite);
    } catch (Exception e) {
      log.debug(e);
    }
  }
}
