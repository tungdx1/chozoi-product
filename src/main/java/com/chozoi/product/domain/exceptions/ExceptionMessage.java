package com.chozoi.product.domain.exceptions;

import java.util.List;

public class ExceptionMessage {
  public static final String NOT_SELLER = "Bạn chưa phải là seller";
  public static final String USER_IS_REJECT = "Tài khoản bị khóa";
  public static final String NO_MATCHING_SHIPPING_UNITS = "Không hỗ trợ vận chuyển";
  public static String PRODUCT_NOT_FOUND = "Sản phẩm không được tìm thấy";
  public static String PRODUCT_NOT_PUBLIC = "Sản phẩm không có trên sàn";
  public static String USER_NOT_NULL = "User id not null";

  public static String AUCTION_NOT_OVER = "Đấu giá chưa kết thúc";
  public static String AUCTION_USER_NOT_WINNER = "Bạn không phải là người thắng";

  public static String shopNotFound(Object id) {
    return "Shop : " + id + " không được tìm thấy";
  }

  public static String categoryNotFound(Object id) {
    return "Category : " + id + " không được tìm thấy";
  }

  public static String ProductDraftNotFound(Object id) {
    return "Sản phẩm (seller) : " + id + " không được tìm thấy";
  }

  public static String attributeValueNotFound(List<Integer> valueError) {
    return "Attribute value : " + valueError + " không tìm thấy";
  }

  public static String attributeIsRequired(List<Integer> attrRequiredIds) {
    return "Attribute : " + attrRequiredIds + " bắt buộc phải nhập giá trị";
  }

  public static String imagesNotIsProduct(Long id, Long productId) {
    return " id : " + id + " không phải ảnh của product id : " + productId;
  }
}
