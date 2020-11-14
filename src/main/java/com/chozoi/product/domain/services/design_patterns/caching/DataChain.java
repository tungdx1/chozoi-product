package com.chozoi.product.domain.services.design_patterns.caching;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Using Chain of Responsibility */
@Log4j2
@Service
@Data
public class DataChain {

//  @Autowired private DataRedis redis;
  @Autowired private DataMongo mongo;
  @Autowired private DataPostgres postgres;

  /**
   * select db
   *
   * @return
   */
  public DataAbstract getData() {
    mongo.setDataAbstract(postgres);
    return mongo;
  }
}
