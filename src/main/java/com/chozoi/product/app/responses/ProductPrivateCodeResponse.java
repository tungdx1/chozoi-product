package com.chozoi.product.app.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductPrivateCodeResponse {
    private Boolean status;
    private String message;
}
