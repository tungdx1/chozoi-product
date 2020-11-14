package com.chozoi.product.domain.services.design_patterns.database_factory.config_home;

import org.springframework.stereotype.Service;

@Service
public class ConfigHomePostgres implements ConfigHome {
  @Override
  //TODO : lay config trong postgresql
  public Object getData() {
    return "hehe";
  }
}
