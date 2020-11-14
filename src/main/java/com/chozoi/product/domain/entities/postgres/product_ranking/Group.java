package com.chozoi.product.domain.entities.postgres.product_ranking;

import com.chozoi.product.domain.entities.postgres.product_ranking.data.Rules;
import com.chozoi.product.domain.entities.postgres.product_ranking.types.GroupType;
import com.chozoi.product.domain.utils.GenericArrayUserType;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity

@Table(name = "group", schema = "products")
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "pg-array", typeClass = GenericArrayUserType.class)
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class Group {
  @Id
  private Integer id;
  private String name;

  @Type(type = "pg-jsonb")
  @Column(name = "rules", columnDefinition = "jsonb")
  private Rules rules;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pg-enum")
  @Column(nullable = false, columnDefinition = "group_type", name = "type")
  private GroupType type;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
