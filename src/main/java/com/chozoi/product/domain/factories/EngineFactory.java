package com.chozoi.product.domain.factories;

import com.chozoi.product.data.response.RecursiveData;
import com.chozoi.product.domain.entities.suggestion.Item;
import com.chozoi.product.domain.entities.suggestion.ItemScores;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.apache.predictionio.sdk.java.EngineClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class EngineFactory {
  @Value("${predictionio.engineUrl}")
  private String engineUrl;

  @Value("${predictionio.accessKey}")
  private String accessKey;

  private EngineClient engineClient;

  private String state = "PUBLIC";

  public final RecursiveData getIds(String userId, int i) throws Exception {
    boolean status = false;
    int coef = 20;
    JsonObject response;
    response = engineClient.sendQuery(ImmutableMap.<String, Object>of(
            "user", userId,
            "num", coef * i));

    Gson gson = new Gson();
    ItemScores itemScores = gson.fromJson(response, ItemScores.class);
    List<Item> items = itemScores.getItemScores();
    List<Long> ids = new ArrayList<>();
    List<Item> itemList;
    if ( items.size() == coef * i ) itemList = items.subList(coef * (i - 1), coef * i);
    else {
      itemList = items.subList(coef * (i - 1), items.size());
      status = true;
    }
    itemList.forEach(item -> {
      try {
        ids.add(Long.parseLong(String.valueOf(item.getItem())));
      } catch (Exception e) {
      }
    });

    return new RecursiveData(status, ids);
  }


  @PostConstruct
  private void init() {
    engineClient = new EngineClient(engineUrl);
  }

  ;

}
