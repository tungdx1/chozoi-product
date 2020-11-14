package com.chozoi.product.app.responses;

import com.chozoi.product.domain.entities.elasticsearch.AuctionResultEs;
import com.chozoi.product.domain.entities.elasticsearch.ImageEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductDraftEs;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.*;

@Data
@Log4j2
public class ProductsDraftEsResponse<T> {
  private List<T> products;
  private Metadata metadata;

  public ProductsDraftEsResponse(Page<ProductDraftEs> page, Class<T> tClass) {
    List<T> productListResponses = new ArrayList<>();
    ModelMapper beanMapper = new ModelMapper();
    page.getContent()
        .forEach(
            productEs -> {
              // set images and sort
              setAndSortImage(productEs);
              // set remaining
              productEs
                  .getVariants()
                  .forEach(
                      variantEs -> {
                        try {
                          variantEs.getInventory().inferProperties();
                        } catch (Exception e) {
                          log.error("===== " + productEs.getId());
                        }
                      });
              // hanlde description
              hanldeDescription(productEs);
              // handel price
              if (productEs.getType().equals("AUCTION")
                  || productEs.getType().equals("AUCTION_SALE")) handleAuction(productEs);
              // shop url
              hanldeShop(productEs);
              productListResponses.add(beanMapper.map(productEs, tClass));
            });
    products = productListResponses;
    metadata = Metadata.of(page);
  }

  private void hanldeShop(ProductDraftEs productEs) {
    String img = productEs.getShop().getImgAvatarUrl();
    if (Objects.nonNull(img)) {
      List<String> arrOfStr = Arrays.asList(img.split("profile", 2));
      if (!Objects.isNull(arrOfStr)) if (arrOfStr.size() > 1) {
        String string = arrOfStr.get(0) + "profile/30x30" + arrOfStr.get(1);
        productEs.getShop().setImgAvatarUrl(string);
      }
    }
  }

  private void handleAuction(ProductDraftEs productEs) {
    if (Objects.isNull(productEs.getAuction())
        || Objects.isNull(productEs.getAuction().getResult())
        || productEs.getAuction().getResult().getCurrentPrice() == 0) {
      AuctionResultEs resultEs = new AuctionResultEs();
      resultEs.setCurrentPrice(productEs.getAuction().getStartPrice());
      productEs.getAuction().setResult(resultEs);
    }
    productEs.setPrice(productEs.getAuction().getResult().getCurrentPrice());
    productEs.setSalePrice(productEs.getAuction().getResult().getCurrentPrice());
    // handle time
    productEs.getAuction().mappingTime();
  }

  private void hanldeDescription(ProductDraftEs productEs) {
    String str =
        !Objects.isNull(productEs.getDescription())
            ? productEs.getDescription().length() > 200
                ? productEs.getDescription().substring(0, 200)
                : productEs.getDescription()
            : "";
    productEs.setDescription(str);
  }

  private void setAndSortImage(ProductDraftEs productEs) {
    List<ImageEs> images = ObjectUtils.defaultIfNull(productEs.getImages(), new ArrayList<>());
    images.forEach(
        img -> {
          img.setSort(ObjectUtils.defaultIfNull(img.getSort(), 0));
        });
    images.sort(Comparator.comparing(ImageEs::getSort));
    productEs.setImages(images);
  }
}
