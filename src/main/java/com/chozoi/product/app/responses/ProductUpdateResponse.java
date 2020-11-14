package com.chozoi.product.app.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductUpdateResponse {
    private Boolean status;
    private Boolean statusValidation;
    private Object message;
    private Long id;
}
