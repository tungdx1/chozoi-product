package com.chozoi.product.domain.entities.postgres.product_ranking;

import com.chozoi.product.domain.entities.postgres.product_ranking.data.KeyWord;
import com.chozoi.product.domain.entities.postgres.product_ranking.data.Spotlight;
import com.chozoi.product.domain.entities.postgres.product_ranking.data.Stores;
import com.chozoi.product.domain.entities.postgres.product_ranking.data.Task;
import com.chozoi.product.domain.entities.postgres.product_ranking.types.BlockState;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "config_layout_block", schema = "configs")
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "pgsql-enum", typeClass = PostgreSQLEnumType.class)
public class ConfigLayoutBlock {
  @Id private Integer id;
  private String title;

  @Column(name = "title_link")
  private String titleLink;

  @Column(name = "title_screen")
  private String titleScreen;

  @Column(name = "product_size")
  private Integer productSize;

  private String site;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pg-enum")
  @Column(nullable = false, columnDefinition = "block_state", name = "state")
  private BlockState state;

  private String banner;

  @Column(name = "banner_link")
  private String bannerLink;

  @Column(name = "banner_screen")
  private String bannerScreen;

  @Column(name = "banner_mobile")
  private String bannerMobile;

  @Column(name = "type")
  private String type;

  @Type(type = "pg-jsonb")
  @Column(name = "keywords", columnDefinition = "jsonb")
  private List<KeyWord> keywords;

  @Type(type = "pg-jsonb")
  @Column(name = "spotlights", columnDefinition = "jsonb")
  private List<Spotlight> spotlights;

  @Type(type = "pg-jsonb")
  @Column(name = "tasks", columnDefinition = "jsonb")
  private List<Task> tasks;

  @Type(type = "pg-jsonb")
  @Column(name = "shops", columnDefinition = "jsonb")
  private List<String> shops;

  @Type(type = "pg-jsonb")
  @Column(name = "stores", columnDefinition = "jsonb")
  private Stores stores;

  @Column(name = "background")
  private String background;

  @Column(name = "sort")
  private Integer sort;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Transient private List<LayoutBlockProductGroup> productGroups;
}
