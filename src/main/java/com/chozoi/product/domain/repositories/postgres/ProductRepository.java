package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.Category;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.Shop;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  @Query("select u from Product u where u.shop = ?1  and u.state = ?2 ")
  Page<Product> getByShop(Shop shop, ProductState state, Pageable pageable);

  Page<Product> findByCategoryAndState(Category category, ProductState state, Pageable pageable);

  Page<Product> findByStateOrderByIdDesc(ProductState state, Pageable pageable);

  Page<Product> findByTypeAndType(ProductType type, ProductType type_2, Pageable pageable);

  Page<Product> findByTypeAndStateOrderByIdDesc(
      ProductType type, ProductState state, Pageable pageable);

  Page<Product> findByCategoryInAndStateOrderByIdDesc(
      List<Category> category, ProductState state, Pageable pageable);

  Page<Product> findByShopAndTypeInAndStateInOrderByIdDesc(
      Shop shop, List<ProductType> type, List<ProductState> state, Pageable pageable);

  Optional<Product> findByIdAndState(Long productId, ProductState state);

  Page<Product> findByTypeInAndStateInOrderByIdDesc(
      List<ProductType> type, List<ProductState> state, Pageable pageable);

  Page<Product> findByIdInAndShop(List<Long> ids, Shop shop, Pageable pageable);

  List<Product> findByIdInAndShopId(List<Long> ids, Integer idShop);

  Product findByIdAndShopId(Long productId, Integer shopId);

  Page<Product> findByShopAndStateOrderByIdDesc(Shop shop, ProductState state, Pageable pageable);

  //  Page<Product> findByShopAndStateAndQuantityOrderByIdDesc(
  //      Shop shop, ProductState state, Integer quantity, Pageable pageable);

  @Query(
      "select u from Product u where u.state = ?1  and u.type in ?2 and u.auction.timeStart <= ?3 and u.auction.timeEnd >= ?4 order by u.id")
  Page<Product> findByAuctionPageHome(
      ProductState productState,
      List<ProductType> productTypes,
      LocalDateTime timeStart,
      LocalDateTime timeEnd,
      Pageable pageable);

  List<Product> findByIdIn(List<Long> ids);

  // find by ids, state, shop id
  List<Product> findByIdInAndShop_IdAndState(List<Long> ids, Integer shopId, ProductState state);
}
