package com.chozoi.product.data.request;

import com.chozoi.product.domain.entities.postgres.types.ProductState;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateData {
    private ProductState state;
    private Object message;
    private Boolean stateValidate;
}
