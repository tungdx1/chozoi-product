package com.chozoi.product.app.responses;

import com.chozoi.product.domain.entities.elasticsearch.AuctionEs;
import com.chozoi.product.domain.entities.elasticsearch.AuctionResultEs;
import com.chozoi.product.domain.entities.elasticsearch.ImageEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public abstract class ProductsPublicResponseAbs<T> {

  public List<T> ProductsPublicResponseAbs(List<ProductEs> page, Class<T> tClass) {
    List<T> productListResponses = new ArrayList<>();
    ModelMapper beanMapper = new ModelMapper();
    page.forEach(
            productEs -> {
              if ( !Objects.isNull(productEs.getImages()) ) {
                List<ImageEs> imageEsList =
                        productEs.getImages().stream()
                                .filter(imageEs -> Objects.isNull(imageEs.getSort()))
                                .collect(Collectors.toList());
                if ( imageEsList.isEmpty() ) {
                  productEs.getImages().sort(Comparator.comparing(ImageEs::getSort));
                }
              }
              String str = null;
              if ( !Objects.isNull(productEs.getDescription()) ) {
                str =
                        productEs.getDescription().length() > 200
                                ? productEs.getDescription().substring(0, 200)
                                : productEs.getDescription();
              }
              productEs.setDescription(str);
              //
              DecimalFormat df = new DecimalFormat("#.#");
              productEs
                      .getStats()
                      .setAverageRating(Float.valueOf(df.format(productEs.getStats().getAverageRating())));
              // handel price
              if ( productEs.getType().equals("AUCTION") || productEs.getType().equals("AUCTION_SALE") ) {
                if ( Objects.nonNull(productEs.getAuction()) ) {
                  if ( Objects.isNull(productEs.getAuction().getResult())
                          || productEs.getAuction().getResult().getCurrentPrice() == 0 ) {
                    AuctionResultEs resultEs = new AuctionResultEs();
                    resultEs.setCurrentPrice(productEs.getAuction().getStartPrice());
                    productEs.getAuction().setResult(resultEs);
                  }
                  productEs.setPrice(productEs.getAuction().getResult().getCurrentPrice());
                  productEs.setSalePrice(productEs.getAuction().getResult().getCurrentPrice());
                } else {
                  AuctionEs auction = new AuctionEs();
                  auction.setId(productEs.getId());
                  AuctionResultEs resultEs = new AuctionResultEs();
                  resultEs.setId(productEs.getId());
                  auction.setResult(resultEs);
                }
              }
              // shop url
              String img = productEs.getShop().getImgAvatarUrl();
              if ( Objects.nonNull(img) ) {
                List<String> arrOfStr = Arrays.asList(img.split("profile", 2));
                if ( arrOfStr.size() == 2 ) {
                  String string = arrOfStr.get(0) + "profile/180x180" + arrOfStr.get(1);
                  productEs.getShop().setImgAvatarUrl(string);
                }
              }
              try {
                if ( productEs.getShop().getFreeShipStatus().equals("OFF") ) {
                  productEs.setFreeShipStatus(false);
                }
              } catch (Exception e) {
                productEs.setFreeShipStatus(false);
              }
              productListResponses.add(beanMapper.map(productEs, tClass));
            });
    return productListResponses;
  }
}
