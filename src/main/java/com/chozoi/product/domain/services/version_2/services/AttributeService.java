package com.chozoi.product.domain.services.version_2.services;

import com.chozoi.product.data.request.AttributeProduct;
import com.chozoi.product.domain.entities.postgres.Attribute;
import com.chozoi.product.domain.entities.postgres.AttributeValue;
import com.chozoi.product.domain.entities.postgres.Category;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.exceptions.ExceptionMessage;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttributeService {

  /** check and set attribute for product */
  public static void setAttribute(Product product) throws NotFoundException {
    List<Integer> valueIds =
        product.getAttributes().stream()
            .map(AttributeProduct::getValue_id)
            .collect(Collectors.toList());
    Category category = product.getCategory();
    List<Attribute> attributes = category.getAttributes();
    List<Integer> attrRequiredIds =
        attributes.stream()
            .filter(Attribute::getIsRequired)
            .map(Attribute::getId)
            .collect(Collectors.toList());
    List<AttributeProduct> attributeProducts = new ArrayList<>();
    List<Integer> valueError = new ArrayList<>();
    Map<Integer, Integer> maps = new HashMap<>();
    Map<Integer, Attribute> attributeMap = new HashMap<>();
    Map<Integer, AttributeValue> valueMap = new HashMap<>();
    attributes.forEach(
        attribute -> {
          List<AttributeValue> values = attribute.getValues();
          attributeMap.put(attribute.getId(), attribute);
          values.forEach(
              v -> {
                maps.put(v.getId(), attribute.getId());
                valueMap.put(v.getId(), v);
              });
        });
    valueIds.forEach(
        id -> {
          if (Objects.isNull(maps.get(id))) valueError.add(id);
          else {
            int attributeId = maps.get(id);
            attrRequiredIds.removeIf(v -> v.equals(attributeId));
            AttributeProduct data =
                AttributeProduct.builder()
                    .id(attributeId)
                    .name(attributeMap.get(attributeId).getName())
                    .value_id(id)
                    .value(valueMap.get(id).getValue())
                    .build();
            attributeProducts.add(data);
          }
        });
    if (valueError.size() != 0) throw new NotFoundException(ExceptionMessage.attributeValueNotFound(valueError));
    // check attribute required
    if (attrRequiredIds.size() != 0) throw new NotFoundException(ExceptionMessage.attributeIsRequired(attrRequiredIds));
    product.setAttributes(attributeProducts);
  }
}
