package com.chozoi.product.domain.services;

import com.chozoi.product.data.response.ProductsPublicResponse;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.postgres.Comment;
import com.chozoi.product.domain.entities.postgres.Order;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.Shop;
import com.chozoi.product.domain.entities.postgres.types.CommentState;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.entities.types.ShopOrderState;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import com.chozoi.product.domain.services.mongo.ProductMongoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Log4j2
public class ProductService extends BaseService {

  @Autowired ProductMongoService mongoService;

  @Autowired ProductEsRepository productEsRepository;

  /**
   * Get products by shop
   *
   * @param shopId
   * @param pageable
   * @return
   * @throws ResourceNotFoundException
   */
  @Transactional(readOnly = true)
  public Page<Product> getByShop(Integer shopId, Pageable pageable)
      throws ResourceNotFoundException {
    Shop shop = getShop(shopId);
    return productRepository.getByShop(shop, ProductState.PUBLIC, pageable);
  }

  /**
   * Get product promotion by shop id
   *
   * @param shopId
   * @param pageable
   * @return
   */
  public Page<Product> getProductPromotionByShop(Integer shopId, Pageable pageable) {
    Shop shop = getShop(shopId);
    List<ProductType> types = new ArrayList<>();
    List<ProductState> states = new ArrayList<>();
    types.add(ProductType.PROMOTION);
    states.add(ProductState.PUBLIC);
    return productRepository.findByShopAndTypeInAndStateInOrderByIdDesc(
        shop, types, states, pageable);
  }

  public Page<ProductEs> notYetRated(Integer userId, Pageable pageable) {
    List<Order> orders = orderRepository.findByBuyerIdAndState(userId, ShopOrderState.FINISHED);
    Map<Long, Long> productIds = new HashMap<>();
    // map productId - count order finised
    Map<Long, Long> productRatingIds = new HashMap<>();
    orders.forEach(
        order -> {
          Long count =
              productIds.containsKey(order.getProductId())
                  ? productIds.get(order.getProductId()) + 1
                  : 1;
          productIds.put(order.getProductId(), count);
        });
    List<Comment> comments = commentRepository.findByStateAndUserId(CommentState.PUBLIC, userId);

    comments.forEach(
        comment -> {
          Long count =
              productRatingIds.containsKey(comment.getProductId())
                  ? productRatingIds.get(comment.getProductId()) + 1
                  : 1;
          productRatingIds.put(comment.getProductId(), count);
        });

    List<Long> ids = new ArrayList<>();
    productIds.forEach(
        (k, v) -> {
          if (!productRatingIds.containsKey(k) || v > productRatingIds.get(k)) ids.add(k);
        });
    if (ids.size() <= pageable.getPageSize() * pageable.getPageNumber())
        return new PageImpl<>(new ArrayList<>(), pageable, 0);

    return productEsRepository.findByIdIn(ids, pageable);
  }

  /**
   * get products by ids
   *
   * @param ids
   * @param shopId
   * @return
   */
  public List<ProductsPublicResponse> getAllProductById(String ids, Integer shopId) {
    String[] result = ids.split(",");
    List<String> list = Arrays.asList(result);
    ;
    List<Long> id = new ArrayList<>();
    list.forEach(
        str -> {
          try {
            id.add(Long.valueOf(str));
          } catch (Exception e) {
          }
        });
    List<ProductEs> productEs = new ArrayList<>();
    if (shopId == null) productEs = productEsRepository.findByIdInAndState(id, "PUBLIC");
    else
        productEs = productEsRepository.findByIdInAndStateAndShop_Id(id, "PUBLIC", shopId);
    return modelMapper.productsEsToResponse(productEs);
  }
}
