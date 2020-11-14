package com.chozoi.product.app.dtos;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductIdDTO {
    private List<Long> ids;
}
