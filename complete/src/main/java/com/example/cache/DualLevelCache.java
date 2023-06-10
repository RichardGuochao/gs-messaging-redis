package com.example.cache;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public class DualLevelCache implements Cache {
    private final Cache guavaCache;
    private final Cache redisCache;

    public DualLevelCache(Cache guavaCache, Cache redisCache) {
        this.guavaCache = guavaCache;
        this.redisCache = redisCache;
    }
    @Override
    public String getName() {
        return redisCache.getName();
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper = guavaCache.get(key);

        if (valueWrapper != null) {
            return valueWrapper;
        } else {
            valueWrapper = redisCache.get(key);
            if (valueWrapper != null) {
                guavaCache.put(key, valueWrapper);
            }
            return  valueWrapper;
        }
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper valueWrapper = this.get(key);
        return valueWrapper != null ? type.cast(valueWrapper.get()) : null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        try {
            return valueLoader.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        guavaCache.put(key, value);
        redisCache.put(key, value);
    }

    @Override
    public void evict(Object key) {
        guavaCache.evict(key);
        redisCache.evict(key);
    }

    @Override
    public void clear() {
        guavaCache.clear();
        redisCache.clear();
    }
}
