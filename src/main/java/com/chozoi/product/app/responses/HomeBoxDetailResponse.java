package com.chozoi.product.app.responses;


import com.chozoi.product.data.response.home.HomeData;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Data
@Log4j2
public class HomeBoxDetailResponse {
  private HomeData box;
}
