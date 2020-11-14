package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.UserRole;
import com.chozoi.product.domain.entities.postgres.types.UserState;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "comment_user", schema = "comments")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class UserComment {
  @Id
  @Column(name = "id")
  private long id;

  @Column(name = "state", nullable = false, columnDefinition = "user_state")
  @Enumerated(value = EnumType.STRING)
  @Type(type = "pgsql_enum")
  private UserState state;

  @Column(name = "role", nullable = false, columnDefinition = "user_role")
  @Enumerated(value = EnumType.STRING)
  @Type(type = "pgsql_enum")
  private UserRole role;
}
