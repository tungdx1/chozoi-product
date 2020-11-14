package com.chozoi.product.domain.services.design_patterns.change_state_product.state;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductDraft;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.services.design_patterns.change_state_product.data.DataHandle;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
public class PendingState extends Handler implements StateProduct {
  @Override
  public ProductState state() {
    return ProductState.PENDING;
  }

  @Override
  public DataHandle handle(
      List<ProductDraft> productDrafts, List<Product> products, ProductState preState)
      throws Exception {
    DataHandle data = new DataHandle();
    if (preState == ProductState.DRAFT) draftToPending(productDrafts, products, data);
    else
        rejectToPending(productDrafts, products, data);
    return data;
  }

  private void draftToPending(
      List<ProductDraft> productDrafts, List<Product> products, DataHandle data) throws Exception {
    ProductState state = state();
    ProductState preState = ProductState.DRAFT;
    checkState(productDrafts, preState);
    productDrafts.forEach(
        productDraft -> {
          Product product =
              products.stream()
                  .filter(v -> v.getId().equals(productDraft.getId()))
                  .findFirst()
                  .orElse(new Product());
          productDraft.getData().setState(state);
          productDraft.setState(state);
          if (product.getState() != ProductState.PUBLIC && product.getState() != ProductState.READY)
              product.setState(state);
        });
    data.setProducts(products);
    data.setProductDrafts(productDrafts);
  }

  private void rejectToPending(
      List<ProductDraft> productDrafts, List<Product> products, DataHandle data) throws Exception {
    ProductState state = state();
    ProductState preState = ProductState.REJECT;
    checkState(productDrafts, preState);
    productDrafts.forEach(
        productDraft -> {
          List<Product> product1 =
              products.stream()
                  .filter(product -> product.getId().equals(productDraft.getId()))
                  .collect(Collectors.toList());
          if (product1.size() > 0) {
            Product product = product1.get(0);
            log.info(
                "======= productState"
                    + product.getState()
                    + " ==== productDraft"
                    + productDraft.getData().getState()
                    + productDraft.getState());
            if (product.getState().equals(productDraft.getState())) product.setState(state);
          }

          productDraft.getData().setState(state);
          productDraft.setState(state);
        });
    data.setProductDrafts(productDrafts);
    data.setProducts(products);
  }
}
