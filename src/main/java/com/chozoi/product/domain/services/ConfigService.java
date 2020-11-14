package com.chozoi.product.domain.services;

import com.chozoi.product.app.ModelMapper;
import com.chozoi.product.app.responses.HomeBoxDetailResponse;
import com.chozoi.product.app.responses.HomeBoxListResponse;
import com.chozoi.product.data.response.ProductsPublicResponse;
import com.chozoi.product.data.response.ShopResponse;
import com.chozoi.product.data.response.home.HomeData;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.mongodb.Shop;
import com.chozoi.product.domain.entities.mongodb.config_home.*;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import com.chozoi.product.domain.repositories.mongodb.LayoutBlockGroupMongoRepository;
import com.chozoi.product.domain.repositories.mongodb.LayoutBlockMongoRepository;
import com.chozoi.product.domain.repositories.mongodb.ProductGroupMongoRepository;
import com.chozoi.product.domain.repositories.mongodb.ShopMDRepository;
import com.chozoi.product.domain.services.elasticsearch.ElasticsearchBuilderQuery;
import com.chozoi.product.domain.services.elasticsearch.ProductStaticService;
import com.chozoi.product.domain.utils.ProductUtils;
import io.sentry.Sentry;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class ConfigService {
  @Autowired private LayoutBlockMongoRepository layoutBlockMongoRepository;
  @Autowired private ShopMDRepository shopMDRepository;
  @Autowired private LayoutBlockGroupMongoRepository layoutBlockGroupMongoRepository;
  @Autowired private ProductGroupMongoRepository productGroupMongoRepository;
  @Autowired private ProductEsRepository productRepository;
  @Autowired private SuggestionService suggestionService;
  @Autowired private ModelMapper modelMapper;

  public HomeBoxListResponse boxList() {
    HomeBoxListResponse homeBoxListResponse = new HomeBoxListResponse();
    homeBoxListResponse.setBoxs(new ArrayList());
    try {
      List<LayoutBlockMongo> blockMongoList =
          layoutBlockMongoRepository.findBySiteAndStateOrderBySortAsc("home", "SHOW");
      List<HomeData> boxList = new ArrayList<>();
      for (LayoutBlockMongo blockMongo : blockMongoList) {
        HomeData homeData = null;
        switch (blockMongo.getType()) {
          case "SPOTLIGHT":
            homeData = buildSpotlight(blockMongo);
            break;
          case "KEYWORD":
            homeData = buildKeyword(blockMongo);
            break;
          case "OFFICIAL":
            homeData = buildOfficial(blockMongo, false);
            break;
          case "AUCTION":
          case "NORMAL":
          case "PRODUCT_SHOP":
          case "MARKET":
            homeData = buildProduct(blockMongo, false, 0);
            break;
        }

        if (homeData != null) {
          boxList.add(homeData);
        }
      }

      homeBoxListResponse.setBoxs(boxList);
    } catch (Exception e) {
      Sentry.capture(e);
      e.printStackTrace();
    }

    return homeBoxListResponse;
  }

  private HomeData buildProduct(LayoutBlockMongo block, boolean initData, Integer tabIndex) {
    HomeData.SuperData superData = new HomeData.SuperData();
    if (initData) {
      List<LayoutBlockGroupMongo> productGroups =
          layoutBlockGroupMongoRepository.findAllByBlockIdAndTabIndexAndStateNot(
              block.getId(), tabIndex, "DELETED");
      List<ProductEs> products = new ArrayList<>();
      List<Integer> groupIds = productGroups.stream().map(LayoutBlockGroupMongo::getGroupId).collect(Collectors.toList());
      Map<Integer, ProductGroup> group2Id = new HashMap<>();
      if (groupIds.size() > 0) {
        List<ProductGroupMongo> groupList = productGroupMongoRepository.findByIdIn(groupIds);
        group2Id = groupList.stream().collect(Collectors.toMap(ProductGroupMongo::getId, Function.identity()));
      }
      for (LayoutBlockGroupMongo productGroup : productGroups)
        try {
          if (group2Id.get(productGroup.getGroupId()) != null)
            productGroup.setGroup(group2Id.get(productGroup.getGroupId()));
          getProducts(productGroup, products, block.getProductSize());
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      int sizeAi = block.getProductSize() - products.size();
      boolean status = ProductStaticService.checkAliveAi(block);
      if (status)
        try {
          getProductsByAi(products, sizeAi);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      List<ProductsPublicResponse> data = modelMapper.productsEsToResponse(products);
      // sort
      this.sortProduct(block, data);
      superData.setProducts(data);
      if (block.getType().equals("PRODUCT_SHOP")) getShop(block, superData);
    }

    return buildBlock(block, superData);
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

  private void sortProduct(LayoutBlock config, List<ProductsPublicResponse> data) {
    String sortType = config.getProductSort();
    if (Objects.isNull(sortType)) return;
    if (sortType.equals("SELL_COUNT_DESC"))
      data.sort(Comparator.comparing(ProductsPublicResponse::getSoldQuantity));
    else if (sortType.equals("CREATED_AT_DESC"))
      data.sort(Comparator.comparing(ProductsPublicResponse::getCreatedAt));
  }

  private void getProducts(
      LayoutBlockGroupMongo productGroup, List<ProductEs> products, Integer size) throws Exception {
    ProductGroup group = productGroup.getGroup();
    if (group != null)
      switch (group.getType()) {
        case "PICK":
          pickProducts(productGroup, products, size, 0);
          break;
        case "CONFIG":
          getProductsConfigs(productGroup, products, size, 0);
          break;
        default:
          break;
      }
  }

  private void getProductsConfigs(
      LayoutBlockGroupMongo productGroup, List<ProductEs> products, Integer size, Integer offset) {
    size = Objects.isNull(size) || size <= 0 ? 10 : size;
    double rate = productGroup.getRate();
    int thisSize = (int) Math.round(size * rate / 100);
    PageRequest page = PageRequest.of(0, thisSize);
    ProductGroup group = productGroup.getGroup();
    ProductGroupMongo.Rules rules = group.getRules();
    List<ProductGroupMongo.Rules.Condition> conditions = rules.getProducts();
    List<Integer> categoryId = rules.getCategories();
    BoolQueryBuilder queryBuilder =
        boolQuery()
            .filter(termQuery("state", "PUBLIC"))
            .mustNot(termQuery("shop.isLock", true))
            .filter(
                boolQuery()
                    .should(termQuery("isQuantityLimited", false))
                    .should(rangeQuery("remainingQuantity").gt(0)));
    if (Objects.nonNull(conditions)) {
      List<String> types = new ArrayList<>();
      conditions.forEach(
          condition -> {
            if (condition.getType().equals("ALL")) {
              types.addAll(ProductUtils.PRODUCT_ALL_TYPE_STR);
            } else {
              if (!types.contains(condition.getType())) {
                types.add(condition.getType());
              }
            }
          });
      conditions.forEach(
          condition -> {
            ElasticsearchBuilderQuery.queryByType(types, queryBuilder);
            ElasticsearchBuilderQuery.queryByState(condition, queryBuilder);
            ElasticsearchBuilderQuery.queryByCondition(condition.getCondition(), queryBuilder);
          });
    }
    if (Objects.nonNull(categoryId))
      if (!categoryId.isEmpty())
        ElasticsearchBuilderQuery.queryByCategories(categoryId, queryBuilder);
    SortBuilder<FieldSortBuilder> sortBuilder =
        SortBuilders.fieldSort("createdAt").order(SortOrder.DESC);
    SearchQuery searchQuery =
        ElasticsearchBuilderQuery.searchQueryBuilder(queryBuilder, sortBuilder, page);
    Page<ProductEs> productEs = productRepository.search(searchQuery);
    List<ProductEs> productEsList = productEs.getContent();
    productEsList = ProductStaticService.removeAuctionStopped(productEs);
    products.addAll(productEsList);
    if (productEsList.size() != size) {
      offset++;
      pickProducts(productGroup, productEsList, size, offset);
    }
  }

  private void pickProducts(
      LayoutBlockGroupMongo productGroup, List<ProductEs> products, Integer size, int offset) {
    size = Objects.isNull(size) || size <= 0 ? 10 : size;
    double rate = productGroup.getRate();
    int thisSize = (int) Math.round(size * rate / 100);
    List<Long> productIds = productGroup.getProducts();
    if (productIds == null) return;
    if ((offset * thisSize > productIds.size())) return;
    List<Long> ids =
        productIds.size() > thisSize ? productIds.subList(offset * thisSize, thisSize) : productIds;
    List<ProductEs> productEs = productRepository.findByIdInAndState(ids, "PUBLIC");
    productEs = ProductStaticService.removeAuctionStopped(productEs);
    products.addAll(productEs);
    if (productEs.size() != size) {
      offset++;
      pickProducts(productGroup, products, size, offset);
    }
  }

  private void getProductsByAi(List<ProductEs> products, Integer size) throws Exception {
    size = Objects.isNull(size) || size <= 0 ? 10 : size;
    PageRequest page = PageRequest.of(0, size);
    Page<ProductEs> productEs = suggestionService.forHome(null, page);
    products.addAll(productEs.getContent());
  }

  private HomeData buildOfficial(LayoutBlockMongo block, Boolean initData) {
    HomeData.SuperData superData = new HomeData.SuperData();
    if (initData) {
      LayoutBlockMongo.Stores stores = block.getStores();
      if (stores != null) {
        HomeData.OfficialStore data = new HomeData.OfficialStore();
        List<LayoutBlock.Stores.OfficialStore> storesPriority = stores.getPriority();
        List<LayoutBlock.Stores.OfficialStore> storesNonPriority = stores.getNonPriority();
        List<LayoutBlock.Stores.Banner> storesBannerList = stores.getBannerList();
        List<Integer> storeIds = new ArrayList<>();
        if (storesPriority != null) {
          storesPriority.forEach(
              officialStore -> {
                if (officialStore.getShowLogo().equals("SYSTEM"))
                  storeIds.add(officialStore.getId());
              });
        }
        if (storesNonPriority != null) {
          storesNonPriority.forEach(
              officialStore -> {
                if (officialStore.getShowLogo().equals("SYSTEM"))
                  storeIds.add(officialStore.getId());
              });
        }

        Map<Integer, Shop> shop2Id = new HashMap<>();
        if (storeIds.size() > 0) {
          List<Shop> shops = shopMDRepository.findByIdIn(storeIds);
          shop2Id = shops.stream().collect(Collectors.toMap(Shop::getId, Function.identity()));
        }

        List<HomeData.Store> priority = new ArrayList<>();
        if (storesPriority != null) {
          Map<Integer, Shop> finalShop2Id = shop2Id;
          storesPriority.forEach(
              store -> {
                HomeData.Store store1 = new HomeData.Store();

                String name = "@Not Found";
                Shop shop = finalShop2Id.get(store.getId());
                if (shop != null) {
                  name = shop.getName();
                }
                store1.setId(store.getId());
                store1.setName(name);
                store1.setTitle(store.getTitle());
                handleShopBanner(store1, store, shop);
                handleShopLogo(store1, store, shop);

                priority.add(store1);
              });
        }

        data.setPriority(priority);

        List<HomeData.Store> nonPriority = new ArrayList<>();
        if (storesNonPriority != null) {
          Map<Integer, Shop> finalShop2Id = shop2Id;
          storesNonPriority.forEach(
              store -> {
                HomeData.Store store1 = new HomeData.Store();

                String name = "@Not Found";
                Shop shop = finalShop2Id.get(store.getId());
                if (shop != null) {
                  name = shop.getName();
                }
                store1.setId(store.getId());
                store1.setName(name);
                store1.setTitle(store.getTitle());
                handleShopBanner(store1, store, shop);
                handleShopLogo(store1, store, shop);

                nonPriority.add(store1);
              });
        }
        data.setNonPriority(nonPriority);
        data.setBannerList(storesBannerList);
        superData.setOfficialStores(data);
      }
    }

    return buildBlock(block, superData);
  }

  private void handleShopBanner(
      HomeData.Store storeHome, LayoutBlock.Stores.OfficialStore store, Shop shop) {
    String banner, bannerApp;
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
        assert shop != null;
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

    storeHome.setBanner(banner);
    storeHome.setBannerApp(bannerApp);
  }

  private void handleShopLogo(
      HomeData.Store storeHome, LayoutBlock.Stores.OfficialStore store, Shop shop) {
    String logo, logoApp;
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
        assert shop != null;
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
    storeHome.setAvatar(logo);
    storeHome.setAvatarApp(logoApp);
  }

  private HomeData buildKeyword(LayoutBlockMongo block) {
    HomeData.SuperData superData = new HomeData.SuperData();
    List<LayoutBlock.Keyword> keyWords = block.getKeyWords();
    if (keyWords != null) {
      List<HomeData.KeyWord> data = new ArrayList<>();
      keyWords.forEach(
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
      superData.setKeywords(data);
    }
    return buildBlock(block, superData);
  }

  private HomeData buildSpotlight(LayoutBlockMongo block) {
    HomeData.SuperData superData = new HomeData.SuperData();
    List<LayoutBlockMongo.Spotlight> spotlights = block.getSpotlights();
    if (spotlights != null) {
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
      superData.setSpotlights(data);
    }
    return buildBlock(block, superData);
  }

  private HomeData buildBlock(LayoutBlockMongo block, HomeData.SuperData superData) {
    Integer sort = ObjectUtils.defaultIfNull(block.getSort(), 0);
    List<LayoutBlock.Task> tasks = block.getTasks();
    if (Objects.nonNull(tasks)) for (int i = 0; i < tasks.size(); i++) tasks.get(i).setId(i);
    return HomeData.builder()
        .id(block.getId())
        .title(block.getTitle())
        .titleLink(block.getTitleLink())
        .titleScreen(block.getTitleScreen())
        .tab("HOT")
        .sort(sort)
        .template(block.getType())
        .typeData(block.getType())
        .bannerScreen(block.getBanner())
        .data(superData)
        .tasks(tasks)
        .banner(block.getBanner())
        .bannerLink(block.getBannerLink())
        .bannerApp(block.getBannerMobile())
        .background(block.getBackground())
        .build();
  }

  public HomeBoxDetailResponse boxDetail(Integer blockId, Integer taskId) {
    HomeBoxDetailResponse homeBoxDetailResponse = new HomeBoxDetailResponse();
    try {
      LayoutBlockMongo block = layoutBlockMongoRepository.findById(blockId).orElse(null);
      if (block != null) {
        HomeData homeData = null;
        switch (block.getType()) {
          case "SPOTLIGHT":
            homeData = buildSpotlight(block);
            break;
          case "KEYWORD":
            homeData = buildKeyword(block);
            break;
          case "OFFICIAL":
            homeData = buildOfficial(block, true);
            break;
          case "AUCTION":
          case "NORMAL":
          case "PRODUCT_SHOP":
          case "MARKET":
            homeData = buildProduct(block, true, taskId);
            break;
        }

        homeBoxDetailResponse.setBox(homeData);
      }

    } catch (Exception e) {
      Sentry.capture(e);
      e.printStackTrace();
    }

    return homeBoxDetailResponse;
  }
}
