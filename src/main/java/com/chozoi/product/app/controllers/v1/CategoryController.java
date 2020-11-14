package com.chozoi.product.app.controllers.v1;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.app.responses.CategoriesResponse;
import com.chozoi.product.app.responses.CategoryResponse;
import com.chozoi.product.app.responses.Metadata;
import com.chozoi.product.domain.entities.elasticsearch.CategoriesEs;
import com.chozoi.product.domain.entities.postgres.Category;
import com.chozoi.product.domain.exceptions.CategoryNotFoundException;
import com.chozoi.product.domain.services.CategoryService;
import com.chozoi.product.domain.services.elasticsearch.CategoryElasticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/v1")
@Slf4j
public class CategoryController {

  @Autowired private CategoryService categoryService;

  @Autowired private CategoryElasticService categoryElasticService;

  @Autowired private ModelMapper modelMapper;

  /**
   * Get category by id
   *
   * @param categoryId category id
   * @return GetCategoryResponse
   * @throws CategoryNotFoundException In case of category not found
   */
  @GetMapping("/categories/{category_id}")
  public CategoryResponse getCategory(@PathVariable("category_id") int categoryId) {
    // TODO : cache and mongo
    Category category = categoryService.findById(categoryId);
    return new CategoryResponse<>(modelMapper.categoryToResponse(category));
  }

  /**
   * Get categories by page
   *
   * @return GetCategoriesResponse
   */
  @GetMapping("/categories")
  public CategoriesResponse getCategories(
      @RequestParam(required = false) Integer level,
      @RequestParam(name = "parent_id", required = false) Integer parentId) {
    Pageable page = new PageRequest(0, 10000);
    Page<CategoriesEs> category = categoryElasticService.findAll(level, parentId, page);
    return new CategoriesResponse<>(
        modelMapper.categoryEsToResponse(category.getContent()), Metadata.of(category));
  }
}
