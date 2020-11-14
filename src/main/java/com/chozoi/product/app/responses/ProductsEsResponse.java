package com.chozoi.product.app.responses;


import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Log4j2

public class ProductsEsResponse<T> extends ProductsPublicResponseAbs {
  private List products;
  private Metadata metadata;

  public ProductsEsResponse(Page<ProductEs> page, Class<T> tClass) {
    products = ProductsPublicResponseAbs(page.getContent(), tClass);
    metadata = Metadata.of(page);
  }

}
