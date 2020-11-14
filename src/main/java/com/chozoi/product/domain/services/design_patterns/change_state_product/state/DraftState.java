package com.chozoi.product.domain.services.design_patterns.change_state_product.state;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductDraft;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.services.design_patterns.change_state_product.data.DataHandle;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DraftState extends Handler implements StateProduct {
  @Override
  public ProductState state() {
    return ProductState.DRAFT;
  }

  @Override
  public DataHandle handle(
      List<ProductDraft> productDrafts, List<Product> products, ProductState preState)
      throws Exception {
    DataHandle data = new DataHandle();
    if (preState == ProductState.PENDING) pendingToDraft(productDrafts, products, data);
    else
      rejectToDraft(productDrafts, data);
    return data;
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  void pendingToDraft(List<ProductDraft> productDrafts, List<Product> product, DataHandle data)
      throws Exception {
    ProductState state = state();
    ProductState preState = ProductState.PENDING;
    checkState(productDrafts, preState);
    productDrafts.forEach(
        productDraft -> {
          productDraft.getData().setState(state);
          productDraft.setState(state);
        });
    product.forEach(
        product1 -> {
          if (product1.getState().equals(preState)) product1.setState(state);
        });
    data.setProductDrafts(productDrafts);
    data.setProducts(product);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  void rejectToDraft(List<ProductDraft> productDrafts, DataHandle data) throws Exception {
    ProductState state = state();
    ProductState preState = ProductState.REJECT;
    checkState(productDrafts, preState);
    productDrafts.forEach(
        productDraft -> {
          productDraft.getData().setState(state);
          productDraft.setState(state);
        });
    data.setProductDrafts(productDrafts);
  }
}
