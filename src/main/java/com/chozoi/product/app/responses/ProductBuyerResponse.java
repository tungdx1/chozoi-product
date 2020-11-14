package com.chozoi.product.app.responses;

import com.chozoi.product.domain.entities.elasticsearch.AuctionEs;
import com.chozoi.product.domain.entities.elasticsearch.AuctionResultEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Log4j2
public class ProductBuyerResponse {
  private List<ProductEs> products;
  private Metadata metadata;

  public ProductBuyerResponse(Page<ProductEs> page) {
    page.getContent()
        .forEach(
            productEs -> {
              AuctionEs auctionEs =
                  ObjectUtils.defaultIfNull(productEs.getAuction(), new AuctionEs());
              auctionEs.setId(productEs.getId());
              AuctionResultEs resultEs =
                  ObjectUtils.defaultIfNull(auctionEs.getResult(), new AuctionResultEs());
              resultEs.setId(productEs.getId());
              auctionEs.setResult(resultEs);
              productEs.setAuction(auctionEs);
            });
    products = page.getContent();
    metadata = Metadata.of(page);
  }
}
