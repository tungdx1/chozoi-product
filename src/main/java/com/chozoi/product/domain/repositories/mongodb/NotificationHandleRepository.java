package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.NotificationHandle;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationHandleRepository extends MongoRepository<NotificationHandle, String> {}
