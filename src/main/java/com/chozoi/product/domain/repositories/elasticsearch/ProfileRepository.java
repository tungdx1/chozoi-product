package com.chozoi.product.domain.repositories.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.ProfileEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProfileRepository extends ElasticsearchRepository<ProfileEs, Integer> {}
