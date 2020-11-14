package com.chozoi.product.domain.entities.mongodb.config_home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "configs.layout_block_group")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayoutBlockGroupMongo {
  @Id private Integer id;
  private Integer blockId;
  private Integer groupId;
  private Double rate;
  private Long createdAt;
  private Long updatedAt;
  private List<Long> products;
  private Integer tabIndex;
  private String state;

  @Transient private ProductGroup group;
}
