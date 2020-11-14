package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.ProductVariant;
import com.chozoi.product.domain.entities.postgres.types.VariantState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductId(Long productId);

    List<ProductVariant> findByProduct_IdAndState(Long productId, VariantState state);

    void deleteAllByIdIn(List<Long> ids);

//    @Modifying
//    @Query(
//            value = "delete from products.product_variant u where u.id in :1",
//            nativeQuery = true)
//    void deleteVariant(@Param("id") List<Long> ids);
//
//    @Modifying
//    @Query(
//            value = "delete from products.inventory u where u.id in :1",
//            nativeQuery = true)
//    void deleteInventory(@Param("id") List<Long> ids);
//
//    @Modifying
//    @Query(
//            value = "delete from products.inventory_history u where u.variant_id in :id",
//            nativeQuery = true)
//    void deleteInventoryHistory(@Param("id") List<Long> ids);
//
//
//    default void deleteVariants(List<Long> ids) {
//        deleteInventory(ids);
//        deleteInventoryHistory(ids);
//        deleteVariant(ids);
//    }

}
