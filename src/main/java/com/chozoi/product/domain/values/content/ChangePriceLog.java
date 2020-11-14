package com.chozoi.product.domain.values.content;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePriceLog extends ProductContent {
    private Long preSalePrice;
    private Long salePrice;
    private Long price;
    private Long prePrice;
}
