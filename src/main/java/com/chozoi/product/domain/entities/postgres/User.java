package com.chozoi.product.domain.entities.postgres;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user", schema = "accounts") // TODO : edit to products.shop_view
@Immutable
@Data
@NoArgsConstructor
public class User {
  @Id private Integer id;

  @Column(name = "email")
  private String email;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  @Fetch(FetchMode.SUBSELECT)
  private List<UserContact> contacts;
}
