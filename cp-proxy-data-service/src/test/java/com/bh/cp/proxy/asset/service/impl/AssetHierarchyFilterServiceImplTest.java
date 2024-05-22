package com.bh.cp.proxy.asset.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.bh.cp.proxy.constants.JSONUtilConstants;

class AssetHierarchyFilterServiceImplTest {

	@InjectMocks
	private AssetHierarchyFilterServiceImpl assetHierarchyFilterServiceImpl;

	private List<Map<String, Object>> assetHierarchy;

	private class TestConstants {

		private static final String TEST1 = "TEST1";
		private static final String TEST2 = "TEST2";
		private static final String TEST3 = "TEST3";
		private static final String PL_TEST1 = "PL_TEST1";
		private static final String PL_TEST2 = "PL_TEST2";
		private static final String PR_TEST1 = "PR_TEST1";
		private static final String PR_TEST2 = "PR_TEST2";
		private static final String PR_TEST3 = "PR_TEST3";
		private static final String TESTPRFIELDKEY1 = "testPRFieldKey1";
		private static final String TESTPLFIELDKEY1 = "testPLFieldKey1";
		private static final String PL_TEST1VALUE1 = "PL_TEST1Value1";
		private static final String PL_TEST2VALUE1 = "PL_TEST1Value1";
		private static final String PR_TEST1VALUE1 = "PR_TEST1Value1";
		private static final String PR_TEST2VALUE1 = "PR_TEST2Value1";
		private static final String PR_TEST3VALUE1 = "PR_TEST2Value1";
		private static final String INVALID_VID = "PR_TEST25";

	}

	@BeforeEach
	void setup() {

		MockitoAnnotations.openMocks(this);
		List<Map<String, Object>> projects1 = new ArrayList<>();
		Map<String, Object> data = new HashMap<>();
		data.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		data.put(JSONUtilConstants.DATA, projects1);
		List<Map<String, Object>> plants1 = new ArrayList<>();
		Map<String, Object> data1 = new HashMap<>();
		data1.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PLANTS);
		data1.put(JSONUtilConstants.DATA, plants1);

		Map<String, Object> additionalFields = new HashMap<>();
		additionalFields.put(JSONUtilConstants.EQUIPMENTCODES, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.TECHNOLOGYCODES, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.ENABLEDSERVICES, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.GIBSERIALNOS, List.of("abc", "cde"));
		additionalFields.put(JSONUtilConstants.SERIALNOS, List.of("abc", "cde"));

