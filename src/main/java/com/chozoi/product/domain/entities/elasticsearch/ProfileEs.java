package com.chozoi.product.domain.entities.elasticsearch;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

@Data
@Document(indexName = "chozoi_accounts_profiles", type = "_doc")
public class ProfileEs {
  @Id private Integer id;
  private String name;
  private String avatarUrl;
}
