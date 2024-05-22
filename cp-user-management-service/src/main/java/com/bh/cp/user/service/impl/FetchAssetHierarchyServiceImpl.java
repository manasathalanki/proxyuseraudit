package com.bh.cp.user.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.bh.cp.user.feign.client.AssetHierarchyFeignClient;
import com.bh.cp.user.service.FetchAssetHierarchyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;

import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

@Service
public class FetchAssetHierarchyServiceImpl implements FetchAssetHierarchyService {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(FetchAssetHierarchyServiceImpl.class);

	@Value("${bh.asset.hierarchy.url}")
	private String assetHierarchyUrl;

	@Value("${bh.asset.hierarchy.v2.url}")
	private String assetHierarchyV2Url;

	@Value("${bh.asset.hierarchy.v2.payload.file.location}")
	private String assetHierarchyV2PayloadFileLocation;

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "fullassethierarchy")
	public List<Map<String, Object>> callAssetHierarchyAPI() throws JsonProcessingException {
		logger.info("Calling full Asset Hierarchy...");
		AssetHierarchyFeignClient feignClient = Feign.builder().client(new OkHttpClient()).encoder(new GsonEncoder())
				.decoder(new GsonDecoder()).logger(new Slf4jLogger(AssetHierarchyFeignClient.class))
				.logLevel(Logger.Level.FULL).target(AssetHierarchyFeignClient.class, assetHierarchyUrl);
		return new ObjectMapper().readValue(new GsonBuilder().create().toJson(feignClient.callGETRequestAPI()),
				List.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Cacheable(value = "fullassethierarchy")
	public List<Map<String, Object>> callAssetHierarchyAPIv2() throws IOException {
		logger.info("Calling full Asset Hierarchy v2 ...");
		AssetHierarchyFeignClient feignClient = Feign.builder().contract(new Contract.Default())
				.client(new OkHttpClient()).encoder(new GsonEncoder()).decoder(new GsonDecoder())
				.logger(new Slf4jLogger(AssetHierarchyFeignClient.class)).logLevel(Logger.Level.FULL)
				.target(AssetHierarchyFeignClient.class, assetHierarchyV2Url);

		ClassPathResource classPathResource = new ClassPathResource(assetHierarchyV2PayloadFileLocation);
		try (InputStream inputStream = classPathResource.getInputStream()) {
			Map<String, Object> payload = new ObjectMapper().readValue(inputStream, Map.class);
			return new ObjectMapper()
					.readValue(new GsonBuilder().create().toJson(feignClient.callPOSTRequestAPI(payload)), List.class);
		}

	}
}
