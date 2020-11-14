package com.chozoi.product.domain.services.version_2.services;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.app.dtos.ProductCreateDTO;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductDraft;
import com.chozoi.product.domain.entities.postgres.ProductImage;
import com.chozoi.product.domain.entities.postgres.types.ProductImageState;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.exceptions.ExceptionMessage;
import com.chozoi.product.domain.repositories.postgres.ProductDraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProductDraftService {
  private final Integer UPDATE_VERSION = 1;
  @Autowired private ProductDraftRepository productDraftRepository;
  @Autowired private ModelMapper modelMapper;
  /**
   * create product draft
   *
   * @param product
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public void create(Product product) throws CloneNotSupportedException {
    Product product1 = product.clone();
    product1.getCategory().setAttributes(new ArrayList<>());
    ProductDraft productDraft =
        ProductDraft.builder()
            .id(product1.getId())
            .data(product1)
            .state(product1.getState())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .updatedVersion(UPDATE_VERSION)
            .build();
    productDraftRepository.save(productDraft);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public void update(ProductCreateDTO dto, Product product) throws Exception {
    // mapper
    Product product1 = product.clone();
    List<ProductImage> imageList = new ArrayList<>();
    product1
        .getImages()
        .forEach(
            image -> {
              if (!image.getState().equals(ProductImageState.DELETED)) imageList.add(image.clone());
            });
    product1.setImages(imageList);
    // check exist
    ProductDraft productDraft = findById(product.getId());
    // up version
    int version =
        Objects.isNull(productDraft.getUpdatedVersion()) ? 1 : productDraft.getUpdatedVersion();
    version += 1;
    // add data
    productDraft.setState(product1.getState());
    productDraft.setData(product1);
    productDraft.setUpdatedAt(LocalDateTime.now());
    productDraft.setUpdatedVersion(version);
    // save
    productDraftRepository.save(productDraft);
  }

  /**
   * get product draft
   *
   * @param productId
   * @return
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public ProductDraft findById(Long productId) throws Exception {
    return productDraftRepository
        .findById(productId)
        .orElseThrow(() -> new Exception("product id : " + productId + " not found"));
  }

  /**
   * update partial product draft
   *
   * @param product
   */
  //  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public void updatePartial(Product product) throws Exception {
    ProductDraft draft =
        productDraftRepository
            .findById(product.getId())
            .orElseThrow(() -> new Exception(ExceptionMessage.PRODUCT_NOT_FOUND));
    product.setVariants(null);
    draft.setData(product);
    productDraftRepository.save(draft);
  }
}
