package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.utils.GenericArrayUserType;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "notification_segment", schema = "notifications")
@TypeDef(name = "pg-enum", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "pg-array", typeClass = GenericArrayUserType.class)
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
public class NotificationSegment {
  @Id private Integer id;
  private String name;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pgsql_enum")
  @Column(nullable = false, columnDefinition = "object_type", name = "object_type")
  private SegmentObjectType objectType;

  @Column(name = "object_value")
  private String objectValue;

  @Column(name = "time_remain")
  private Integer timeRemain;

  @Column(name = "time_pending")
  private Integer timePending;
}
