package com.chozoi.product.domain.services.design_patterns.change_state_product.state;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductDraft;
import com.chozoi.product.domain.entities.postgres.ProductImage;
import com.chozoi.product.domain.entities.postgres.ProductVariant;
import com.chozoi.product.domain.entities.postgres.types.ProductImageState;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.services.design_patterns.change_state_product.data.DataHandle;
import com.chozoi.product.domain.services.static_service.ProductStaticService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Component
public class ReadyState extends Handler implements StateProduct {

  @Override
  public ProductState state() {
    return ProductState.READY;
  }

  @Override
  public DataHandle handle(
      List<ProductDraft> productDrafts, List<Product> products, ProductState preState)
      throws Exception {
    DataHandle data = new DataHandle();
    if (preState == ProductState.PENDING) pendingToReady(productDrafts, products, data);
    else
      publicToReady(productDrafts, products, data);
    return data;
  }

  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  void pendingToReady(List<ProductDraft> productDrafts, List<Product> products, DataHandle data)
      throws Exception {
    ProductState preState = ProductState.PENDING;
    List<Product> productsUpdate = new ArrayList<>();
    checkState(productDrafts, preState);
    for (ProductDraft productDraft : productDrafts) {
      Product product = hanldeProduct(productDraft.getId(), products);
      assert product != null;
      ProductState isState = ProductStaticService.specifiedStatePublic(product);
      Product productUpdate = productDraft.getData().clone();
      // handle image
      imageService.acceptImage(productUpdate, product);

      //
      if (Objects.nonNull(productUpdate.getImages())) productUpdate.getImages().forEach(ProductImage::inferProperties);
      // accept auction // required variant not null
      handleForAcceptAuction(productUpdate, product, isState);
      productUpdate.setVariants(null);

      //
      productUpdate.inferProperties();

      productDraft.setState(isState);
      List<ProductImage> imageList = new ArrayList<>();
      productUpdate
          .getImages()
          .forEach(
              image -> {
                ProductImage image1 = image.clone();
                if (!image1.getState().equals(ProductImageState.DELETED)) imageList.add(image1);
              });
      productDraft.getData().setImages(imageList);
      productUpdate.setState(isState);
      productUpdate.setVariants(null);
      productUpdate.setStats(null);

      productsUpdate.add(productUpdate);
    }
    productRepository.saveAll(productsUpdate);
  }

  private void saveProducts(List<Product> productsUpdate, DataHandle data) {
    productsUpdate.forEach(
        product -> {
          List<ProductVariant> variants =
              data.getVariants() == null ? new ArrayList<>() : data.getVariants();
          if (product.getVariants() != null) {
            variants.addAll(product.getVariants());
            product.setVariants(null);
          }
        });
    data.setProducts(productsUpdate);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  void publicToReady(List<ProductDraft> productDrafts, List<Product> products, DataHandle data)
      throws Exception {
    ProductState state = ProductState.READY;
    ProductState preState = ProductState.PUBLIC;
    checkState(productDrafts, preState);
    List<Product> productSave = new ArrayList<>();
    for (ProductDraft productDraft : productDrafts) {
      Product product = hanldeProduct(productDraft.getId(), products);
      assert product != null;
      if ((product.getType() == ProductType.AUCTION_SALE
              || product.getType() == ProductType.AUCTION)
          && !Objects.isNull(product.getAuction().getTimeEnd())) try {
        throw new Exception("Product auction not to ready");
      } catch (Exception e) {
        e.printStackTrace();
      }
      product.setState(state);
      if (productDraft.getState() == ProductState.PUBLIC) {
        productDraft.getData().setState(state);
        productDraft.setState(state);
      }
      productSave.add(product);
    }
    data.setProducts(productSave);
    data.setProductDrafts(productDrafts);
  }
}
