package com.chozoi.product.domain.services.design_patterns.caching;

import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlock;
import com.chozoi.product.domain.services.design_patterns.database_factory.DatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataMongo extends DataAbstract {
  private DatabaseFactory.Type type = DatabaseFactory.Type.MONGODB;

  @Autowired private DatabaseFactory databaseFactory;

  @Override
  protected List getConfig() throws Exception {
    return (List<LayoutBlock>) databaseFactory.getFactory(type).getConfig().getData();
  }
}
