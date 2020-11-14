package com.chozoi.product.domain.utils;

import com.chozoi.product.data.response.ProductVariantDetail;
import com.chozoi.product.data.response.ProductsPublicResponse;
import com.chozoi.product.data.response.abstracts.VariantAbstract;
import com.chozoi.product.domain.entities.postgres.ProductVariant;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;

@Log4j2
public class Calculate {
    public static Integer soldQuantity(List<ProductVariantDetail> variants) {
        int soldQuantity = 0;
        if (!Objects.isNull(variants)) {
            soldQuantity = variants.stream().filter(v -> Objects.nonNull(v.getInventory())).mapToInt(v -> v.getInventory().getOutQuantity()).sum();
        }
        return Math.max(soldQuantity, 0);
    }

    public static Integer remainingQuantity(List<ProductVariantDetail> variants) {
        int remainingQuantity = 0;
        if (!Objects.isNull(variants)) {
            remainingQuantity = variants.stream().filter(v -> Objects.nonNull(v.getInventory())).mapToInt(v -> v.getInventory().getInQuantity() + v.getInventory().getInitialQuantity() - v.getInventory().getOutQuantity()).sum();
        }
        return Math.max(remainingQuantity, 0);
    }

    public static Long price(ProductsPublicResponse product) {
        if (product.getType().equals("PROMOTION")) {
            return product.getPromotion().getPrice();
        } else if (product.getType().equals("AUCTION") || product.getType().equals("AUCTION_SALE")) {
            return product.getAuction().getResult().getCurrentPrice();
        } else {
            return product.getVariants().get(0).getPrice();
        }
    }

    public static Long salePrice(ProductsPublicResponse product) {
        if (product.getType().equals("PROMOTION")) {
            return product.getPromotion().getSalePrice();
        } else if (product.getType().equals("AUCTION") || product.getType().equals("AUCTION_SALE")) {
            return product.getAuction().getResult().getCurrentPrice();
        } else {
            return product.getVariants().get(0).getSalePrice();
        }
    }
}
