package com.chozoi.product.domain.values.content;

import com.chozoi.product.domain.entities.postgres.types.ProductState;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeStateLog extends ProductContent {
    private ProductState state;
    private ProductState preState;
}
