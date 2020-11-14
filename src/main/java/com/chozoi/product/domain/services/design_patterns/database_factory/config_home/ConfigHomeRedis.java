package com.chozoi.product.domain.services.design_patterns.database_factory.config_home;

import com.chozoi.product.domain.entities.redis.config_home.LayoutBlockRedis;
import com.chozoi.product.domain.entities.redis.config_home.ProductGroupRedis;
import com.chozoi.product.domain.repositories.redis.LayoutBlockRedisRepository;
import com.chozoi.product.domain.repositories.redis.ProductGroupRedisRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ConfigHomeRedis implements ConfigHome {
  @Autowired private LayoutBlockRedisRepository repository;
  @Autowired private ProductGroupRedisRepository groupRedisRepository;

  @Override
  public Object getData() {
    List<LayoutBlockRedis> list = new ArrayList<>();
    Iterable<LayoutBlockRedis> layoutBlockRedisList = repository.findAll();
    List<ProductGroupRedis> groupRedis = getGroup();
    for (LayoutBlockRedis layoutBlockRedis : layoutBlockRedisList) list.add(layoutBlockRedis);
    addGroup(list, groupRedis);
    if (list.size() == 0) return null;
    if (Objects.isNull(list.get(0))) return null;
    return list;
  }

  private List<ProductGroupRedis> getGroup() {
    Iterable<ProductGroupRedis> groups = groupRedisRepository.findAll();
    List<ProductGroupRedis> groupList = new ArrayList<>();
    groups.forEach(groupList::add);
    return groupList;
  }

  private void addGroup(List<LayoutBlockRedis> list, List<ProductGroupRedis> groupRedis) {
    ModelMapper modelMapper = new ModelMapper();
    list.forEach(
        layoutBlockRedis -> {
          if (Objects.nonNull(layoutBlockRedis)) if (layoutBlockRedis.getProductGroups() != null) layoutBlockRedis
                  .getProductGroups()
                  .forEach(
                          layoutBlockGroup -> {
                              Integer groupId = layoutBlockGroup.getGroupId();
                              List<ProductGroupRedis> groupRedis1 =
                                      groupRedis.stream()
                                              .filter(group -> group.getId().equals(groupId))
                                              .collect(Collectors.toList());
                              if (!groupRedis1.isEmpty()) layoutBlockGroup.setGroup(groupRedis1.get(0));
                          });
        });
  }
}
