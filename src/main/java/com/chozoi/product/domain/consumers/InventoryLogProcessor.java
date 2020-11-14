package com.chozoi.product.domain.consumers;

import chozoi.sales.inventory_success.Value;
import com.chozoi.product.domain.constants.ConfigRetry;
import com.chozoi.product.domain.entities.postgres.InventoryHistory;
import com.chozoi.product.domain.entities.postgres.InventoryKeep;
import com.chozoi.product.domain.entities.postgres.InventorySuccess;
import com.chozoi.product.domain.entities.postgres.ProductVariant;
import com.chozoi.product.domain.entities.postgres.types.InventoryHistoryState;
import com.chozoi.product.domain.repositories.postgres.DomainEventRepository;
import com.chozoi.product.domain.repositories.postgres.InventoryHistoryRepository;
import com.chozoi.product.domain.repositories.postgres.InventoryKeepRepository;
import com.chozoi.product.domain.repositories.postgres.InventorySuccessRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Log4j2
public class InventoryLogProcessor {

  @Autowired private InventoryKeepRepository inventoryKeepRepository;

  @Autowired private DomainEventRepository domainEventRepository;

  @Autowired private InventorySuccessRepository inventorySuccessRepository;
  @Autowired private InventoryHistoryRepository inventoryHistoryRepository;

  @KafkaListener(topics = "chozoi.sales.inventory_success", groupId = "inventory_success")
  @Retryable(
      value = {Exception.class},
      maxAttempts = ConfigRetry.MAX_ATTEMPTS,
      backoff = @Backoff(delay = ConfigRetry.DELAY_RETRY))
  @Transactional(
      propagation = Propagation.REQUIRED,
      rollbackFor = Exception.class,
      isolation = Isolation.SERIALIZABLE)
  public void process(@Payload(required = false) Value value) throws Exception {
    if (Objects.nonNull(value)) update(value);
  }

  @Transactional(
      propagation = Propagation.MANDATORY,
      rollbackFor = Exception.class,
      isolation = Isolation.SERIALIZABLE)
  public void update(Value value) {
    List<Integer> keepId = value.getKeepIds();
    Long orderSuccesId = value.getId();
    List<Long> keepIds = new ArrayList<>();
    keepId.forEach(
        i -> {
          keepIds.add(i.longValue());
        });
    List<InventoryKeep> keeps = inventoryKeepRepository.findAllById(keepIds);

    // update log

    //    saveHistory(keeps);
    // delete order_succes
    InventorySuccess inventorySuccess =
        inventorySuccessRepository.findById(orderSuccesId).orElse(new InventorySuccess());
    if (inventorySuccess.getId() != null) {
      inventorySuccessRepository.delete(inventorySuccess);
      inventoryKeepRepository.deleteAll(keeps);
    }
  }

  @Transactional(
      propagation = Propagation.MANDATORY,
      rollbackFor = Exception.class,
      isolation = Isolation.SERIALIZABLE)
  public List<InventoryHistory> saveHistory(List<InventoryKeep> keeps) {
    List<InventoryHistory> inventoryHistories = new ArrayList<>();
    log.error(keeps);
    keeps.forEach(
        keep -> {
          InventoryHistory inventoryHistory = new InventoryHistory();
          inventoryHistory.setQuantity(keep.getQuantity());
          inventoryHistory.setType(InventoryHistoryState.ORDERED);
          ProductVariant variant = new ProductVariant();
          variant.setId(keep.getVariantId());
          inventoryHistory.setVariant(variant);
          inventoryHistories.add(inventoryHistory);
        });
    //
    return inventoryHistoryRepository.saveAll(inventoryHistories);
  }
}
