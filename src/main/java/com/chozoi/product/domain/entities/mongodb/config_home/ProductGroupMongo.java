package com.chozoi.product.domain.entities.mongodb.config_home;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products.group")
@Data
@Builder
@NoArgsConstructor
public class ProductGroupMongo extends ProductGroup {}
