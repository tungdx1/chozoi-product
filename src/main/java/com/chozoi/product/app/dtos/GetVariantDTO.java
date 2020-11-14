package com.chozoi.product.app.dtos;

import lombok.Data;

import java.util.List;

@Data
public class GetVariantDTO {
    private List<Long> ids;
}
