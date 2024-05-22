package com.bh.cp.proxy.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.bh.cp.proxy.entity.ServicesDirectory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class CacheConfig {

	@Bean
	public RedisConnectionFactory redisConnectionFactory(@Value("${spring.cache.redis.host}") String redisHost,
			@Value("${spring.cache.redis.port}") Integer redisPort,
			@Value("${spring.cache.redis.username}") String redisUsername,
			@Value("${spring.cache.redis.password}") String redisCredValue,
			@Value("${spring.cache.redis.ssl}") boolean useSsl) {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redisHost);
		redisStandaloneConfiguration.setPort(redisPort);
		redisStandaloneConfiguration.setUsername(redisUsername);
		redisStandaloneConfiguration.setPassword(redisCredValue);
		return new LettuceConnectionFactory(redisStandaloneConfiguration,
				useSsl ? LettuceClientConfiguration.builder().useSsl().build()
						: LettuceClientConfiguration.builder().build());
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean
	public RedisCacheManagerBuilder redisCacheManagerBuilder(@Autowired RedisConnectionFactory redisConnectionFactory) {
		return RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory);
	}

	@Bean
	public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {

		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

		RedisCacheConfiguration redisCacheConfiguration1 = RedisCacheConfiguration.defaultCacheConfig()
				.disableCachingNullValues()
				.serializeValuesWith((SerializationPair.fromSerializer(compressedSerializer())))
				.computePrefixWith(CacheKeyPrefix.prefixed("{Compressed}")).entryTtl(Duration.ofSeconds(3600));

		RedisCacheConfiguration redisCacheConfiguration2 = RedisCacheConfiguration.defaultCacheConfig()
				.disableCachingNullValues()
				.serializeValuesWith((SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())))
				.computePrefixWith(CacheKeyPrefix.prefixed("{GenericJackson}")).entryTtl(Duration.ofSeconds(3600));

		RedisCacheConfiguration redisCacheConfiguration3 = RedisCacheConfiguration.defaultCacheConfig()
				.disableCachingNullValues()
				.serializeValuesWith((SerializationPair.fromSerializer(new JdkSerializationRedisSerializer())))
				.computePrefixWith(CacheKeyPrefix.prefixed("{JDK}")).entryTtl(Duration.ofSeconds(3600));

		RedisCacheConfiguration redisCacheConfiguration4 = RedisCacheConfiguration.defaultCacheConfig()
				.disableCachingNullValues()
				.serializeValuesWith((SerializationPair
						.fromSerializer(new JdkSerializationRedisSerializer(ServicesDirectory.class.getClassLoader()))))
				.computePrefixWith(CacheKeyPrefix.prefixed("{JDKCustom1}")).entryTtl(Duration.ofSeconds(3600));

		cacheConfigurations.put("userassethierarchy", redisCacheConfiguration1);
		cacheConfigurations.put("proxyuserdetails", redisCacheConfiguration2);
		cacheConfigurations.put("widgetaccess", redisCacheConfiguration2);
		cacheConfigurations.put("oktauserdetails", redisCacheConfiguration2);
		cacheConfigurations.put("alluserdetails", redisCacheConfiguration2);
		cacheConfigurations.put("userprivileges", redisCacheConfiguration3);
		cacheConfigurations.put("applicablemachines", redisCacheConfiguration3);
		cacheConfigurations.put("servicedirectory", redisCacheConfiguration4);
		return builder -> builder.withInitialCacheConfigurations(cacheConfigurations).build();
	}

	private RedisSerializer<Object> compressedSerializer() {

		return new RedisSerializer<Object>() {

			@Override
			public byte[] serialize(Object value) throws SerializationException {
				try {
					return compress(new ObjectMapper().writeValueAsBytes(value));
				} catch (JsonProcessingException e) {
					return new byte[] {};
				}
			}

			@Override
			public Object deserialize(byte[] bytes) throws RuntimeException {
				try {
					return new ObjectMapper().readValue(decompress(bytes), List.class);
				} catch (IOException e) {
					return null;
				}
			}
		};

	}

	private byte[] compress(byte[] content) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
			gzipOutputStream.write(content);
		} catch (IOException e) {
			throw new SerializationException("Unable to compress data", e);
		}
		return byteArrayOutputStream.toByteArray();
	}

	private byte[] decompress(byte[] contentBytes) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(contentBytes), 256), out);
		} catch (IOException e) {
			throw new SerializationException("Unable to decompress data", e);
		}
		return out.toByteArray();
	}

}