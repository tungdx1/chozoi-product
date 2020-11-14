package com.chozoi.product.app;

import com.chozoi.product.app.dtos.*;
import com.chozoi.product.app.dtos.elasticsearch.BucketDTO;
import com.chozoi.product.app.dtos.elasticsearch.CategoriesDTO;
import com.chozoi.product.app.responses.ProductPrivateReponse;
import com.chozoi.product.data.ProductData;
import com.chozoi.product.data.elasticsearch.Buckets;
import com.chozoi.product.data.response.*;
import com.chozoi.product.domain.entities.elasticsearch.CategoriesEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductDraftEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.postgres.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import javax.validation.Valid;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ModelMapper {

  Auction auctionToDto(@Valid AuctionDTO product);

  ProductData productToData(Product product);

  List<ProductDTO> productToDTO(List<Product> products);

  @Mappings({
    @Mapping(target = "descriptionPinking", source = "descriptionPicking"),
    @Mapping(target = "descriptionPinkingIn", source = "descriptionPickingIn"),
    @Mapping(target = "descriptionPinkingOut", source = "descriptionPickingOut")
  })
  Product dtoToProduct(ProductCreateDTO productDto);

  Product dtoToProduct(ProductUpdateDTO productDto);

  @Mappings({
    @Mapping(target = "descriptionPicking", source = "descriptionPinking"),
    @Mapping(target = "descriptionPickingIn", source = "descriptionPinkingIn"),
    @Mapping(target = "descriptionPickingOut", source = "descriptionPinkingOut")
  })
  ProductDTO productToDto(Product product);

  CategoryResponse categoryToResponse(Category category);

  List<CategoriesResponse> categoryEsToResponse(List<CategoriesEs> categoriesEs);

  InstantBidDTO instantBidToDTO(InstantBid instantBid);

  AutoBidDTO autoBidToDTO(AutoBid autoBid);

  List<BucketDTO> aggregationToBucketDTO(List<Buckets> buckets);

  List<CategoriesDTO> categoriesToDTO(List<CategoriesEs> categories);

  List<ProductEs> productsResponseToEs(List<ProductsPublicResponse> productRedis);

  List<ProductVariant> dtoToVariants(List<VariantDTO> variantDTOS);

  List<ProductsPublicResponse> productsEsToResponse(List<ProductEs> productEs);

  List<ProductsPublicResponse2> productsEsToResponse2(List<ProductEs> productEs);

  @Valid
  List<VariantDTO> variantsToDto(List<ProductVariant> variants);

  ProductPrivateReponse productToPrivateProduct(Product product);

  ProductDraftEs productDraftToEs(ProductEs productEs);

  List<ShopResponse> shopMdToResponse(List<com.chozoi.product.domain.entities.mongodb.Shop> shop);
}
