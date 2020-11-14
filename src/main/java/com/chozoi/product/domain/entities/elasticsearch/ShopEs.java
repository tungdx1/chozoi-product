package com.chozoi.product.domain.entities.elasticsearch;

import com.chozoi.product.domain.entities.types.ShopRanking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "chozoi_shops", type = "_doc")
public class ShopEs {
  @Id
  private Long id;

  @Field(type = FieldType.Text)
  private String contactName;

  private String email;
  private List<LocationEs> location;
  @Builder.Default
  private String tag = String.valueOf(ShopRanking.NORMAL);

  @Field(type = FieldType.Keyword)
  private String imgAvatarUrl;

  @Field(type = FieldType.Keyword)
  private String imgCoverUrl;

  @Field(type = FieldType.Text)
  private String name;

  @Field(type = FieldType.Text)
  private String freeShipStatus;

  @Field(type = FieldType.Keyword)
  private String pageUrl;

  @Field(type = FieldType.Keyword)
  private String type;

  private List<ProvindeEs> provinces;

  //  private Long updatedAt;
  //  private Long createdAt;
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
