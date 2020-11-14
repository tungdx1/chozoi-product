package com.chozoi.product.domain.producers.factories;

import com.chozoi.product.domain.producers.SuggestionEventProducer;
import com.chozoi.product.domain.producers.content.SuggestionEventContent;
import com.chozoi.product.domain.producers.types.EventTypeSuggestion;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class SuggestionFactory {
  @Autowired
  private SuggestionEventProducer producer;

  public void viewFactory(String userId, Long productId) throws IOException {
    SuggestionEventContent eventContent = SuggestionEventContent.builder()
            .userId(userId)
            .productId(String.valueOf(productId))
            .build();
    producer.sendMassage(eventContent, EventTypeSuggestion.VIEW);
  }

  public void likeFactory(String userId, Long productId) throws IOException {
    SuggestionEventContent eventContent = SuggestionEventContent.builder()
            .userId(userId)
            .productId(String.valueOf(productId))
            .build();
    producer.sendMassage(eventContent, EventTypeSuggestion.LIKE);
  }

  public void unLikeFactory(String userId, Long productId) throws IOException {
    SuggestionEventContent eventContent = SuggestionEventContent.builder()
            .userId(userId)
            .productId(String.valueOf(productId))
            .build();
    producer.sendMassage(eventContent, EventTypeSuggestion.UNLIKE);
  }

}
