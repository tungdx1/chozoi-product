package com.chozoi.product.domain.entities.abstracts;

import chozoi.products.domain_event.Value;
import com.chozoi.product.domain.entities.postgres.EventContent;
import com.chozoi.product.domain.utils.JsonParser;
import lombok.Data;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Data
public abstract class DomainEventAbs {
  public UUID id;
  protected Short version;
  protected String aggregate;
  protected EventContent content;
  protected LocalDateTime createdAt;

  public Value toValue() throws IOException {
    Value value = new Value();
    value.setId(this.getId().toString());
    value.setVersion((int) this.getVersion());
    value.setAggregate(this.getAggregate());
    value.setContent(JsonParser.toJson(this.getContent()));
    value.setCreatedAt(createdAt.toInstant(ZoneOffset.UTC).toEpochMilli());
    return value;
  }
}
