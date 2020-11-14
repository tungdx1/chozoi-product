package com.chozoi.product.data;

import com.chozoi.product.domain.entities.postgres.types.ProductState;
import lombok.Data;

@Data
public class ChangeStateHanldeData {
    private Long id;
    private ProductState state;
    private ProductState preState;
}
