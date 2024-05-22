package com.bh.cp.user.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bh.cp.user.constants.JSONUtilConstants;
import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.dto.body.AttributeBody;
import com.bh.cp.user.dto.response.DomainResponseDTO;
import com.bh.cp.user.dto.response.GroupResponseDTO;
import com.bh.cp.user.exception.DeletionNotPermissableException;
import com.bh.cp.user.pojo.DomainAttribute;
import com.bh.cp.user.service.DomainService;
import com.bh.cp.user.service.FetchAssetHierarchyService;
import com.bh.cp.user.service.GenericAssetHierarchyFilterService;
import com.bh.cp.user.service.GroupService;
import com.bh.cp.user.service.RestClientWrapperService;
import com.bh.cp.user.util.CustomHttpServletRequestWrapper;
import com.bh.cp.user.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class DomainServiceImpl implements DomainService {

	private String subGroupURI;

	private String getAllDomainsUri;

	private String groupURI;

	private JwtUtil jwtUtil;

	private RestTemplate restTemplate;

	private RestClientWrapperService restClientWrapperService;

	private GenericAssetHierarchyFilterService assetHierarchyFilterService;

	private GroupService groupService;

	private FetchAssetHierarchyService fetchAssetHierarchyService;

	public DomainServiceImpl(@Autowired JwtUtil jwtUtil, @Autowired RestTemplate restTemplate,
			@Autowired GroupService groupService,
			@Autowired GenericAssetHierarchyFilterService assetHierarchyFilterService,
			@Autowired FetchAssetHierarchyService fetchAssetHierarchyService,
			@Autowired RestClientWrapperService restClientWrapperService,
			@Value("${keycloak.get.all.domains.uri}") String getAllDomainsUri,
			@Value("${keycloak.sub.group.uri}") String subGroupURI, @Value("${keycloak.groups.uri}") String groupURI) {
		super();
		this.jwtUtil = jwtUtil;
		this.groupService = groupService;
		this.assetHierarchyFilterService = assetHierarchyFilterService;
		this.restTemplate = restTemplate;
		this.restClientWrapperService = restClientWrapperService;
		this.fetchAssetHierarchyService = fetchAssetHierarchyService;
		this.getAllDomainsUri = getAllDomainsUri;
		this.subGroupURI = subGroupURI;
		this.groupURI = groupURI;
	}

	@Override
	@SuppressWarnings("unchecked")
	public synchronized List<DomainResponseDTO> getAllDomains(HttpServletRequest httpServletRequest) {
		List<DomainResponseDTO> domains = new ArrayList<>();
		CustomHttpServletRequestWrapper modifiedHttpServletRequest = new CustomHttpServletRequestWrapper(
				httpServletRequest, jwtUtil);
		ResponseEntity<String> response = restClientWrapperService.getResponseFromUrl(modifiedHttpServletRequest,
				getAllDomainsUri);
		JSONObject subGroup;
		String name;
		Boolean editable;
		JSONObject attributes;
		JSONArray groupsArray = new JSONObject(response.getBody()).getJSONArray(UMSConstants.SUBGROUPS);
		for (Object subGroupObj : groupsArray) {
			subGroup = (JSONObject) subGroupObj;
			name = subGroup.getString(UMSConstants.NAME);
			attributes = (JSONObject) subGroup.get(UMSConstants.ATTRIBUTES);
			List<AttributeBody> attributeBodyList = new ArrayList<>();
			attributes.toMap()
					.forEach((key, value) -> attributeBodyList.add(new AttributeBody(key, (List<String>) value)));
			editable = Boolean.TRUE;
			if (name != null && UMSConstants.ALL_DOMAIN.equals(name))
				editable = Boolean.FALSE;
			domains.add(new DomainResponseDTO(subGroup.getString(UMSConstants.ID),
					subGroup.getString(UMSConstants.NAME), attributeBodyList, editable));
		}
		return domains;
	}

	@Override
	@CacheEvict(value = "domainsmap", allEntries = true)
	public ResponseEntity<String> createDomain(GroupRepresentation request, HttpServletRequest httpServletRequest)
			throws JsonProcessingException {
		Map<String, List<String>> attributes = request.getAttributes();
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		attributes.put("source", Arrays.asList("local"));
		request.setAttributes(attributes);
		HttpHeaders headers = new HttpHeaders();
		headers.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + jwtUtil.generateAdminToken());
		HttpEntity<GroupRepresentation> httpEntity = new HttpEntity<>(request, headers);
		return restTemplate.exchange(subGroupURI, HttpMethod.POST, httpEntity,
				new ParameterizedTypeReference<String>() {
				});
	}

	@Override
	@CacheEvict(value = "domainsmap", allEntries = true)
	public ResponseEntity<String> updateDomain(GroupRepresentation request, HttpServletRequest httpServletRequest)
			throws JsonProcessingException {
		Map<String, List<String>> attributes = request.getAttributes();
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		attributes.put("source", Arrays.asList("local"));
		request.setAttributes(attributes);
		HttpHeaders headers = new HttpHeaders();
		headers.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + jwtUtil.generateAdminToken());
		HttpEntity<GroupRepresentation> httpEntity = new HttpEntity<>(request, headers);
		return restTemplate.exchange(groupURI + "/" + request.getId(), HttpMethod.PUT, httpEntity,
				new ParameterizedTypeReference<String>() {
				});
	}

	@Override
	public ResponseEntity<GroupRepresentation> getDomain(String domainId, HttpServletRequest httpServletRequest)
			throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + jwtUtil.generateAdminToken());
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		return restTemplate.exchange(groupURI + "/" + domainId, HttpMethod.GET, httpEntity,
				new ParameterizedTypeReference<GroupRepresentation>() {
				});
	}

	@Override
	public synchronized Map<String, List<DomainAttribute>> viewDomain(String domainId,
			HttpServletRequest httpServletRequest) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + jwtUtil.generateAdminToken());
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<GroupRepresentation> response = restTemplate.exchange(groupURI + "/" + domainId, HttpMethod.GET,
				httpEntity, new ParameterizedTypeReference<GroupRepresentation>() {
				});
		GroupRepresentation group = response.getBody();
		Map<String, List<DomainAttribute>> responseDto = new HashMap<>();
		if (group != null) {
			Map<String, List<String>> attributes = group.getAttributes();
			addAssetsToResponseDTO(responseDto, attributes, UMSConstants.PROJECTS_ATTRIBUTE,
					JSONUtilConstants.LEVEL_PROJECTS);
			addAssetsToResponseDTO(responseDto, attributes, UMSConstants.PLANTS_ATTRIBUTE,
					JSONUtilConstants.LEVEL_PLANTS);
			addAssetsToResponseDTO(responseDto, attributes, UMSConstants.TRAINS_ATTRIBUTE,
					JSONUtilConstants.LEVEL_TRAINS);
			addAssetsToResponseDTO(responseDto, attributes, UMSConstants.LINEUPS_ATTRIBUTE,
					JSONUtilConstants.LEVEL_LINEUPS);
			addAssetsToResponseDTO(responseDto, attributes, UMSConstants.MACHINES_ATTRIBUTE,
					JSONUtilConstants.LEVEL_MACHINES);
		}
		return responseDto;
	}

	private void addAssetsToResponseDTO(Map<String, List<DomainAttribute>> responseDto,
			Map<String, List<String>> attributes, String attributeName, String assetName) throws IOException {
		if (attributes != null && attributes.get(attributeName) != null) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			List<Map<String, Object>> fullHierarchy = fetchAssetHierarchyService.callAssetHierarchyAPIv2();
			Map<String, String> dislayNameMap = assetHierarchyFilterService.getDisplayNameMap(fullHierarchy);
			List<String> assetVids = attributes.get(attributeName);
			List<DomainAttribute> assets = new ArrayList<>();
			for (String assetVid : assetVids) {
				assets.add(new DomainAttribute(assetVid, dislayNameMap.get(assetVid), Boolean.TRUE));
			}
			responseDto.put(assetName, assets);
		}
	}

	@Override
	@CacheEvict(value = "domainsmap", allEntries = true)
	public synchronized ResponseEntity<String> deleteDomain(String domainId, HttpServletRequest httpServletRequest)
			throws DeletionNotPermissableException, JsonProcessingException, InterruptedException, ExecutionException {
		HttpHeaders headers = new HttpHeaders();
		headers.set(UMSConstants.AUTHORIZATION, UMSConstants.BEARER + jwtUtil.generateAdminToken());
		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		GroupResponseDTO group = groupService.getGroupDetails(httpServletRequest, domainId);
		if (group != null && UMSConstants.ALL_DOMAIN.equals(group.getName()))
			throw new DeletionNotPermissableException("Delete is not allowed for All-Domain");
		if (group != null && !group.getUsers().isEmpty())
			throw new DeletionNotPermissableException("Domain associated with Users can't be deleted");
		return restTemplate.exchange(groupURI + "/" + domainId, HttpMethod.DELETE, httpEntity,
				new ParameterizedTypeReference<String>() {
				});
	}

	@Override
	@Cacheable(value = "domainsmap", key = "#httpServletRequest.getHeader(\"Authorization\")")
	public Map<String, DomainResponseDTO> getDomainIdMap(HttpServletRequest httpServletRequest) {
		return getAllDomains(httpServletRequest).stream()
				.collect(Collectors.toMap(DomainResponseDTO::getId, domainResponseDTO -> domainResponseDTO));
	}

}
