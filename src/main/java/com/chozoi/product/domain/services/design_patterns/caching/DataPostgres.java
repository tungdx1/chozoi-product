package com.chozoi.product.domain.services.design_patterns.caching;

import com.chozoi.product.domain.services.design_patterns.database_factory.DatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataPostgres extends DataAbstract {

  private DatabaseFactory.Type type = DatabaseFactory.Type.POSTGRES;

  @Autowired private DatabaseFactory databaseFactory;

  @Override
  protected List getConfig() throws Exception {
    return (List) databaseFactory.getFactory(type).getConfig().getData();
  }
}
