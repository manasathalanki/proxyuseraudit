package com.bh.cp.dashboard.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

class CacheConfigTest {

	private CacheConfig cacheConfig;

	private RedisConnectionFactory redisConnectionFactory;

	@BeforeEach
	void setUp() throws Exception {
		cacheConfig = new CacheConfig();
		redisConnectionFactory = mock(RedisConnectionFactory.class);
	}

	@ParameterizedTest
	@CsvSource({ "true", "false" })
	@DisplayName("Test redisConnectionFactory1 -- Validate Created Bean")
	void testRedisConnectionFactory_Positive1(boolean ssl) {
		LettuceConnectionFactory output = (LettuceConnectionFactory) cacheConfig
				.redisConnectionFactory("http://test.com", 6379, "username", "12345678", ssl);
		assertEquals("http://test.com", output.getHostName());
		assertEquals(6379, output.getPort());
		assertEquals("12345678", output.getPassword());
		assertEquals(ssl, output.isUseSsl());
	}

	@Test
	@DisplayName("Test redisTemplate1 -- Validate Created Bean")
	void testRedisTemplate_Positive1() {
		RedisTemplate<String, Object> output = cacheConfig.redisTemplate(redisConnectionFactory);
		assertEquals(redisConnectionFactory, output.getConnectionFactory());
	}

	@Test
	@DisplayName("Test redisCacheManagerBuilder1 -- Validate Created Bean")
	void testRedisCacheManagerBuilder_Positive1() {
		assertInstanceOf(RedisCacheManagerBuilder.class, cacheConfig.redisCacheManagerBuilder(redisConnectionFactory));
	}

	@Test
	@SuppressWarnings("deprecation")
	@DisplayName("Test redisCacheManagerBuilderCustomizer1 -- Validate Created Bean")
	void testRedisCacheManagerBuilderCustomizer_Positive1() throws NoSuchFieldException, SecurityException {
		RedisCacheManagerBuilderCustomizer output = cacheConfig.redisCacheManagerBuilderCustomizer();
		RedisCacheManagerBuilder redisCacheManagerBuilder = RedisCacheManagerBuilder
				.fromConnectionFactory(mock(LettuceConnectionFactory.class))
				.withInitialCacheConfigurations(new HashMap<>());
		output.customize(redisCacheManagerBuilder);
		assertInstanceOf(RedisCacheManagerBuilderCustomizer.class, output);
		assertEquals(Duration.ofSeconds(3600),
				redisCacheManagerBuilder.getCacheConfigurationFor("userassethierarchy").get().getTtl());
		assertEquals(false, redisCacheManagerBuilder.getCacheConfigurationFor("userassethierarchy").get()
				.getAllowCacheNullValues());
	}

}
