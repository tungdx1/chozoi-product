package com.chozoi.product.domain.entities.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "user_contact", schema = "accounts") // TODO : edit to products.shop_view
@Immutable
@Data
@NoArgsConstructor
public class UserContact {
  @Id private Integer id;

  @Column(name = "name")
  private String name;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private User user;
}
