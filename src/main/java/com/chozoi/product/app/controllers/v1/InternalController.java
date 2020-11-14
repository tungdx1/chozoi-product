package com.chozoi.product.app.controllers.v1;

import com.chozoi.product.app.dtos.InventoryProductDTO;
import com.chozoi.product.app.dtos.ProductStateDto;
import com.chozoi.product.app.responses.InternalResponse;
import com.chozoi.product.domain.exceptions.CategoryNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController()
@RequestMapping("/v1/internal")
@Log4j2
public class InternalController extends BaseProductController {
  /**
   * Get category by id
   *
   * @return GetCategoryResponse
   * @throws CategoryNotFoundException In case of category not found
   */
  @PutMapping("/inventory/changeQuantity")
  public InternalResponse changeQuantity(@Valid @RequestBody InventoryProductDTO dto) throws Exception {
    return inventoryService.changeQuantity(dto);
  }

  /**
   * Change product state.
   *
   * @return
   */
  @PutMapping("/products/state")
  public Boolean productState(@Valid @RequestBody ProductStateDto dto) throws Exception {
    return inventoryService.productState(dto);
  }

}
