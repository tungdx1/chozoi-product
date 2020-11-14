package com.chozoi.product;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableCaching
public class CacheConfiguration {

  public static final String CONFIG_HOME_FOR_USER = "ConfigHomeForUser";
  public static final String PRODUCT_FOR_BUYER = "ChozoiProductBuyer";

  @Bean
  public CacheManager cacheManager() {
    ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(CONFIG_HOME_FOR_USER);
    return cacheManager;
  }

  @CacheEvict(
      allEntries = true,
      value = {CONFIG_HOME_FOR_USER})
  @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 500)
  public void reportCacheEvict() {}
}
