package com.chozoi.product.domain.factories;

import com.chozoi.product.domain.constants.LogConstant;
import com.chozoi.product.domain.entities.postgres.DomainEvent;
import com.chozoi.product.domain.entities.postgres.DomainLogEvent;
import com.chozoi.product.domain.entities.postgres.EventContent;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.EventType;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.utils.MessagePack;
import com.chozoi.product.domain.values.InventoryEventContent;
import com.chozoi.product.domain.values.ProductEventContent;
import com.chozoi.product.domain.values.ProductViewEventContent;
import com.chozoi.product.domain.values.content.BaseContent;
import com.chozoi.product.domain.values.content.ChangePriceLog;
import com.chozoi.product.domain.values.content.ChangeStateLog;
import com.chozoi.product.domain.values.content.InventoryLog;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Log4j2
public class DomainEventFactory {

    public final DomainEvent inventoryCreated(InventoryEventContent content) {
        return createDomainEvent(content, EventType.InventoryCreated);
    }

    public final DomainEvent inventoryQuantityChanged(InventoryEventContent content) {
        return createDomainEvent(content, EventType.ProductQuantityChanged);
    }

    public final DomainEvent productCreated(ProductEventContent content) {
        return createDomainEvent(content, EventType.ProductCreated);
    }


    public DomainEvent productUpdated(ProductEventContent content) {
        return createDomainEvent(content, EventType.ProductUpdated);
    }

    public final DomainEvent productStateChanged(ProductEventContent content) {
        return createDomainEvent(content, EventType.ProductChangeStated);
    }

    public final DomainEvent productViewed(ProductViewEventContent content) {
        return createDomainEvent(content, EventType.ProductViewed);
    }

    private DomainEvent createDomainEvent(EventContent content, EventType type) {
        DomainEvent domainEvent = new DomainEvent();
        domainEvent.setId(UUID.randomUUID());
        domainEvent.setAggregate(LogConstant.PRODUCT_AGGREGATE);
        domainEvent.setVersion(LogConstant.PRODUCT_VERSION);
        domainEvent.setContent(content);
        domainEvent.setCreatedAt(LocalDateTime.now());
        domainEvent.setType(type);
        return domainEvent;
    }

    public DomainLogEvent createProductLog(Product content, EventType type, Integer userId) {
        BaseContent baseContent = BaseContent.builder()
                .data(content)
                .shopId(content.getShop().getId())
                .updatedBySystemId(userId)
                .updatedById(null)
                .productId(content.getId())
                .build();
        byte[] bytes = MessagePack.objectToBytea(baseContent);
        return domainLogEvent(bytes, type);
    }

    private DomainLogEvent domainLogEvent(byte[] bytes, EventType type) {
        DomainLogEvent domainEvent = new DomainLogEvent();
        domainEvent.setId(UUID.randomUUID());
        domainEvent.setAggregate(LogConstant.PRODUCT_AGGREGATE);
        domainEvent.setVersion(LogConstant.PRODUCT_VERSION);
        domainEvent.setContent(bytes);
        domainEvent.setCreatedAt(LocalDateTime.now());
        domainEvent.setType(type);
        return domainEvent;
    }

    public DomainLogEvent changeStateLog(Product product, ProductState preState, ProductState state, Integer userSystemId, Integer userId) {
        log.info("============" + preState + "===========>>>" + state);
        ChangeStateLog log = ChangeStateLog.builder().preState(preState).state(state).build();
        BaseContent content = BaseContent.builder()
                .productId(product.getId())
                .shopId(product.getShop().getId())
                .updatedById(userId)
                .updatedBySystemId(userSystemId)
                .data(log)
                .build();
        byte[] bytes = MessagePack.objectToBytea(content);
        return domainLogEvent(bytes, EventType.ProductChangeStated);
    }

    public DomainLogEvent priceChangedLog(Product product, Long prePrice, Long price, Long preSalePrice, Long salePrice, Integer userId) {
        ChangePriceLog log = ChangePriceLog.builder()
                .preSalePrice(preSalePrice)
                .salePrice(salePrice)
                .price(price)
                .prePrice(prePrice)
                .build();
        BaseContent content = BaseContent.builder()
                .productId(product.getId())
                .shopId(product.getShop().getId())
                .updatedById(userId)
                .updatedBySystemId(null)
                .data(log)
                .build();
        byte[] bytes = MessagePack.objectToBytea(content);
        return domainLogEvent(bytes, EventType.ProductPriceChanged);
    }

    public DomainLogEvent changeQuantityLog(Product product, Integer userId, InventoryLog log) {
        BaseContent content = BaseContent.builder()
                .productId(product.getId())
                .shopId(product.getShop().getId())
                .updatedById(userId)
                .updatedBySystemId(null)
                .data(log)
                .build();
        byte[] bytes = MessagePack.objectToBytea(content);
        return domainLogEvent(bytes, EventType.ProductQuantityChanged);
    }
}
