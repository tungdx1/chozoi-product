package com.chozoi.product.app.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BlogHomeResponse {
  private String title;
  private String description;
  private String imageUrl;
  private String link;
  private LocalDateTime createdAt;
}
