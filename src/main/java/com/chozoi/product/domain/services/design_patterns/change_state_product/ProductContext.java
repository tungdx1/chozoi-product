package com.chozoi.product.domain.services.design_patterns.change_state_product;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductDraft;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.services.design_patterns.change_state_product.state.Handler;
import com.chozoi.product.domain.services.design_patterns.change_state_product.state.StateProduct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductContext extends Handler {
  private StateProduct state;

  public void setState(StateProduct state) {
    this.state = state;
  }

  public void changeState(
      List<ProductDraft> productDrafts, List<Product> products, ProductState preState)
      throws Exception {
    this.state.handle(productDrafts, products, preState);
  }
}
