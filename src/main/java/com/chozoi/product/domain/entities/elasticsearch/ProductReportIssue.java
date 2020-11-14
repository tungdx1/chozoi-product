package com.chozoi.product.domain.entities.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReportIssue {
    private Long id;
    private Integer categoryId;
    private String description;
    private String solution;
//  private Long createdAt;
//  private Long updatedAt;
}
