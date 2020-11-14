package com.chozoi.product.app.responses;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class Metadata {
  private int page;
  private int size;
  private long total;
  private long totalPages;

  public static Metadata of(Page page) {
    Metadata metadata = new Metadata();
    metadata.setSize(page.getSize());
    metadata.setTotal(page.getTotalElements());
    metadata.setPage(page.getNumber());
    metadata.setTotalPages(page.getTotalPages());
    return metadata;
  }
}
