package com.chozoi.product.app.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Response {
  private LocalDateTime timestamp;
  private Object message;
  private String details;
  private Integer status;

  public Response(Object message, String details, Integer status) {
    this.timestamp = LocalDateTime.now();
    this.message = message;
    this.details = details;
    this.status = status;
  }
}
