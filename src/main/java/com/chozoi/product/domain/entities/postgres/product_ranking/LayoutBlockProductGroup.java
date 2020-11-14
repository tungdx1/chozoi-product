package com.chozoi.product.domain.entities.postgres.product_ranking;

import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "layout_block_product_group", schema = "configs")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class LayoutBlockProductGroup {

  @Id
  private Integer id;

  @Column(name = "block_id")
  private Integer blockId;

  private Double rate;

  @Type(type = "pg-jsonb")
  @Column(name = "products", columnDefinition = "jsonb")
  private List<Long> productIds;

  @Column(name = "group_id")
  private Integer groupId;

  @Column(name = "tab_index")
  private Integer tabIndex;

  @Transient
  private Group group;
}
