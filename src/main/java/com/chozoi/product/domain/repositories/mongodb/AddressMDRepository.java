package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.Address;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AddressMDRepository extends MongoRepository<Address, Integer> {
}
