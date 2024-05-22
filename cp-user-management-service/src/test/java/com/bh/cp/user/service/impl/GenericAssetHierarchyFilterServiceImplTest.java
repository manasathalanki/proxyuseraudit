package com.bh.cp.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bh.cp.user.constants.JSONUtilConstants;
import com.bh.cp.user.constants.UMSConstants;
import com.bh.cp.user.service.FetchAssetHierarchyService;
import com.bh.cp.user.util.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class GenericAssetHierarchyFilterServiceImplTest {

	@InjectMocks
	private GenericAssetHierarchyFilterServiceImpl genericAssetHierarchyFilterServiceImpl;

	@Mock
	private FetchAssetHierarchyService fetchAssetHierarchyService;

	@Mock
	private JSONUtil jsonUtil;

	List<String> vids;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		vids = new ArrayList<>();
		vids.add("PR_TEST");
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("getFilteredHierarchy - Getting User's Asset Hierarchy")
	void testGetFilteredHierarchy() throws JsonProcessingException, Exception {
		when(fetchAssetHierarchyService.callAssetHierarchyAPIv2())
		.thenReturn(new ObjectMapper().readValue("[{ \"level\":\"projects\", \"data\":[ { \"id\":\"PREST\", \"vid\":\"PR_TEST\", \"fields\":{}, \"children\":{ \"level\":\"machines\", \"data\":[ { \"id\":\"MCTEST\", \"vid\":\"MC_MCTEST\", "
				+ "\"fields\":{ \"equipmentCode\":\"UNK\", \"technologyCodeOg\":\"GB\", \"vid\":\"MC_GB0654\", \"lowNoxType\":null, \"id\":4938, \"gibSerialNo\":\"935853\" }, "
				+ "\"enabledServices\":[ \"CBM2567\" ] } ] } } ] } ]",List.class));
		List<Map<String, Object>> fullHierarchy = genericAssetHierarchyFilterServiceImpl.getFilteredHierarchy(vids,
				true);
		assertNotNull(fullHierarchy);
	}

	@Test
	@DisplayName("GetSubTree - Getting Sub-Tree")
	void testGetSubTree() throws JsonProcessingException, Exception {
		List<Map<String, Object>> fullHierarchy = genericAssetHierarchyFilterServiceImpl.getSubTree(getAssetHierarchy(),
				"MC_MCTEST");
		assertNotNull(fullHierarchy);
	}

	@Test
	@DisplayName("ValidateVid - Validating VID")
	void testValidateVid() throws JsonProcessingException, Exception {
		boolean fullHierarchy = genericAssetHierarchyFilterServiceImpl.validateVid(getAssetHierarchy(), "MC_MCTEST");
		assertTrue(fullHierarchy);
	}

	@Test
	@DisplayName("GetAssetsMap - Retrieve Assets Map")
	void testGetAssetsMap() throws JsonProcessingException, Exception {
		Map<String, Object> assetsMap = genericAssetHierarchyFilterServiceImpl.getAssetsMap(getAssetHierarchy(),
				"PR_PRTEST", "projects");
		assertNotNull(assetsMap);
	}

	@Test
	@DisplayName("GetDisplayNameMap")
	void testGetDisplayNameMap() throws JsonProcessingException, Exception {
		Map<String, String> assetsMap = genericAssetHierarchyFilterServiceImpl.getDisplayNameMap(getAssetHierarchy());
		assertNotNull(assetsMap);
	}

	@Test
	@DisplayName("GetFieldsAndEnabledServicesToMap")
	void testGetFieldsAndEnabledServicesToMap() throws JsonProcessingException, Exception {
		Map<String, Map<String, Set<String>>> enableServicesMap = genericAssetHierarchyFilterServiceImpl
				.getFieldsAndEnabledServicesToMap(getAssetHierarchy());
		assertNotNull(enableServicesMap);
	}

	@Test
	@DisplayName("GetImmediateParentField")
	void testGetImmediateParentField() throws JsonProcessingException, Exception {
		String immediateParent = genericAssetHierarchyFilterServiceImpl.getImmediateParentField(getAssetHierarchy(),
				"MC_MCTEST", "machines");
		assertNull(immediateParent);
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("GetAssetHierarchyByLevel")
	void testGetAssetHierarchyByLevel() throws JsonProcessingException, Exception {
		when(fetchAssetHierarchyService.callAssetHierarchyAPIv2())
		.thenReturn(new ObjectMapper().readValue("[{ \"level\":\"projects\", \"data\":[ { \"id\":\"PREST\", \"vid\":\"PR_TEST\", \"fields\":{}, \"children\":{ \"level\":\"machines\", \"data\":[ { \"id\":\"MCTEST\","
				+ " \"vid\":\"MC_MCTEST\", \"fields\":{ \"equipmentCode\":\"UNK\", \"technologyCodeOg\":\"GB\", \"vid\":\"MC_GB0654\", \"lowNoxType\":null, \"id\":4938, \"gibSerialNo\":\"935853\" }, \"enabledServices\":[ \"CBM2567\" ] } ] } } ] } ]",List.class));
		String immediateParent = genericAssetHierarchyFilterServiceImpl.getAssetHierarchyByLevel("machines");
		assertNotNull(immediateParent);
	}

	private List<Map<String, Object>> getAssetHierarchy() {
		List<Map<String, Object>> listOfMaps = new ArrayList<>();
		Map<String, Object> mainObject = new HashMap<>();
		List<Map<String, Object>> outer = new ArrayList<>();
		Map<String, Object> children = new HashMap<>();
		children.put("level", "machines");
		children.put("data", toMap());
		Map<String, Object> map1 = new HashMap<>();
		map1.put(JSONUtilConstants.ID, "PRTEST");
		map1.put(JSONUtilConstants.VID, "PR_PRTEST");
		map1.put("fields", new HashMap<>());
		map1.put("children", children);
		outer.add(map1);
		mainObject.put("level", "projects");
		mainObject.put(JSONUtilConstants.DATA, outer);
		listOfMaps.add(mainObject);
		return listOfMaps;
	}

	private List<Map<String, Object>> toMap() {
		List<Map<String, Object>> listOfMaps = new ArrayList<>();
		Map<String, Object> map1 = new HashMap<>();
		map1.put("equipmentCode", "UNK");
		map1.put("technologyCodeOg", "UNK1");
		map1.put("lowNoxType", "kk");
		map1.put("gibSerialNo", "MC5087");
		map1.put("vid", "MC_MCTEST");
		Map<String, Object> map = new HashMap<>();
		map.put(UMSConstants.ID, "MCTEST");
		map.put(JSONUtilConstants.VID, "MC_MCTEST");
		map.put("fields", map1);
		map.put("enabledServices", Arrays.asList("Enabled1", "Enabled2"));
		listOfMaps.add(map);
		return listOfMaps;
	}

}
