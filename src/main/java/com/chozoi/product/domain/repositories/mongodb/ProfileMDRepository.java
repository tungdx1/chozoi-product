package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileMDRepository extends MongoRepository<Profile, Integer> {
}