		Map<String, Object> plant1 = new HashMap<>();
		plant1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		plant1.put(JSONUtilConstants.VID, TestConstants.PL_TEST1);
		plant1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PL_TEST1,
				TestConstants.TESTPLFIELDKEY1, TestConstants.PL_TEST1VALUE1));
		plant1.putAll(additionalFields);
		plants1.add(plant1);

		Map<String, Object> plant2 = new HashMap<>();
		plant2.put(JSONUtilConstants.ID, TestConstants.TEST2);
		plant2.put(JSONUtilConstants.VID, TestConstants.PL_TEST2);
		plant2.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PL_TEST2,
				TestConstants.TESTPLFIELDKEY1, TestConstants.PL_TEST2VALUE1));
		plant2.putAll(additionalFields);
		plants1.add(plant2);

		Map<String, Object> project1 = new HashMap<>();
		project1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		project1.put(JSONUtilConstants.VID, TestConstants.PR_TEST1);
		project1.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PR_TEST1,
				TestConstants.TESTPRFIELDKEY1, TestConstants.PR_TEST1VALUE1));
		project1.put(JSONUtilConstants.CHILDREN, data1);
		project1.putAll(additionalFields);
		projects1.add(project1);

		Map<String, Object> project2 = new HashMap<>();
		project2.put(JSONUtilConstants.ID, TestConstants.TEST2);
		project2.put(JSONUtilConstants.VID, TestConstants.PR_TEST2);
		project2.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PR_TEST2,
				TestConstants.TESTPRFIELDKEY1, TestConstants.PR_TEST2VALUE1));
		project2.putAll(additionalFields);
		projects1.add(project2);

		Map<String, Object> project3 = new HashMap<>();
		project3.put(JSONUtilConstants.ID, TestConstants.TEST3);
		project3.put(JSONUtilConstants.VID, TestConstants.PR_TEST3);
		project3.put(JSONUtilConstants.FIELDS, Map.of(JSONUtilConstants.VID, TestConstants.PR_TEST3,
				TestConstants.TESTPRFIELDKEY1, TestConstants.PR_TEST3VALUE1));
		project3.putAll(additionalFields);
		projects1.add(project3);

		assetHierarchy = new ArrayList<>();
		assetHierarchy.add(data);
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Test GetSubTree -- VID with Children")
	void testGetSubTree_Positive1() {
		List<Map<String, Object>> output = assetHierarchyFilterServiceImpl.getSubTree(assetHierarchy,
				TestConstants.PR_TEST1);
		assertEquals(2, ((List<Map<String, Object>>) output.get(0).get(JSONUtilConstants.DATA)).size());
		assertEquals(TestConstants.PL_TEST1, ((List<Map<String, Object>>) output.get(0).get(JSONUtilConstants.DATA))
				.get(0).get(JSONUtilConstants.VID));
		assertEquals(null, ((List<Map<String, Object>>) output.get(0).get(JSONUtilConstants.DATA)).get(0)
				.get(JSONUtilConstants.CHILDREN));
	}

	@Test
	@DisplayName("Test GetSubTree -- VID without Children")
	void testGetSubTree_Positive2() {
		List<Map<String, Object>> output = assetHierarchyFilterServiceImpl.getSubTree(assetHierarchy,
				TestConstants.PR_TEST2);
		assertEquals(null, output.get(0));
	}

	@Test
	@DisplayName("Test GetSubTree -- Invalid VID")
	void testGetSubTree_Negative1() {
		List<Map<String, Object>> output = assetHierarchyFilterServiceImpl.getSubTree(assetHierarchy,
				TestConstants.INVALID_VID);
		assertEquals(0, output.size());
	}

	@Test
	@DisplayName("Test GetAssetsMap -- VID with Children")
	void testGetAssetsMap_Positive1() {
		Map<String, Object> output = assetHierarchyFilterServiceImpl.getAssetsMap(assetHierarchy,
				TestConstants.PR_TEST1, true);
		assertEquals(true, output.get(JSONUtilConstants.MATCHFOUND));
		assertEquals(TestConstants.PR_TEST1, output.get(JSONUtilConstants.SEARCHVID));
		assertEquals(JSONUtilConstants.LEVEL_PROJECTS, output.get(JSONUtilConstants.CURRENTLEVEL));
		assertNotEquals(0, output.get(output.get(JSONUtilConstants.NEXTLEVEL)));
	}

	@Test
	@DisplayName("Test GetAssetsMap -- VID without Children")
	void testGetAssetsMap_Positive2() {
		Map<String, Object> output = assetHierarchyFilterServiceImpl.getAssetsMap(assetHierarchy,
				TestConstants.PR_TEST2, true);
		assertEquals(true, output.get(JSONUtilConstants.MATCHFOUND));
		assertEquals(TestConstants.PR_TEST2, output.get(JSONUtilConstants.SEARCHVID));
		assertEquals(JSONUtilConstants.LEVEL_PROJECTS, output.get(JSONUtilConstants.CURRENTLEVEL));
		assertEquals(null, output.get(JSONUtilConstants.NEXTLEVEL));
	}

	@Test
	@DisplayName("Test GetAssetsMap -- Invalid VID")
	void testGetAssetsMap_Negative1() {
		Map<String, Object> output = assetHierarchyFilterServiceImpl.getAssetsMap(assetHierarchy,
				TestConstants.INVALID_VID, true);
		assertEquals(0, output.size());
	}

	@Test
	@DisplayName("Test GetImmediateParentField -- VID with Parent")
	void testGetImmediateParentField_Positive1() {
		assertEquals(TestConstants.PR_TEST1VALUE1, assetHierarchyFilterServiceImpl
				.getImmediateParentField(assetHierarchy, TestConstants.PL_TEST1, TestConstants.TESTPRFIELDKEY1));
	}

	@Test
	@DisplayName("Test GetImmediateParentField -- VID without Parent")
	void testGetImmediateParentField_Negative1() {
		assertEquals(TestConstants.PR_TEST2VALUE1, assetHierarchyFilterServiceImpl
				.getImmediateParentField(assetHierarchy, TestConstants.PR_TEST2, TestConstants.TESTPRFIELDKEY1));
	}

	@Test
	@DisplayName("Test GetImmediateParentField -- Invalid VID")
	void testGetImmediateParentField_Negative2() {
		assertEquals(null, assetHierarchyFilterServiceImpl.getImmediateParentField(assetHierarchy,
				TestConstants.INVALID_VID, TestConstants.TESTPRFIELDKEY1));
	}

	@Test
	@DisplayName("Test GetFieldValuesForLevel -- VID with Children")
	void testGetFieldValuesForLevel_Positive1() {
		assertEquals(List.of(TestConstants.PL_TEST1VALUE1, TestConstants.PL_TEST2VALUE1),
				assetHierarchyFilterServiceImpl.getFieldValuesForLevel(assetHierarchy, TestConstants.PR_TEST1,
						JSONUtilConstants.LEVEL_PLANTS, TestConstants.TESTPLFIELDKEY1));
	}

	@Test
	@DisplayName("Test GetFieldValuesForLevel -- VID without Children")
	void testGetFieldValuesForLevel_Positive2() {
		assertEquals(Collections.EMPTY_LIST, assetHierarchyFilterServiceImpl.getFieldValuesForLevel(assetHierarchy,
				TestConstants.PR_TEST2, JSONUtilConstants.LEVEL_PLANTS, TestConstants.PL_TEST1VALUE1));
	}

	@Test
	@DisplayName("Test GetFieldValuesForLevel -- Invalid VID")
	void testGetFieldValuesForLevel_Negative1() {
		assertEquals(Collections.EMPTY_LIST, assetHierarchyFilterServiceImpl.getFieldValuesForLevel(assetHierarchy,
				TestConstants.INVALID_VID, JSONUtilConstants.LEVEL_PLANTS, TestConstants.PL_TEST1VALUE1));
	}

	@Test
	@DisplayName("Test GetFieldsAndEnabledServicesToMap -- Valid VID")
	void testGetFieldsAndEnabledServicesToMap_Positive1() {
		Map<String, Map<String, Set<String>>> output = assetHierarchyFilterServiceImpl
				.getFieldsAndEnabledServicesToMap(assetHierarchy);
		Map<String, Set<String>> vidMap = output.get(TestConstants.PR_TEST1);
		assertNotEquals(null, vidMap);
		assertEquals(2, vidMap.get(JSONUtilConstants.EQUIPMENTCODES).size());
		assertEquals(2, vidMap.get(JSONUtilConstants.TECHNOLOGYCODES).size());
		assertEquals(2, vidMap.get(JSONUtilConstants.ENABLEDSERVICES).size());
		assertEquals(2, vidMap.get(JSONUtilConstants.GIBSERIALNOS).size());
		assertEquals(2, vidMap.get(JSONUtilConstants.SERIALNOS).size());
	}

	@Test
	@DisplayName("Test GetFieldsAndEnabledServicesToMap -- Invalid VID")
	void testGetFieldsAndEnabledServicesToMap_Negative1() {
		Map<String, Map<String, Set<String>>> output = assetHierarchyFilterServiceImpl
				.getFieldsAndEnabledServicesToMap(assetHierarchy);
		Map<String, Set<String>> vidMap = output.get(TestConstants.INVALID_VID);
		assertEquals(null, vidMap);
	}

	@Test
	@DisplayName("Test GetProjectsAndPlants -- VID with Children")
	void testGetProjectsAndPlants_Positive1() {
		Map<String, Object> assetsMap = assetHierarchyFilterServiceImpl.getAssetsMap(assetHierarchy,
				TestConstants.PR_TEST1, true);
		List<Map<String, String>> output = assetHierarchyFilterServiceImpl.getProjectsAndPlants(assetsMap);
		assertEquals(2, output.size());
		assertEquals(Map.of(JSONUtilConstants.PROJECTS_PREFIX, TestConstants.PR_TEST1, JSONUtilConstants.PLANTS_PREFIX,
				TestConstants.PL_TEST1), output.get(0));
		assertEquals(Map.of(JSONUtilConstants.PROJECTS_PREFIX, TestConstants.PR_TEST1, JSONUtilConstants.PLANTS_PREFIX,
				TestConstants.PL_TEST2), output.get(1));
	}

	@Test
	@DisplayName("Test GetProjectsAndPlants -- VID without Children")
	void testGetProjectsAndPlants_Negative1() {
		Map<String, Object> assetsMap = assetHierarchyFilterServiceImpl.getAssetsMap(assetHierarchy,
				TestConstants.PR_TEST2, true);
		List<Map<String, String>> output = assetHierarchyFilterServiceImpl.getProjectsAndPlants(assetsMap);
		assertEquals(0, output.size());
	}

	@Test
	@DisplayName("Test ValidateVid -- Valid VID")
	void testValidateVid_Positive1() {
		assertEquals(true, assetHierarchyFilterServiceImpl.validateVid(assetHierarchy, TestConstants.PR_TEST1));
	}

	@Test
	@DisplayName("Test ValidateVid -- Invalid VID")
	void testValidateVid_Negative1() {
		assertEquals(false, assetHierarchyFilterServiceImpl.validateVid(assetHierarchy, TestConstants.INVALID_VID));
	}

}