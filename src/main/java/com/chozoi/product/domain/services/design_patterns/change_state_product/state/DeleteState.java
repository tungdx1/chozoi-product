package com.chozoi.product.domain.services.design_patterns.change_state_product.state;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductDraft;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.services.design_patterns.change_state_product.data.DataHandle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeleteState extends Handler implements StateProduct {

  @Override
  public ProductState state() {
    return ProductState.DELETED;
  }

  @Override
  public DataHandle handle(
      List<ProductDraft> productDrafts, List<Product> products, ProductState preState)
      throws Exception {
    DataHandle data = new DataHandle();
    ProductState state = state();
    productDrafts.forEach(
        productDraft -> {
          productDraft.getData().setState(state);
          productDraft.setState(state);
        });
    List<Long> ids = new ArrayList<>();
    products.forEach(
        product -> {
          if (!statesAcceptDelete.contains(product.getState())) ids.add(product.getId());
          product.setState(state);
        });
    if (ids.size() > 0) throw new Exception("product id : " + ids + " not delete");
    // set data
    data.setProducts(products);
    data.setProductDrafts(productDrafts);
    return data;
  }
}
