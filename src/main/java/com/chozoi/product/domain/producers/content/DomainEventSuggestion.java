package com.chozoi.product.domain.producers.content;

import chozoi.products.domain_event.Key;
import chozoi.products.domain_event.Value;
import com.chozoi.product.domain.entities.abstracts.DomainEventAbs;
import com.chozoi.product.domain.producers.types.EventTypeSuggestion;
import com.chozoi.product.domain.utils.JsonParser;
import lombok.Data;

import java.io.IOException;
import java.time.ZoneOffset;

@Data
public class DomainEventSuggestion extends DomainEventAbs {


  private EventTypeSuggestion type;

  public Value toValueAvro() throws IOException {
    Value value = new Value();
    value.setId(getId().toString());
    value.setVersion((int) getVersion());
    value.setType(getType().toString());
    value.setAggregate(getAggregate());
    value.setContent(JsonParser.toJson(getContent()));
    value.setCreatedAt(createdAt.toInstant(ZoneOffset.UTC).toEpochMilli());
    return value;
  }

  public Key toKeyAvro() {
    return new Key(getId().toString());
  }
}

