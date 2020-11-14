package com.chozoi.product.domain.entities.postgres;

import chozoi.products.domain_event.Key;
import chozoi.products.domain_event.Value;
import com.chozoi.product.domain.entities.abstracts.DomainEventAbs;
import com.chozoi.product.domain.entities.postgres.types.EventType;
import lombok.Data;

import java.io.IOException;

@Data
public class DomainEvent extends DomainEventAbs {
  public EventType type;

  public Value toValueAvro() throws IOException {
    Value value = toValue();
    value.setType(this.getType().toString());
    return value;
  }

  public Key toKeyAvro() {
    return new Key(this.getId().toString());
  }

}
