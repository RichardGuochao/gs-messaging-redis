package com.example.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;

public class DualLevelCacheManager implements CacheManager {
    private final CacheManager guavaCacheManager;
    private final CacheManager redisCacheManager;

    public DualLevelCacheManager(CacheManager guavaCacheManager, CacheManager redisCacheManager) {
        this.guavaCacheManager = guavaCacheManager;
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public Cache getCache(String name) {
        return new DualLevelCache(guavaCacheManager.getCache(name),
                redisCacheManager.getCache(name));
    }

    @Override
    public Collection<String> getCacheNames() {
        return redisCacheManager.getCacheNames();
    }
}
