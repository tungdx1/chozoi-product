package com.chozoi.product.domain.services.design_patterns.change_state_product.state;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductDraft;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.services.design_patterns.change_state_product.data.DataHandle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RejectState extends Handler implements StateProduct {

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
    List<Long> errorIds = new ArrayList<>();
    List<ProductState> states = Arrays.asList(ProductState.PUBLIC, ProductState.READY);
    productDrafts.forEach(
        productDraft -> {
          List<Product> productDetail =
              products.stream()
                  .filter(v -> v.getId().equals(productDraft.getId()))
                  .collect(Collectors.toList());
          Product product = productDetail.get(0);
          if (product.getState() == productDraft.getState() && states.contains(product.getState()))
              errorIds.add(product.getId());
          productDraft.getData().setState(state);
          productDraft.setState(state);
          // update product
          if (!states.contains(product.getState())) product.setState(state);
        });
    if (errorIds.size() > 0) throw new Exception("Sản phẩm id: " + errorIds + " đang bán không được reject");
    data.setProducts(products);
    data.setProductDrafts(productDrafts);
    return data;
  }
}
