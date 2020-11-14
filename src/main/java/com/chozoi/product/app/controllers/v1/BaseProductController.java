package com.chozoi.product.app.controllers.v1;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.app.dtos.ProductDTO;
import com.chozoi.product.app.responses.GetProductsResponse;
import com.chozoi.product.app.responses.Metadata;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.Shop;
import com.chozoi.product.domain.entities.postgres.types.UserRole;
import com.chozoi.product.domain.entities.postgres.types.UserRoleState;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
import com.chozoi.product.domain.repositories.mongodb.ShopMDRepository;
import com.chozoi.product.domain.repositories.postgres.ShopRepository;
import com.chozoi.product.domain.services.AuctionParticipantService;
import com.chozoi.product.domain.services.InternalService;
import com.chozoi.product.domain.services.ProductSellerService;
import com.chozoi.product.domain.services.ProductService;
import com.chozoi.product.domain.services.elasticsearch.ProductElasticService;
import com.chozoi.product.domain.services.elasticsearch.ShopProductService;
import com.chozoi.product.domain.services.mongo.ProductMongoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

@Log4j2
public class BaseProductController {

  protected Integer page = 1;
  protected Integer size = 20;

  @Autowired protected ProductService productService;

  @Autowired protected ProductMongoService productMongoService;
  @Autowired protected ProductSellerService productSellerService;
  @Autowired protected AuctionParticipantService auctionParticipantService;

  @Autowired protected InternalService inventoryService;
  @Autowired protected ShopProductService shopProductService;

  @Autowired protected ProductElasticService productElasticService;

  @Autowired protected ModelMapper modelMapper;

  @Autowired
  ShopRepository shopRepository;

  /**
   * check pedding seller
   *
   * @param state
   */
  protected void checkIsPeddingSeller(String state) {
    if (state == null) throw new ResourceNotFoundException("You are not seller");
    if (UserRoleState.valueOf(state) == UserRoleState.REJECT) throw new ResourceNotFoundException("Seller Reject");
  }

  /**
   * check is seller
   *
   * @param role
   */
  protected void checkIsSeller(String role) {
    if (!(UserRole.valueOf(role) == UserRole.SELLER)) throw new ResourceNotFoundException("You are not seller");
  }

  protected Shop checkShop(Integer shopId, Integer reqShopId) {
    if (!shopId.equals(reqShopId)) throw new ResourceNotFoundException("Shop not found");
    Shop shop = new Shop();
    shop.setId(shopId);
    return shop;
  }

  GetProductsResponse getProductsResponse(Page<Product> page) {
    List<ProductDTO> products = modelMapper.productToDTO(page.getContent());
    return new GetProductsResponse(products, Metadata.of(page));
  }

  protected void checkWritable(Boolean writable) throws Exception {
    if (!writable) throw new Exception("Hệ thống bảo trì");
  }

  protected void validShop(Integer reqShopId, Integer userId) throws Exception {
    Shop shop = shopRepository.findAllById(reqShopId);
    if (shop == null) throw new Exception("Cửa hàng không tồn tại");
    if (!shop.getUserId().equals(userId)) throw new Exception("Bạn không thuộc quyền quản lý cửa hàng!");
  }
}
