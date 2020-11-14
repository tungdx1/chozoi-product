package com.chozoi.product.domain.entities.postgres;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@Table(name = "shops", schema = "official_store_template")
@Where(clause = "state='PUBLIC'")
public class OfficialStoreTemplate {
  @Id private Integer id;

  @Column(name = "shop_id")
  private Integer shopId;

  @Column(name = "logo")
  private String logo;

  @Column(name = "main_banner")
  private String mainBanner;

  @Column(name = "sub_bannner")
  private String subBanner;
}
