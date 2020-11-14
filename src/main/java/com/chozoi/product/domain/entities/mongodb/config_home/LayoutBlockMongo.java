package com.chozoi.product.domain.entities.mongodb.config_home;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "configs.config_layout_block")
@Data
@Builder
@NoArgsConstructor
public class LayoutBlockMongo extends LayoutBlock {

}

