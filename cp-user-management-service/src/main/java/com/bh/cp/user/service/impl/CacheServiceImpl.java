package com.bh.cp.user.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Service;

import com.bh.cp.user.service.CacheService;

@Service
public class CacheServiceImpl implements CacheService {

	private static final Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);

	private CacheManager cacheManager;

	public CacheServiceImpl(@Autowired CacheManager cacheManager) {
		super();
		this.cacheManager = cacheManager;
	}

	@Override
	public void clearCacheWithPattern(String cacheName, String pattern) {
		RedisCache redisCache = (RedisCache) cacheManager.getCache(cacheName);
		if (redisCache != null) {
			logger.info("Clearing caches named {} with given pattern...", cacheName);
			redisCache.getNativeCache().clean(cacheName, pattern.getBytes());
		}
	}

}
