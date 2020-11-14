package com.chozoi.product.domain.services;

import com.chozoi.product.app.dtos.InventoryProductDTO;
import com.chozoi.product.app.dtos.InvetoryChangeQuantity;
import com.chozoi.product.app.dtos.ProductStateDto;
import com.chozoi.product.app.responses.InternalResponse;
import com.chozoi.product.domain.entities.postgres.*;
import com.chozoi.product.domain.entities.postgres.types.InventoryHistoryState;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class InternalService extends BaseService {

  public Boolean productState(ProductStateDto dto) throws Exception {
    // TODO use state pattern and Factory Method Pattern
    switch (dto.getState()) {
      case READY:
        acceptedProducts(dto);
        return true;
      case REJECT: // reject content
        rejectContentProduct(dto);
        return true;
      case REJECTPRODUCT: // reject product
        rejectProduct(dto);
        return true;
      case REPORT:
        reportProduct(dto);
        return true;
      default:
        return false;
    }
  }

  /**
   * ban product
   *
   * @param dto
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void reportProduct(ProductStateDto dto) throws Exception {
    List<Long> ids = new ArrayList<>(Collections.singletonList(dto.getProductId()));
    List<Product> productList =
        changeStateProducts(
            null,
            ProductState.REPORT,
            ids,
            null,
            dto.getUserSystemId(),
            null,
            dto.getDescription());
    // save issue report
    saveIssueReport(dto, productList.get(0));
  }

  /**
   * Reject product
   *
   * @param dto
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void rejectProduct(ProductStateDto dto) throws Exception {
    List<Long> ids = new ArrayList<>(Collections.singletonList(dto.getProductId()));
    List<Product> productList =
        changeStateProducts(
            null,
            ProductState.REJECTPRODUCT,
            ids,
            null,
            dto.getUserSystemId(),
            null,
            dto.getDescription());
    Product product = productList.get(0);
    // save issue report
    saveIssueReport(dto, product);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  public void saveIssueReport(ProductStateDto dto, Product product) throws Exception {
    ProductReportIssue issue = new ProductReportIssue();
    issue.setProduct(product);
    issue.setDescription(dto.getDescription());
    issue.setSolution(dto.getSolution());
    // save report issue category
    ProductReportIssueCategory cate = new ProductReportIssueCategory();
    cate.setId(dto.getCategoryId());
    issue.setCategory(cate);
    productReportIssueRepository.save(issue);
    // TODO: update issue
    // update data for product draft
    ProductDraft productDraft =
        productDraftRepository
            .findById(product.getId())
            .orElseThrow(() -> new Exception("product draft not found"));
    productDraft.getData().setReportIssues(issue);
    productDraftRepository.save(productDraft);
  }

  /**
   * Reject.
   *
   * @param dto
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  void rejectContentProduct(ProductStateDto dto) throws Exception {
    List<Long> ids = new ArrayList<>(Collections.singletonList(dto.getProductId()));
    List<Product> productList =
        changeStateProducts(
            null,
            ProductState.REJECT,
            ids,
            null,
            dto.getUserSystemId(),
            null,
            dto.getDescription());
    Product product = productList.get(0);
    saveIssueReport(dto, product);
  }

  /**
   * Ready or public.
   *
   * @param dto
   */
  @Transactional(propagation = Propagation.REQUIRED)
  void acceptedProducts(ProductStateDto dto) throws Exception {
    ProductDraft productDraft =
        productDraftRepository
            .findById(dto.getProductId())
            .orElseThrow(() -> new Exception("product not found"));
    if (!productDraft.getUpdatedVersion().equals(dto.getUpdatedVersion())) {
        throw new Exception("Vui lòng F5 để xem dữ liệu mới nhất");
    }
    List<Long> ids = new ArrayList<>(Collections.singletonList(dto.getProductId()));
    changeStateProducts(
        ProductState.PENDING, ProductState.READY, ids, null, dto.getUserSystemId(), null, null);
  }

  @Transactional(
      propagation = Propagation.REQUIRED,
      rollbackFor = Exception.class,
      isolation = Isolation.SERIALIZABLE)
  public InternalResponse changeQuantity(InventoryProductDTO dto) throws Exception {
    try {
      InventoryHistoryState state = dto.getState();
      List<Long> ids =
          dto.getData().stream()
              .map(InvetoryChangeQuantity::getVariantId)
              .collect(Collectors.toList());
      Map<Long, Integer> idQuatity = new HashMap<>();
      dto.getData()
          .forEach(
              v -> {
                idQuatity.put(v.getVariantId(), v.getQuantity());
              });
      // get variant
      List<ProductVariant> variants = productVariantRepository.findAllById(ids);
      // call handle
      List<ProductVariant> variantsUpdate = new ArrayList<>();
      for (ProductVariant variant : variants) {
        ProductVariant v = hanldeVariant(variant, idQuatity.get(variant.getId()), state);
        v.inferProperties();
        variantsUpdate.add(v);
      }
      // write log change
      List<Long> keepIds = new ArrayList<>();
      variantsUpdate.forEach(
          productVariant -> {
            List<ProductVariant> variants1 =
                variants.stream()
                    .filter(v -> v.getId().equals(productVariant.getId()))
                    .collect(Collectors.toList());
            Inventory preInventory = variants1.get(0).getInventory();
            Inventory inventory = productVariant.getInventory();
            InventoryKeep keep =
                inventoryQuantityChangedEvent(
                    preInventory, inventory, state, productVariant.getProduct(), null, true);
            keepIds.add(keep.getId());
          });
      this.saveVariants(variantsUpdate);
      return new InternalResponse(true, keepIds);
    } catch (Exception e) {
      return new InternalResponse(false, new ArrayList<>());
    }
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  void saveVariants(List<ProductVariant> variants) {
    productVariantRepository.saveAll(variants);
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
  ProductVariant hanldeVariant(ProductVariant variant, int quantityDto, InventoryHistoryState state)
      throws Exception {
    // get variant_id
    Product product =
        productRepository
            .findById(variant.getProduct().getId())
            .orElseThrow(() -> new Exception("Product not found"));
    // update products.Inventory and create products.inventory_history
    ProductVariant variantResponse;
    variantResponse = (ProductVariant) variant.clone();
    Inventory inventory;
    inventory = (Inventory) variantResponse.getInventory().clone();
    if (state == InventoryHistoryState.CANCELED) {
      inventory.setOutQuantity(variantResponse.getInventory().getOutQuantity() - quantityDto);
      if (inventory.getOutQuantity() < 0) {
          throw new Exception(
              "Variant id : "
                  + variantResponse.getId()
                  + ", out quantity: "
                  + variantResponse.getInventory().getOutQuantity());
      }
      variantResponse.setInventory(inventory);
      InventoryHistory history = new InventoryHistory();
      history.setVariant(variantResponse);
      history.setQuantity(quantityDto);
      history.setType(state);
      variantResponse.getInventoryHistory().add(history);
    } else if (state == InventoryHistoryState.ORDERED) {
      int quantity =
          +variantResponse.getInventory().getInQuantity()
              - variantResponse.getInventory().getOutQuantity();
      if (product.getIsQuantityLimited() && quantity < quantityDto) {
          throw new Exception("sold off");
      } else {
        inventory.setOutQuantity(variantResponse.getInventory().getOutQuantity() + quantityDto);
        variantResponse.setInventory(inventory);
        InventoryHistory history = new InventoryHistory();
        history.setVariant(variantResponse);
        history.setQuantity(quantityDto);
        history.setType(state);
        variantResponse.getInventoryHistory().add(history);
      }
    } else {
        throw new Exception("state not matched");
    }
    return variantResponse;
  }
}
