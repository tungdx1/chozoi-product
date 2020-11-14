package com.chozoi.product.domain.services.design_patterns.database_factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * singleton and abstract factory
 */
@Service
public class DatabaseFactory {

    @Autowired
    private RedisFactory redisFactory;

    @Autowired
    private MongoFactory mongoFactory;

    @Autowired
    private PostgresFactory postgresFactory;

    private DatabaseFactory() {
    }

    public static DatabaseFactory getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public DatabaseAbstractFactory getFactory(Type type) throws Exception {
        switch (type) {
            case REDIS:
                return redisFactory;
            case MONGODB:
                return mongoFactory;
            case POSTGRES:
                return postgresFactory;
            default:
                throw new Exception("Database not found");
        }
    }

    public enum Type {
        REDIS,
        MONGODB,
        POSTGRES
    }

    private static class SingletonHelper {
        private static final DatabaseFactory INSTANCE = new DatabaseFactory();
    }
}
