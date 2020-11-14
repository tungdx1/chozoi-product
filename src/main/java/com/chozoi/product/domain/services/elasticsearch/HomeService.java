package com.chozoi.product.domain.services.elasticsearch;

import com.chozoi.product.CacheConfiguration;
import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.data.response.ProductImageResponse;
import com.chozoi.product.data.response.ProductsPublicResponse;
import com.chozoi.product.data.response.ShopResponse;
import com.chozoi.product.data.response.home.HomeData;
import com.chozoi.product.domain.entities.elasticsearch.ImageEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.mongodb.Shop;
import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlock;
import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlockGroupMongo;
import com.chozoi.product.domain.entities.mongodb.config_home.LayoutBlockMongo;
import com.chozoi.product.domain.entities.postgres.product_ranking.ConfigLayoutBlock;
import com.chozoi.product.domain.entities.postgres.product_ranking.Group;
import com.chozoi.product.domain.entities.postgres.product_ranking.LayoutBlockProductGroup;
import com.chozoi.product.domain.factories.BeanMapper;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import com.chozoi.product.domain.repositories.postgres.ConfigLayoutBlockRepository;
import com.chozoi.product.domain.repositories.postgres.LayoutBlockGroupRepository;
import com.chozoi.product.domain.repositories.postgres.ProductGroupRepository;
import com.chozoi.product.domain.services.SuggestionService;
import com.chozoi.product.domain.services.async.AsyncProductService;
import com.chozoi.product.domain.services.design_patterns.caching.DataChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HomeService extends BaseElasticService {
  @Autowired protected ProductEsRepository productRepository;
  @Autowired protected LayoutBlockGroupRepository layoutBlockGroupRepository;
  @Autowired protected ProductGroupRepository productGroupRepository;
  @Autowired protected BeanMapper beanMapper;
  @Autowired protected ModelMapper modelMapper;

  @Value("${environment}")
  private String cluster;

  private String site = "home";
  @Autowired private SuggestionService suggestionService;

  @Autowired private AsyncProductService asyncProductService;
  @Autowired private ProductElasticService productElasticService;
  @Autowired private ConfigLayoutBlockRepository configLayoutBlockRepository;
  @Autowired private DataChain dataChain;

  public Page<ProductEs> suggestion(String userId, Pageable pageable) {
    Page<ProductEs> products;
    int size = pageable.getPageSize();
    int pageNumber = pageable.getPageNumber();
    Pageable pageableCustom = PageRequest.of(pageNumber, size);
    if (cluster.equals("PROD")) try {
        products = suggestionService.forHome(userId, pageableCustom);
    } catch (Exception e) {
        products = productElasticService.getProductNew(pageableCustom);
    }
    else products = productElasticService.getProductNew(pageableCustom);
    return products;
  }

  /**
   * config home
   *
   * @param userId
   * @return
   * @throws Exception
   */
  @Cacheable(value = CacheConfiguration.CONFIG_HOME_FOR_USER, key = "#userId")
  public List<HomeData> homeConfig(String userId) throws Exception {
    // product
    List<HomeData> response = new ArrayList<>();
    List<LayoutBlock> configs = dataChain.getData().next(LayoutBlock.class);
    // spotlight
    getTypeSpotlight(configs, userId, response);
    getTypeProduct(configs, userId, 0, response);
    getTypeKeyWord(configs, userId, response);
    getTypeOfficital(configs, userId, response);
    response.forEach(
        res -> {
          if (res.getData().getProducts() != null) res.getData()
                  .getProducts()
                  .forEach(
                          product -> {
                              product.getImages().sort(Comparator.comparing(ProductImageResponse::getSort));

                              try {
                                  if (product.getShop().getFreeShipStatus().equals("OFF"))
                                      product.setFreeShipStatus(false);
                              } catch (Exception e) {
                                  product.setFreeShipStatus(false);
                              }
                          });
        });
    response.sort(Comparator.comparing(HomeData::getSort));
    return response;
  }

  private void getTypeOfficital(List<LayoutBlock> configs, String userId, List<HomeData> response) {
    List<LayoutBlock> list =
        configs.stream()
            .filter(configLayoutBlock -> configLayoutBlock.getType().equals("OFFICIAL"))
            .collect(Collectors.toList());
    list.forEach(
        config -> {
          // builder data
          LayoutBlockMongo.Stores stores = config.getStores();
          HomeData.OfficialStore data = new HomeData.OfficialStore();
          if (stores != null) {
              // priority
              List<LayoutBlock.Stores.OfficialStore> storesPriority = stores.getPriority();
              List<LayoutBlock.Stores.OfficialStore> storesNonPriority = stores.getNonPriority();
              List<LayoutBlock.Stores.Banner> storesBannerList = stores.getBannerList();
              List<Integer> priorityId = new ArrayList<>();
              if (storesPriority != null) {
                  priorityId = storesPriority.stream()
                          .map(LayoutBlock.Stores.OfficialStore::getId)
                          .collect(Collectors.toList());
              }
              List<Integer> nonPriorityId = new ArrayList<>();
              if (storesNonPriority != null) {
                  nonPriorityId = storesNonPriority.stream()
                          .map(LayoutBlock.Stores.OfficialStore::getId)
                          .collect(Collectors.toList());
              }
              priorityId.addAll(nonPriorityId);
              // get store
              // TODO handler get logoshop and check shop state
              List<Shop> shops = shopMDRepository.findByIdIn(priorityId);
              List<HomeData.Store> priority = new ArrayList<>();
              storesPriority.forEach(
                      store -> {
                          List<Shop> shopEsList =
                                  shops.stream()
                                          .filter(shopEs -> shopEs.getId().equals(store.getId()))
                                          .collect(Collectors.toList());
                          String name = shopEsList.size() > 0 ? shopEsList.get(0).getName() : "@Not Found";
                          HomeData.Store store1 = new HomeData.Store();
                          store1.setId(store.getId());
                          store1.setName(name);
                          store1.setTitle(store.getTitle());
                          handleShopLogo(store1, store, shops);
                          handleShopBanner(store1, store, shops);
                          priority.add(store1);
                      });
              data.setPriority(priority);
              // non priority
              List<HomeData.Store> nonPriority = new ArrayList<>();
              if (storesNonPriority != null) {
                  storesNonPriority.forEach(
                          store -> {
                              Long id = (long) store.getId();
                              List<Shop> shopEsList =
                                      shops.stream()
                                              .filter(shopEs -> shopEs.getId().equals(store.getId()))
                                              .collect(Collectors.toList());
                              String name = shopEsList.size() > 0 ? shopEsList.get(0).getName() : "@Not Found";
                              HomeData.Store store1 = new HomeData.Store();
                              store1.setId(store.getId());
                              store1.setName(name);
                              store1.setTitle(store.getTitle());
                              handleShopBanner(store1, store, shops);
                              handleShopLogo(store1, store, shops);
                              nonPriority.add(store1);
                          });
              }

              data.setNonPriority(nonPriority);
              data.setBannerList(storesBannerList);
              HomeData.SuperData superData = new HomeData.SuperData();
              superData.setOfficialStores(data);
              HomeData homeData = buidleData(config, superData);
              response.add(homeData);
          }
        });
  }

  private void handleShopImageForNonPriority(HomeData.Store store1) {
    List<ProductEs> productEsList =
        productRepository.findByShop_IdAndState(store1.getId(), "PUBLIC");
    if (Objects.isNull(productEsList) || productEsList.size() == 0) return;
    ProductEs productEs = productEsList.get(0);
    List<ImageEs> imageEs = productEs.getImages();

    if (Objects.nonNull(imageEs)) if (imageEs.size() > 0) {
        imageEs.sort(Comparator.comparing(ImageEs::getSort));
        int i = 0;
        String img = "";
        while (i < imageEs.size()) {
            ImageEs image = imageEs.get(i);
            if (Objects.nonNull(image.getImageUrl())) {
                img = image.getImageUrl();
                if (Objects.nonNull(img)) {
                    List<String> arrOfStr = Arrays.asList(img.split("product", 2));
                    if (arrOfStr.size() == 2) img = arrOfStr.get(0) + "product/180x180" + arrOfStr.get(1);
                }
                break;
            }
        }
        store1.setBanner(img);
        store1.setBannerApp(img);
    }
  }

  /**
   * handle image for official stores
   *
   * @param store1 data response
   * @param store data config
   * @param shops data init by shop
   */
  private void handleShopLogo(
      HomeData.Store store1, LayoutBlock.Stores.OfficialStore store, List<Shop> shops) {
    String logo, logoApp;
    Shop shop =
        shops.stream()
            .filter(shop1 -> shop1.getId().equals(store.getId()))
            .findFirst()
            .orElse(null);
    if (store.getShowLogo().equals("SHOP")) {
      try {
        assert shop != null;
        logo = shop.getOfficialTemplate().getDesktopLogo();
      } catch (Exception e) {
        logo = "";
      }
      try {
        logoApp = shop.getOfficialTemplate().getMobileLogo();
      } catch (Exception e) {
        logoApp = "";
      }
    } else {
      try {
        logo = store.getLogo();
      } catch (Exception e) {
        logo = "";
      }
      try {
        logoApp = store.getLogoMobile();
      } catch (Exception e) {
        logoApp = "";
      }
    }
    store1.setAvatar(logo);
    store1.setAvatarApp(logoApp);
  }

  /**
   * handle image for official stores
   *
   * @param store1 data response
   * @param store data config
   * @param shops data init by shop
   */
  private void handleShopBanner(
      HomeData.Store store1, LayoutBlock.Stores.OfficialStore store, List<Shop> shops) {
    String banner, bannerApp;
    Shop shop =
        shops.stream()
            .filter(shop1 -> shop1.getId().equals(store.getId()))
            .findFirst()
            .orElse(null);
    if (store.getShowBaner().equals("SHOP")) {
      try {
        assert shop != null;
        banner = shop.getOfficialTemplate().getMainDesktopBanner();
      } catch (Exception e) {
        banner = "";
      }
      try {
        bannerApp = shop.getOfficialTemplate().getMainMobileBanner();
      } catch (Exception e) {
        bannerApp = "";
      }
    } else {
      try {
        banner = store.getBanner();
      } catch (Exception e) {
        banner = "";
      }
      try {
        bannerApp = store.getBannerMobile();
      } catch (Exception e) {
        bannerApp = "";
      }
    }

    store1.setBanner(banner);
    store1.setBannerApp(bannerApp);
  }

  private void getTypeKeyWord(List<LayoutBlock> configs, String userId, List<HomeData> response) {
    List<LayoutBlock> list =
        configs.stream()
            .filter(configLayoutBlock -> configLayoutBlock.getType().equals("KEYWORD"))
            .collect(Collectors.toList());
    ;
    list.forEach(
        config -> {
          // builder data
          List<LayoutBlock.Keyword> keyWords = config.getKeyWords();
          List<HomeData.KeyWord> data = new ArrayList<>();
          if (Objects.nonNull(keyWords)) keyWords.forEach(
                  keyWord -> {
                      HomeData.KeyWord keyWord1 = new HomeData.KeyWord();
                      keyWord1.setKey(keyWord.getTitle());
                      keyWord1.setImageUrl(keyWord.getImage());
                      keyWord1.setImageUrlApp(keyWord.getImage());
                      keyWord1.setLink(keyWord.getLink());
                      keyWord1.setLinkId(keyWord.getLinkId());
                      keyWord1.setScreen(keyWord.getScreen());
                      keyWord1.setSearchValue(keyWord.getNumberSearch());
                      data.add(keyWord1);
                  });
          HomeData.SuperData superData = new HomeData.SuperData();
          java.util.Collections.shuffle(data);
          superData.setKeywords(data);
          HomeData homeData = buidleData(config, superData);
          response.add(homeData);
        });
  }

  private void getTypeProduct(
      List<LayoutBlock> configs, String userId, Integer index, List<HomeData> response) {
    List<LayoutBlock> list =
        configs.stream()
            .filter(
                configLayoutBlock ->
                    configLayoutBlock.getType().equals("AUCTION")
                        || configLayoutBlock.getType().equals("NORMAL")
                        || configLayoutBlock.getType().equals("PRODUCT_SHOP")
                        || configLayoutBlock.getType().equals("MARKET"))
            .collect(Collectors.toList());
    list.forEach(
        config -> {
          // builder data
          List<LayoutBlock.Task> tasks = config.getTasks();
          if (Objects.nonNull(tasks)) for (int i = 0; i < tasks.size(); i++) tasks.get(i).setId(i);
          List<LayoutBlockGroupMongo> productGroups = config.getProductGroups();
          List<LayoutBlockGroupMongo> productGroups1 =
              productGroups.stream()
                  .filter(productGroup1 -> productGroup1.getTabIndex().equals(index))
                  .collect(Collectors.toList());
          List<ProductEs> products = new ArrayList<>();
          for (LayoutBlockGroupMongo productGroup : productGroups1)
              try {
                  getProducts(productGroup, userId, products, config.getProductSize());
              } catch (Exception exception) {
                  exception.printStackTrace();
              }
          int sizeAi = config.getProductSize() - products.size();
          boolean status = ProductStaticService.checkAliveAi(config);
          if (status) try {
              getProductsByAi(userId, products, sizeAi);
          } catch (Exception exception) {
              exception.printStackTrace();
          }
          List<ProductsPublicResponse> data = modelMapper.productsEsToResponse(products);
          // sort
          this.sortProduct(config, data);
          HomeData.SuperData superData = new HomeData.SuperData();
          superData.setProducts(data);
          if (config.getType().equals("PRODUCT_SHOP")) getShop(config, superData);
          HomeData homeData = buidleData(config, superData);
          homeData.setTasks(tasks);
          response.add(homeData);
        });
  }

  private void sortProduct(LayoutBlock config, List<ProductsPublicResponse> data) {
    String sortType = config.getProductSort();
    if (Objects.isNull(sortType)) return;
    if (sortType.equals("SELL_COUNT_DESC")) data.sort(Comparator.comparing(ProductsPublicResponse::getSoldQuantity));
    else if (sortType.equals("CREATED_AT_DESC"))
        data.sort(Comparator.comparing(ProductsPublicResponse::getCreatedAt));
  }

  private void getShop(LayoutBlock config, HomeData.SuperData superData) {
    List<Long> shopStrIds = config.getShops();
    if (shopStrIds == null) return;
    List<Integer> shopIds = new ArrayList<>();
    shopStrIds.forEach(
        id -> {
          Integer idq = Math.toIntExact(id);
          shopIds.add(idq);
        });

    List<Shop> shopEs = shopMDRepository.findByIdIn(shopIds);
    List<ShopResponse> shopResponses = modelMapper.shopMdToResponse(shopEs);
    superData.setShops(shopResponses);
  }

  private void getTypeSpotlight(List<LayoutBlock> configs, String userId, List<HomeData> response) {
    List<LayoutBlock> list =
        configs.stream()
            .filter(configLayoutBlock -> configLayoutBlock.getType().equals("SPOTLIGHT"))
            .collect(Collectors.toList());
    ;
    list.forEach(
        config -> {
          // builder data
          List<LayoutBlockMongo.Spotlight> spotlights = config.getSpotlights();
          List<HomeData.Spotlight> data = new ArrayList<>();
          spotlights.forEach(
              spotlight -> {
                HomeData.Spotlight spotlight1 = new HomeData.Spotlight();
                spotlight1.setKey(spotlight.getTitle());
                spotlight1.setLink(spotlight.getLink());
                spotlight1.setLinkId(spotlight.getLinkId());
                spotlight1.setScreen(spotlight.getScreen());
                spotlight1.setImageUrl(spotlight.getImage());
                spotlight1.setImageUrlApp(spotlight.getImage());
                data.add(spotlight1);
              });
          HomeData.SuperData superData = new HomeData.SuperData();
          superData.setSpotlights(data);
          HomeData homeData = buidleData(config, superData);
          response.add(homeData);
        });
  }

  private void addRelation(
      List<ConfigLayoutBlock> configs, List<LayoutBlockProductGroup> layouts, List<Group> groups) {
    layouts.forEach(
        layout -> {
          List<Group> groupsList =
              groups.stream()
                  .filter(group -> group.getId().equals(layout.getGroupId()))
                  .collect(Collectors.toList());
          if (groupsList.size() > 0) {
            Group group = groupsList.get(0);
            layout.setGroup(group);
          }
        });
    configs.forEach(
        config -> {
          List<LayoutBlockProductGroup> layoutList =
              layouts.stream()
                  .filter(layout -> layout.getBlockId().equals(config.getId()))
                  .collect(Collectors.toList());
          config.setProductGroups(layoutList);
        });
  }

  /**
   * filter product by taskId;
   *
   * @param userId
   * @param configId
   * @param taskId
   * @return
   * @throws Exception
   */
  public List<ProductsPublicResponse> findByTaskHome(
      String userId, Integer configId, Integer taskId) throws Exception {
    List<HomeData> response = new ArrayList<>();
    List<LayoutBlock> configs = dataChain.getData().next(LayoutBlock.class);
    List<LayoutBlock> config =
        configs.stream()
            .filter(config1 -> config1.getId().equals(configId))
            .collect(Collectors.toList());
    getTypeProduct(config, userId, taskId, response);
    List<ProductsPublicResponse> products = response.get(0).getData().getProducts();
    products.forEach(
        product -> {
          try {
            if (product.getShop().getFreeShipStatus().equals("OFF")) product.setFreeShipStatus(false);
          } catch (Exception e) {
            product.setFreeShipStatus(false);
          }
        });
    return products;
  }
}
