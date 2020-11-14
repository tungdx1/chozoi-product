package com.chozoi.product.app.controllers.v2;

import com.chozoi.product.app.responses.HomeBoxDetailResponse;
import com.chozoi.product.app.responses.HomeBoxListResponse;
import com.chozoi.product.app.responses.ProductResponse;
import com.chozoi.product.data.response.ProductsPublicResponse;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
import com.chozoi.product.domain.services.ConfigService;
import com.chozoi.product.domain.services.version_2.ProductFacade;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/v1/products")
@Log4j2
public class ProductV2Controller {
  @Autowired private ProductFacade productFacade;
  @Autowired private ConfigService configService;
  /**
   * Get product by id
   *
   * @param productId
   * @return
   * @throws ResourceNotFoundException
   */
  @GetMapping(path = "/{productId}")
  public ProductResponse getProduct(@PathVariable long productId, String userId) throws Exception {
    return productFacade.getProductForBuyer(productId, userId);
  }

  @GetMapping(path = "/home/boxs")
  public HomeBoxListResponse boxList() throws Exception {
    return configService.boxList();
  }

  @GetMapping(path = "/home/boxs/{id}")
  public HomeBoxDetailResponse boxDetail(
      @PathVariable(value = "id") Integer blockId, @RequestParam Integer taskId) throws Exception {
    return configService.boxDetail(blockId, taskId);
  }
}
