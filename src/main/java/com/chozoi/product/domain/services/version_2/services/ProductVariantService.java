package com.chozoi.product.domain.services.version_2.services;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductVariant;
import com.chozoi.product.domain.entities.postgres.types.VariantState;
import com.chozoi.product.domain.repositories.postgres.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductVariantService {

  @Autowired private ProductVariantRepository productVariantRepository;

  /**
   * add new variant : id is null change state variant to DELETED update data for variant exist
   *
   * @param productNew
   */
  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public void handleVariant(Product productOld, Product productNew) throws Exception {
    // handle variant
    List<ProductVariant> variants = productNew.getVariants();
    Product productOld2 = productOld.clone();
    List<ProductVariant> variantsOld = productOld2.getVariants();
    List<ProductVariant> variantsDelete = new ArrayList<>();
    if (Objects.isNull(variants)) throw new Exception("variants not null");
    variants.forEach(
        variant -> {
          variant.setState(VariantState.PUBLIC);
          variant.getInventory().setOutQuantity(0);
          if (Objects.nonNull(variant.getId())) {
            List<ProductVariant> variantList =
                variantsOld.stream()
                    .filter(v -> v.getId().equals(variant.getId()))
                    .collect(Collectors.toList());
            if (variantList.size() > 0) {
              variant
                  .getInventory()
                  .setOutQuantity(variantList.get(0).getInventory().getOutQuantity());
              variantsOld.remove(variantList.get(0));
            } else variants.remove(variant);
          }
        });
    variantsOld.forEach(
        variantOld -> {
          variantOld.setState(VariantState.DELETED);
        });
    variants.addAll(variantsOld);
    variants.forEach(ProductVariant::inferProperties);
    productNew.setVariants(variants);
    productNew.inferProperties();
    productVariantRepository.saveAll(productNew.getVariants());
  }
}
