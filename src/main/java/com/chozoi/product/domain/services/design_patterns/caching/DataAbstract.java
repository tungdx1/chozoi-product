package com.chozoi.product.domain.services.design_patterns.caching;

import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlock;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Log4j2
public abstract class DataAbstract<T> {

  protected DataAbstract<T> dataAbstract;
  private List<T> response;

  public List<T> next(T tClass) throws Exception {
    List<T> data = this.specificData(tClass);
    if (Objects.nonNull(data)) response = data;
    else if (Objects.nonNull(dataAbstract)) response = dataAbstract.next(tClass);
    return response;
  }

  private List<T> specificData(T tClass) throws Exception {
    List<T> data = new ArrayList<>();
    if (tClass.equals(LayoutBlock.class)) data = (List<T>) this.getConfig();
    return data;
  }

  protected abstract List<LayoutBlock> getConfig() throws Exception;
}
