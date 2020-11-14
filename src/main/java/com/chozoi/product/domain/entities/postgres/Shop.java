package com.chozoi.product.domain.entities.postgres;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "shop", schema = "shops") // TODO : edit to products.shop_view
@Immutable
@Data
@NoArgsConstructor
public class Shop extends com.chozoi.product.domain.entities.abstracts.Shop {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name")
  private String name;

  @Column(name = "user_id")
  private Integer userId;
}
