package com.bh.cp.user.feign.client;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;

import feign.Headers;
import feign.RequestLine;

public interface AssetHierarchyFeignClient {

	@RequestLine("GET /")
	Object callGETRequestAPI();

	@RequestLine("POST /")
	@Headers("Content-type: application/json")
	Object callPOSTRequestAPI(@RequestBody Map<String,Object> payload);

}
