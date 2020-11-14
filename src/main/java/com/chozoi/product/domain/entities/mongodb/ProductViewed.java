package com.chozoi.product.domain.entities.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "products.product.view")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewed {
  @Id
  private String id;

  private List<Long> productIds;

  private Long createdAt;

  private Long updatedAt;
}
