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
public class StopState extends Handler implements StateProduct {

  @Override
  public ProductState state() {
    return ProductState.STOPPED;
  };

  @Override
  public DataHandle handle(
      List<ProductDraft> productDrafts, List<Product> products, ProductState preState)
      throws Exception {
    DataHandle data = new DataHandle();
    // product ready to public
    for (Product product : products)
      if (product.getState() != ProductState.PUBLIC)
        throw new Exception("Trạng thái sản phẩm không phù hợp - id : " + product.getId());
    publicToStop(productDrafts, products, preState, data);
    return data;
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  void publicToStop(
      List<ProductDraft> productDrafts,
      List<Product> products,
      ProductState preState,
      DataHandle data)
      throws Exception {
    ProductState state = state();
    checkState(productDrafts, preState);
    productDrafts.forEach(productDraft -> {});

    for (ProductDraft productDraft : productDrafts) {
      Product product = hanldeProduct(productDraft.getId(), products);
      assert product != null;
      Product productUpdate = productDraft.getData().clone();
      handleForStopFlashBid(productUpdate, product, state);
    }
    data.setProducts(products);
    data.setProductDrafts(productDrafts);
  }
}
