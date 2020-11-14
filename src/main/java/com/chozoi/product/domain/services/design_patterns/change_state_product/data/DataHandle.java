package com.chozoi.product.domain.services.design_patterns.change_state_product.data;

import com.chozoi.product.domain.entities.postgres.*;
import lombok.Data;

import java.util.List;

@Data
public class DataHandle {
  List<Product> products;
  List<ProductDraft> productDrafts;
  List<Auction> auctions;
  List<ProductImage> images;
  List<ProductVariant> variants;
}
