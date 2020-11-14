package com.chozoi.product.domain.values.content;

import com.chozoi.product.domain.entities.postgres.EventContent;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class BaseContent extends EventContent {
    private Long productId;
    private Integer shopId;
    private Integer updatedById;
    private Integer updatedBySystemId;
    private ProductContent data;
}
