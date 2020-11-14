package com.chozoi.product.domain.services.design_patterns.change_state_product.state;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductDraft;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.services.design_patterns.change_state_product.data.DataHandle;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RejectAllState extends Handler implements StateProduct {
  @Override
  public ProductState state() {
    return ProductState.REJECT;
  }

  @Override
  public DataHandle handle(
      List<ProductDraft> productDrafts, List<Product> products, ProductState preState)
      throws Exception {
    DataHandle data = new DataHandle();
    ProductState state = state();
    productDrafts.forEach(
        productDraft -> {
          productDraft.setState(state);
        });
    products.forEach(
        product -> {
          product.setState(state);
        });
    data.setProducts(products);
    data.setProductDrafts(productDrafts);
    return data;
  }
}
