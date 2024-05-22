package com.bh.cp.dashboard.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.bh.cp.dashboard.constants.JSONUtilConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

class JSONUtilTest {

	private List<Map<String, Object>> assetHierarchy;

	private class TestConstants {

		private TestConstants() {
			super();
		}

		private static final String TEST1 = "TEST1";
		private static final String TEST2 = "TEST2";
		private static final String TEST3 = "TEST3";
		private static final String TEST4 = "TEST4";
		private static final String MC_TEST3 = "MC_TEST3";
		private static final String MC_TEST4 = "MC_TEST4";
		private static final String MC_TEST2 = "MC_TEST2";
		private static final String MC_TEST1 = "MC_TEST1";
		private static final String LN_TEST1 = "LN_TEST1";
		private static final String TR_TEST1 = "TR_TEST1";
		private static final String PL_TEST1 = "PL_TEST1";
		private static final String PL_TEST2 = "PL_TEST2";
		private static final String PR_TEST1 = "PR_TEST1";
		private static final String PR_TEST2 = "PR_TEST2";
		private static final String PR_TEST3 = "PR_TEST3";
		private static final String PR_TEST4 = "PR_TEST4";
		private static final String TESTPRFIELDKEY1 = "testPRFieldKey1";
		private static final String TESTPLFIELDKEY1 = "testPLFieldKey1";
		private static final String TESTTRFIELDKEY1 = "testTRFieldKey1";
		private static final String TESTLNFIELDKEY1 = "testLNFieldKey1";
		private static final String TESTMCFIELDKEY1 = "testMCFieldKey1";
		private static final String MC_TEST3VALUE1 = "MC_TEST3Value1";
		private static final String MC_TEST2VALUE1 = "MC_TEST2Value1";
		private static final String MC_TEST1VALUE1 = "MC_TEST1Value1";
		private static final String LN_TEST1VALUE1 = "LN_TEST1Value1";
		private static final String TR_TEST1VALUE1 = "TR_TEST1Value1";
		private static final String PL_TEST1VALUE1 = "PL_TEST1Value1";
		private static final String PL_TEST2VALUE1 = "PL_TEST1Value1";
		private static final String PR_TEST1VALUE1 = "PR_TEST1Value1";
		private static final String PR_TEST2VALUE1 = "PR_TEST2Value1";
		private static final String PR_TEST3VALUE1 = "PR_TEST2Value1";
		private static final String TESTPRFIELDKEY2 = "customer";
		private static final String TESTPLFIELDKEY2 = "plantName";
		private static final String TESTTRFIELDKEY2 = "description";
		private static final String TESTLNFIELDKEY2 = "lnupName";
		private static final String TESTMCFIELDKEY2 = "oemSerialNo";
	}

	@BeforeEach
	void setUp() throws JsonMappingException, JsonProcessingException {
		MockitoAnnotations.openMocks(this);

		List<Map<String, Object>> projects1 = new ArrayList<>();
		Map<String, Object> data = new HashMap<>();
		data.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PROJECTS);
		data.put(JSONUtilConstants.DATA, projects1);

		List<Map<String, Object>> plants1 = new ArrayList<>();
		Map<String, Object> data1 = new HashMap<>();
		data1.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_PLANTS);
		data1.put(JSONUtilConstants.DATA, plants1);

		List<Map<String, Object>> trains1 = new ArrayList<>();
		Map<String, Object> data2 = new HashMap<>();
		data2.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_TRAINS);
		data2.put(JSONUtilConstants.DATA, trains1);

		List<Map<String, Object>> lineups1 = new ArrayList<>();
		Map<String, Object> data3 = new HashMap<>();
		data3.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_LINEUPS);
		data3.put(JSONUtilConstants.DATA, lineups1);

		List<Map<String, Object>> machines1 = new ArrayList<>();
		Map<String, Object> data4 = new HashMap<>();
		data4.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_MACHINES);
		data4.put(JSONUtilConstants.DATA, machines1);

		List<Map<String, Object>> lineups2 = new ArrayList<>();
		Map<String, Object> data5 = new HashMap<>();
		data5.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_LINEUPS);
		data5.put(JSONUtilConstants.DATA, lineups2);

		List<Map<String, Object>> lineups3 = new ArrayList<>();
		Map<String, Object> data6 = new HashMap<>();
		data6.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_LINEUPS);
		data6.put(JSONUtilConstants.DATA, lineups3);

		List<Map<String, Object>> machines2 = new ArrayList<>();
		Map<String, Object> data7 = new HashMap<>();
		data7.put(JSONUtilConstants.LEVEL, JSONUtilConstants.LEVEL_MACHINES);
		data7.put(JSONUtilConstants.DATA, machines2);

		Map<String, Object> machine1 = new HashMap<>();
		machine1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		machine1.put(JSONUtilConstants.VID, TestConstants.MC_TEST1);
		machine1.put(JSONUtilConstants.FIELDS, new HashMap<>(Map.of(JSONUtilConstants.VID, TestConstants.MC_TEST1,
				TestConstants.TESTMCFIELDKEY1, TestConstants.MC_TEST1VALUE1, TestConstants.TESTMCFIELDKEY2,
				TestConstants.MC_TEST1 + "_name", JSONUtilConstants.EQUIPMENTCODE, TestConstants.MC_TEST1 + "_eqCode",
				JSONUtilConstants.TECHNOLOGYCODEOG, TestConstants.MC_TEST1 + "_techCode", JSONUtilConstants.GIBSERIALNO,
				TestConstants.MC_TEST1 + "_gibSerialNo", JSONUtilConstants.SERIALNO,
				TestConstants.MC_TEST1 + "_serialNo", JSONUtilConstants.LOWNOX, TestConstants.MC_TEST1 + "_lowNox")));
		machine1.put(JSONUtilConstants.ENABLEDSERVICES, List.of("abc1", "cde1"));
		machines1.add(machine1);

		Map<String, Object> machine2 = new HashMap<>();
		machine2.put(JSONUtilConstants.ID, TestConstants.TEST2);
		machine2.put(JSONUtilConstants.VID, TestConstants.MC_TEST2);
		machine2.put(JSONUtilConstants.FIELDS,
				new HashMap<>(Map.of(JSONUtilConstants.VID, TestConstants.MC_TEST2, TestConstants.TESTMCFIELDKEY1,
						TestConstants.MC_TEST2VALUE1, TestConstants.TESTMCFIELDKEY2, TestConstants.MC_TEST2 + "_name",
						JSONUtilConstants.EQUIPMENTCODE, TestConstants.MC_TEST2 + "_eqCode",
						JSONUtilConstants.TECHNOLOGYCODEOG, TestConstants.MC_TEST2 + "_techCode",
						JSONUtilConstants.GIBSERIALNO, TestConstants.MC_TEST2 + "_gibSerialNo",
						JSONUtilConstants.SERIALNO, TestConstants.MC_TEST2 + "_serialNo")));
		machine2.put(JSONUtilConstants.ENABLEDSERVICES, List.of("abc2", "cde2"));
		machines1.add(machine2);

		Map<String, Object> lineup1 = new HashMap<>();
		lineup1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		lineup1.put(JSONUtilConstants.VID, TestConstants.LN_TEST1);
		lineup1.put(JSONUtilConstants.FIELDS,
				new HashMap<>(Map.of(JSONUtilConstants.VID, TestConstants.LN_TEST1, TestConstants.TESTLNFIELDKEY1,
						TestConstants.LN_TEST1VALUE1, TestConstants.TESTLNFIELDKEY2,
						TestConstants.LN_TEST1 + "_name")));
		lineup1.put(JSONUtilConstants.CHILDREN, data4);
		lineups1.add(lineup1);

		Map<String, Object> train1 = new HashMap<>();
		train1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		train1.put(JSONUtilConstants.VID, TestConstants.TR_TEST1);
		train1.put(JSONUtilConstants.FIELDS,
				new HashMap<>(Map.of(JSONUtilConstants.VID, TestConstants.TR_TEST1, TestConstants.TESTTRFIELDKEY1,
						TestConstants.TR_TEST1VALUE1, TestConstants.TESTTRFIELDKEY2,
						TestConstants.TR_TEST1 + "_name")));
		train1.put(JSONUtilConstants.CHILDREN, data3);
		trains1.add(train1);

		Map<String, Object> plant1 = new HashMap<>();
		plant1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		plant1.put(JSONUtilConstants.VID, TestConstants.PL_TEST1);
		plant1.put(JSONUtilConstants.FIELDS,
				new HashMap<>(Map.of(JSONUtilConstants.VID, TestConstants.PL_TEST1, TestConstants.TESTPLFIELDKEY1,
						TestConstants.PL_TEST1VALUE1, TestConstants.TESTPLFIELDKEY2,
						TestConstants.PL_TEST1 + "_name")));
		plant1.put(JSONUtilConstants.CHILDREN, data2);
		plants1.add(plant1);

		Map<String, Object> machine3 = new HashMap<>();
		machine3.put(JSONUtilConstants.ID, TestConstants.TEST3);
		machine3.put(JSONUtilConstants.VID, TestConstants.MC_TEST3);
		machine3.put(JSONUtilConstants.FIELDS,
				new HashMap<>(Map.of(JSONUtilConstants.VID, TestConstants.MC_TEST3, TestConstants.TESTMCFIELDKEY1,
						TestConstants.MC_TEST3VALUE1, TestConstants.TESTMCFIELDKEY2, TestConstants.MC_TEST3 + "_name",
						JSONUtilConstants.TECHNOLOGYCODEOG, TestConstants.MC_TEST3 + "_techCode",
						JSONUtilConstants.GIBSERIALNO, TestConstants.MC_TEST3 + "_gibSerialNo",
						JSONUtilConstants.SERIALNO, TestConstants.MC_TEST3 + "_serialNo")));
		machines2.add(machine3);

		Map<String, Object> machine4 = new HashMap<>();
		machine4.put(JSONUtilConstants.ID, TestConstants.TEST3);
		machine4.put(JSONUtilConstants.VID, TestConstants.MC_TEST4);
		machines2.add(machine4);

		Map<String, Object> lineup2 = new HashMap<>();
		lineup2.put(JSONUtilConstants.ID, (String) null);
		lineup2.put(JSONUtilConstants.VID, (String) null);
		lineup2.put(JSONUtilConstants.FIELDS, new HashMap<>());
		lineup2.put(JSONUtilConstants.CHILDREN, data7);
		lineups2.add(lineup2);

		Map<String, Object> plant2 = new HashMap<>();
		plant2.put(JSONUtilConstants.ID, TestConstants.TEST2);
		plant2.put(JSONUtilConstants.VID, TestConstants.PL_TEST2);
		plant2.put(JSONUtilConstants.FIELDS,
				new HashMap<>(Map.of(JSONUtilConstants.VID, TestConstants.PL_TEST2, TestConstants.TESTPLFIELDKEY1,
						TestConstants.PL_TEST2VALUE1, TestConstants.TESTPLFIELDKEY2,
						TestConstants.PL_TEST2 + "_name")));
		plant2.put(JSONUtilConstants.CHILDREN, data5);
		plants1.add(plant2);

		Map<String, Object> project1 = new HashMap<>();
		project1.put(JSONUtilConstants.ID, TestConstants.TEST1);
		project1.put(JSONUtilConstants.VID, TestConstants.PR_TEST1);
		project1.put(JSONUtilConstants.FIELDS,
				new HashMap<>(Map.of(JSONUtilConstants.VID, TestConstants.PR_TEST1, TestConstants.TESTPRFIELDKEY1,
						TestConstants.PR_TEST1VALUE1, TestConstants.TESTPRFIELDKEY2,
						TestConstants.PR_TEST1 + "_name")));
		project1.put(JSONUtilConstants.CHILDREN, data1);
		projects1.add(project1);

		Map<String, Object> project2 = new HashMap<>();
		project2.put(JSONUtilConstants.ID, TestConstants.TEST2);
		project2.put(JSONUtilConstants.VID, TestConstants.PR_TEST2);
		project2.put(JSONUtilConstants.FIELDS,
				new HashMap<>(Map.of(JSONUtilConstants.VID, TestConstants.PR_TEST2, TestConstants.TESTPRFIELDKEY1,
						TestConstants.PR_TEST2VALUE1, TestConstants.TESTPRFIELDKEY2,
						TestConstants.PR_TEST2 + "_name")));
		project2.put(JSONUtilConstants.CHILDREN, new HashMap<>());
		projects1.add(project2);

		Map<String, Object> lineup3 = new HashMap<>();
		lineup3.put(JSONUtilConstants.ID, (String) null);
		lineup3.put(JSONUtilConstants.VID, (String) null);
		lineup3.put(JSONUtilConstants.FIELDS, new HashMap<>());
		lineups3.add(lineup3);

		Map<String, Object> project3 = new HashMap<>();
		project3.put(JSONUtilConstants.ID, TestConstants.TEST3);
		project3.put(JSONUtilConstants.VID, TestConstants.PR_TEST3);
		project3.put(JSONUtilConstants.FIELDS, new HashMap<>(Map.of(JSONUtilConstants.VID, TestConstants.PR_TEST3,
				TestConstants.TESTPRFIELDKEY1, TestConstants.PR_TEST3VALUE1, TestConstants.TESTPRFIELDKEY2, "null")));
		project3.put(JSONUtilConstants.CHILDREN, data6);
		projects1.add(project3);

		Map<String, Object> project4 = new HashMap<>();
		project4.put(JSONUtilConstants.VID, TestConstants.PR_TEST4);
		project4.put(JSONUtilConstants.FIELDS, new HashMap<>(Map.of(JSONUtilConstants.VID, TestConstants.TEST4)));
		projects1.add(project4);

		assetHierarchy = new ArrayList<>();
		assetHierarchy.add(data);
	}

	@Test
	@DisplayName("Test filterList1 -- Check filter hierarchy with 2 top level assets")
	void testFilterList_Positive1() {
		List<Map<String, Object>> output = JSONUtil.filterList(assetHierarchy,
				List.of(TestConstants.PR_TEST1, TestConstants.PR_TEST2));
		String outputStr = new JSONArray(output).toString();
		assertTrue(outputStr.contains(TestConstants.PR_TEST1));
		assertTrue(outputStr.contains(TestConstants.PL_TEST1));
		assertFalse(outputStr.contains(TestConstants.PR_TEST3));
	}

	@Test
	@DisplayName("Test filterList2 -- Check filter hierarchy with bottom level asset")
	void testFilterList_Positive2() {
		List<Map<String, Object>> output = JSONUtil.filterList(assetHierarchy, List.of(TestConstants.MC_TEST1));
		String outputStr = new JSONArray(output).toString();
		assertTrue(outputStr.contains(TestConstants.PR_TEST1));
		assertTrue(outputStr.contains(TestConstants.PL_TEST1));
		assertTrue(outputStr.contains(TestConstants.TR_TEST1));
		assertTrue(outputStr.contains(TestConstants.LN_TEST1));
		assertTrue(outputStr.contains(TestConstants.MC_TEST1));
		assertFalse(outputStr.contains(TestConstants.PR_TEST2));
		assertFalse(outputStr.contains(TestConstants.MC_TEST2));
	}

	@Test
	@DisplayName("Test filterList3 -- Null Vid")
	void testFilterList_Negative1() {
		List<Map<String, Object>> output = JSONUtil.filterList(assetHierarchy, null);
		assertTrue(output.isEmpty());
	}

	@Test
	@DisplayName("Test filterList4 -- Null Asset")
	void testFilterList_Negative2() {
		List<Map<String, Object>> children = new ArrayList<>();
		children.add(null);
		List<Map<String, Object>> output = JSONUtil.filterList(children, List.of("temp"));
		assertTrue(output.isEmpty());
	}

	@Test
	@DisplayName("Test filterList5 -- Empty Asset")
	void testFilterList_Negative3() {
		List<Map<String, Object>> output = JSONUtil.filterList(List.of(new HashMap<>()),
				List.of(TestConstants.PR_TEST4));
		assertTrue(output.isEmpty());
	}

	@Test
	@DisplayName("Test filterList6 -- Invalid vid")
	void testFilterList_Negative4() {
		List<Map<String, Object>> output = JSONUtil.filterList(assetHierarchy, List.of("temp"));
		assertTrue(output.isEmpty());
	}

	@Test
	@DisplayName("Test addDisplayName1 -- Check added display name")
	void testAddDisplayName_Positive1() {
		JSONUtil.addDisplayName(assetHierarchy, false);
		String output = new JSONArray(assetHierarchy).toString();
		assertTrue(output.contains("\"displayName\":\"" + TestConstants.PR_TEST1 + "_name\""));
		assertTrue(output.contains("\"displayName\":\"" + TestConstants.PL_TEST1 + "_name\""));
		assertTrue(output.contains("\"displayName\":\"" + TestConstants.TR_TEST1 + "_name\""));
		assertTrue(output.contains("\"displayName\":\"" + TestConstants.LN_TEST1 + "_name (TEST1)\""));
		assertTrue(output.contains("\"displayName\":\"" + TestConstants.MC_TEST1 + "_name (TEST1)\""));
		assertTrue(output.contains("\"fields\":{"));
	}

	@Test
	@DisplayName("Test addDisplayName2 -- Check compact")
	void testAddDisplayName_Positive2() {
		JSONUtil.addDisplayName(assetHierarchy, true);
		String output = new JSONArray(assetHierarchy).toString();
		assertFalse(output.contains("\"fields\":{"));
	}

	@Test
	@DisplayName("Test addDisplayName3 -- Null Vid")
	void testAddDisplayName_Negative1() {
		try (MockedStatic<JSONUtil> jsonUtilMock = mockStatic(JSONUtil.class, CALLS_REAL_METHODS)) {
			JSONUtil.addDisplayName(null, true);
			jsonUtilMock.verify(() -> JSONUtil.addDisplayName(null, true));
		}
	}

	@Test
	@DisplayName("Test addDisplayName4 -- Null Asset")
	void testAddDisplayName_Negative2() {
		List<Map<String, Object>> children = new ArrayList<>();
		children.add(null);
		JSONUtil.addDisplayName(children, true);
		assertEquals(null, children.get(0));
	}

	@Test
	@DisplayName("Test addDisplayName5 -- Empty Asset hierarchy")
	void testAddDisplayName_Negative3() {
		List<Map<String, Object>> children = new ArrayList<>();
		children.add(new HashMap<>());
		JSONUtil.addDisplayName(children, true);
		assertNotEquals(null, children.get(0));
	}

	@Test
	@DisplayName("Test addTechEquipCodeAndEnabledService1 -- Check TechEquipEnabledServices acculumation")
	void testAddTechEquipCodeAndEnabledService_Positive1() {
		JSONUtil.addDisplayName(assetHierarchy, false);
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addTechEquipCodeAndEnabledService(assetHierarchy, null);
		String output = new JSONArray(assetHierarchy).toString();
		assertTrue(output.contains("\"" + JSONUtilConstants.EQUIPMENTCODES + "\":["));
		assertTrue(output.contains("\"" + JSONUtilConstants.TECHNOLOGYCODES + "\":["));
		assertTrue(output.contains("\"" + JSONUtilConstants.ENABLEDSERVICES + "\":["));
		assertTrue(output.contains("\"" + JSONUtilConstants.GIBSERIALNOS + "\":["));
		assertTrue(output.contains("\"" + JSONUtilConstants.SERIALNOS + "\":["));
	}

	@Test
	@DisplayName("Test removeNullParents1 -- Check Null Parent Removed")
	void testRemoveNullParents_Positive1() {
		JSONUtil.removeNullParents(assetHierarchy, null);
		String output = new JSONArray(assetHierarchy).toString();
		assertFalse(output.contains("\"" + JSONUtilConstants.VID + "\": null"));
	}

	@Test
	@DisplayName("Test removeNullParents2 -- Check Null Parent Removed -- Null Asset Hierarchy")
	void testRemoveNullParents_Negative1() {
		try (MockedStatic<JSONUtil> jsonUtilMock = mockStatic(JSONUtil.class, CALLS_REAL_METHODS)) {
			JSONUtil.removeNullParents(null, new HashMap<>());
			jsonUtilMock.verify(() -> JSONUtil.removeNullParents(null, new HashMap<>()));
		}
	}

	@Test
	@DisplayName("Test removeNullParents3 -- Check Null Parent Removed -- Null Children")
	void testRemoveNullParents_Negative2() {
		List<Map<String, Object>> children = new ArrayList<>();
		children.add(null);
		JSONUtil.removeNullParents(children, null);
		assertEquals(null, children.get(0));
	}

	@Test
	@DisplayName("Test addExtFieldToOutputMap1 -- Get External Field")
	void testAddExtFieldToOutputMap_Positive1() {
		Map<String, String> outputMap = new HashMap<>();
		JSONUtil.addExtFieldToOutputMap(assetHierarchy, outputMap, JSONUtilConstants.ID);
		assertEquals(TestConstants.TEST1, outputMap.get(TestConstants.PR_TEST1));
		assertEquals(null, outputMap.get(null));
		assertEquals(TestConstants.TEST2, outputMap.get(TestConstants.MC_TEST2));
		assertEquals(TestConstants.TEST3, outputMap.get(TestConstants.MC_TEST3));
	}

	@Test
	@DisplayName("Test addExtFieldToOutputMap2 -- Get External Field -- Null Asset hierarchy")
	void testAddExtFieldToOutputMap_Negative1() {
		Map<String, String> outputMap = new HashMap<>();
		JSONUtil.addExtFieldToOutputMap(null, outputMap, JSONUtilConstants.ID);
		assertTrue(outputMap.isEmpty());
	}

	@Test
	@DisplayName("Test addExtFieldToOutputMap3 -- Get External Field -- Empty Asset hierarchy")
	void testAddExtFieldToOutputMap_Negative2() {
		Map<String, String> outputMap = new HashMap<>();
		JSONUtil.addExtFieldToOutputMap(new ArrayList<>(), outputMap, JSONUtilConstants.ID);
		assertTrue(outputMap.isEmpty());
	}

	@Test
	@DisplayName("Test addFieldsAndEnabledServicesToMap1 -- Get Field and Enabled Service -- Project Vid")
	void testAddFieldsAndEnabledServicesToMap_Positive1() {
		Map<String, Map<String, Set<String>>> outputMap = new HashMap<>();
		JSONUtil.addDisplayName(assetHierarchy, false);
		JSONUtil.addTechEquipCodeAndEnabledService(assetHierarchy, null);
		JSONUtil.addFieldsAndEnabledServicesToMap(assetHierarchy, outputMap);
		assertEquals(Set.of("abc1", "abc2", "cde1", "cde2"),
				outputMap.get(TestConstants.PR_TEST1).get(JSONUtilConstants.ENABLEDSERVICES));
		assertEquals(Set.of(TestConstants.MC_TEST1 + "_eqCode", TestConstants.MC_TEST2 + "_eqCode"),
				outputMap.get(TestConstants.PR_TEST1).get(JSONUtilConstants.EQUIPMENTCODES));
		assertEquals(
				Set.of(TestConstants.MC_TEST1 + "_techCode", TestConstants.MC_TEST2 + "_techCode",
						TestConstants.MC_TEST3 + "_techCode"),
				outputMap.get(TestConstants.PR_TEST1).get(JSONUtilConstants.TECHNOLOGYCODES));
		assertEquals(
				Set.of(TestConstants.MC_TEST1 + "_gibSerialNo", TestConstants.MC_TEST2 + "_gibSerialNo",
						TestConstants.MC_TEST3 + "_gibSerialNo"),
				outputMap.get(TestConstants.PR_TEST1).get(JSONUtilConstants.GIBSERIALNOS));
		assertEquals(
				Set.of(TestConstants.MC_TEST1 + "_serialNo", TestConstants.MC_TEST2 + "_serialNo",
						TestConstants.MC_TEST3 + "_serialNo"),
				outputMap.get(TestConstants.PR_TEST1).get(JSONUtilConstants.SERIALNOS));
		assertEquals(Set.of(TestConstants.MC_TEST1 + "_lowNox"),
				outputMap.get(TestConstants.PR_TEST1).get(JSONUtilConstants.LOWNOXS));
	}

	@Test
	@DisplayName("Test addFieldsAndEnabledServicesToMap2 -- Get Field and Enabled Service -- Plant Vid")
	void testAddFieldsAndEnabledServicesToMap_Positive2() {
		Map<String, Map<String, Set<String>>> outputMap = new HashMap<>();
		JSONUtil.addDisplayName(assetHierarchy, false);
		JSONUtil.addTechEquipCodeAndEnabledService(assetHierarchy, null);
		JSONUtil.addFieldsAndEnabledServicesToMap(assetHierarchy, outputMap);
		assertEquals(Set.of("abc1", "abc2", "cde1", "cde2"),
				outputMap.get(TestConstants.PL_TEST1).get(JSONUtilConstants.ENABLEDSERVICES));
		assertEquals(Set.of(TestConstants.MC_TEST1 + "_eqCode", TestConstants.MC_TEST2 + "_eqCode"),
				outputMap.get(TestConstants.PL_TEST1).get(JSONUtilConstants.EQUIPMENTCODES));
		assertEquals(Set.of(TestConstants.MC_TEST1 + "_techCode", TestConstants.MC_TEST2 + "_techCode"),
				outputMap.get(TestConstants.PL_TEST1).get(JSONUtilConstants.TECHNOLOGYCODES));
		assertEquals(Set.of(TestConstants.MC_TEST1 + "_gibSerialNo", TestConstants.MC_TEST2 + "_gibSerialNo"),
				outputMap.get(TestConstants.PL_TEST1).get(JSONUtilConstants.GIBSERIALNOS));
		assertEquals(Set.of(TestConstants.MC_TEST1 + "_serialNo", TestConstants.MC_TEST2 + "_serialNo"),
				outputMap.get(TestConstants.PL_TEST1).get(JSONUtilConstants.SERIALNOS));
		assertEquals(Set.of(TestConstants.MC_TEST1 + "_lowNox"),
				outputMap.get(TestConstants.PL_TEST1).get(JSONUtilConstants.LOWNOXS));
	}

	@Test
	@DisplayName("Test addFieldsAndEnabledServicesToMap3 -- Get Field and Enabled Service -- Null Asset hierarchy")
	void testAddFieldsAndEnabledServicesToMap_Negative1() {
		Map<String, Map<String, Set<String>>> outputMap = new HashMap<>();
		JSONUtil.addFieldsAndEnabledServicesToMap(null, outputMap);
		assertTrue(outputMap.isEmpty());
	}

	@Test
	@DisplayName("Test addFieldsAndEnabledServicesToMap4 -- Get Field and Enabled Service -- Empty Asset hierarchy")
	void testAddFieldsAndEnabledServicesToMap_Negative2() {
		Map<String, Map<String, Set<String>>> outputMap = new HashMap<>();
		JSONUtil.addFieldsAndEnabledServicesToMap(new ArrayList<>(), outputMap);
		assertTrue(outputMap.isEmpty());
	}

	@Test
	@DisplayName("Test addAssetsToMap1 -- Get Assets Map -- with Vid")
	void testAddAssetsToMap_Positive1() {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addAssetsToMap(assetHierarchy, TestConstants.MC_TEST1, true, outputMap);
		assertEquals(JSONUtilConstants.LEVEL_MACHINES, outputMap.get(JSONUtilConstants.CURRENTLEVEL));
		assertEquals(JSONUtilConstants.LEVEL_LINEUPS, outputMap.get(JSONUtilConstants.PREVIOUSLEVEL));
		assertEquals(null, outputMap.get(JSONUtilConstants.NEXTLEVEL));
		assertTrue((boolean) outputMap.get(JSONUtilConstants.MATCHFOUND));
		assertEquals(List.of(TestConstants.PR_TEST1), outputMap.get(JSONUtilConstants.LEVEL_PROJECTS));
		assertEquals(List.of(TestConstants.PL_TEST1), outputMap.get(JSONUtilConstants.LEVEL_PLANTS));
		assertEquals(List.of(TestConstants.TR_TEST1), outputMap.get(JSONUtilConstants.LEVEL_TRAINS));
		assertEquals(List.of(TestConstants.LN_TEST1), outputMap.get(JSONUtilConstants.LEVEL_LINEUPS));
		assertEquals(List.of(TestConstants.MC_TEST1), outputMap.get(JSONUtilConstants.LEVEL_MACHINES));
	}

	@Test
	@DisplayName("Test addAssetsToMap2 -- Get Assets Map -- without Vid")
	void testAddAssetsToMap_Positive2() {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addAssetsToMap(assetHierarchy, null, true, outputMap);
		assertEquals(null, outputMap.get(JSONUtilConstants.PREVIOUSLEVEL));
		assertEquals(null, outputMap.get(JSONUtilConstants.CURRENTLEVEL));
		assertEquals(JSONUtilConstants.LEVEL_PROJECTS, outputMap.get(JSONUtilConstants.NEXTLEVEL));
		assertEquals(
				List.of(TestConstants.PR_TEST1, TestConstants.PR_TEST2, TestConstants.PR_TEST3, TestConstants.PR_TEST4),
				outputMap.get(JSONUtilConstants.LEVEL_PROJECTS));
		assertEquals(
				List.of(TestConstants.MC_TEST1, TestConstants.MC_TEST2, TestConstants.MC_TEST3, TestConstants.MC_TEST4),
				outputMap.get(JSONUtilConstants.LEVEL_MACHINES));
	}

	@Test
	@DisplayName("Test addAssetsToMap3 -- Get Assets Map -- Project Vid")
	void testAddAssetsToMap_Positive3() {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addAssetsToMap(assetHierarchy, TestConstants.PR_TEST2, true, outputMap);
		assertEquals(null, outputMap.get(JSONUtilConstants.PREVIOUSLEVEL));
		assertEquals(JSONUtilConstants.LEVEL_PROJECTS, outputMap.get(JSONUtilConstants.CURRENTLEVEL));
		assertEquals(null, outputMap.get(JSONUtilConstants.NEXTLEVEL));
		assertEquals(List.of(TestConstants.PR_TEST2), outputMap.get(JSONUtilConstants.LEVEL_PROJECTS));
		assertEquals(null, outputMap.get(JSONUtilConstants.LEVEL_PLANTS));
	}

	@Test
	@DisplayName("Test addAssetsToMap4 -- Get Assets Map -- Train Vid")
	void testAddAssetsToMap_Positive4() {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addAssetsToMap(assetHierarchy, TestConstants.TR_TEST1, true, outputMap);
		assertEquals(JSONUtilConstants.LEVEL_PLANTS, outputMap.get(JSONUtilConstants.PREVIOUSLEVEL));
		assertEquals(JSONUtilConstants.LEVEL_TRAINS, outputMap.get(JSONUtilConstants.CURRENTLEVEL));
		assertEquals(JSONUtilConstants.LEVEL_LINEUPS, outputMap.get(JSONUtilConstants.NEXTLEVEL));
		assertEquals(List.of(TestConstants.PL_TEST1), outputMap.get(JSONUtilConstants.LEVEL_PLANTS));
		assertEquals(List.of(TestConstants.TR_TEST1), outputMap.get(JSONUtilConstants.LEVEL_TRAINS));
		assertEquals(List.of(TestConstants.LN_TEST1), outputMap.get(JSONUtilConstants.LEVEL_LINEUPS));
	}

	@Test
	@DisplayName("Test addAssetsToMap5 -- Get Assets Map -- Get Asset Id")
	void testAddAssetsToMap_Positive5() {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addAssetsToMap(assetHierarchy, TestConstants.PL_TEST2, false, outputMap);
		assertEquals(JSONUtilConstants.LEVEL_PROJECTS, outputMap.get(JSONUtilConstants.PREVIOUSLEVEL));
		assertEquals(JSONUtilConstants.LEVEL_PLANTS, outputMap.get(JSONUtilConstants.CURRENTLEVEL));
		assertEquals(JSONUtilConstants.LEVEL_MACHINES, outputMap.get(JSONUtilConstants.NEXTLEVEL));
		assertEquals(List.of(TestConstants.TEST2), outputMap.get(JSONUtilConstants.LEVEL_PLANTS));
		assertEquals(List.of(TestConstants.TEST3), outputMap.get(JSONUtilConstants.LEVEL_MACHINES));
	}

	@Test
	@DisplayName("Test addMultipleFieldsOfLevelUnderVid1 -- Get Multiple Fields -- Only Level")
	void testAddMultipleFieldsOfLevelUnderVid_Positive1() {
		List<Map<String, Object>> outputList = new ArrayList<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addMultipleFieldsOfLevelUnderVid(assetHierarchy, null, JSONUtilConstants.LEVEL_TRAINS,
				List.of(JSONUtilConstants.TECHNOLOGYCODEOG, JSONUtilConstants.EQUIPMENTCODE),
				List.of(JSONUtilConstants.ID), outputList, false);
		assertTrue(outputList.get(0).containsKey(JSONUtilConstants.TECHNOLOGYCODEOG));
		assertTrue(outputList.get(0).containsKey(JSONUtilConstants.EQUIPMENTCODE));
		assertTrue(outputList.get(0).containsKey(JSONUtilConstants.ID));
	}

	@Test
	@DisplayName("Test addMultipleFieldsOfLevelUnderVid2 -- Get Multiple Fields -- vid and Level")
	void testAddMultipleFieldsOfLevelUnderVid_Positive2() {
		List<Map<String, Object>> outputList = new ArrayList<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addMultipleFieldsOfLevelUnderVid(assetHierarchy, TestConstants.PL_TEST2,
				JSONUtilConstants.LEVEL_MACHINES,
				List.of(JSONUtilConstants.TECHNOLOGYCODEOG, JSONUtilConstants.EQUIPMENTCODE),
				List.of(JSONUtilConstants.ID), outputList, false);
		assertNotEquals(0, outputList.size());
		assertNotEquals(1, outputList.size());
		assertTrue(outputList.get(0).containsKey(JSONUtilConstants.TECHNOLOGYCODEOG));
		assertTrue(outputList.get(0).containsKey(JSONUtilConstants.EQUIPMENTCODE));
		assertTrue(outputList.get(0).containsKey(JSONUtilConstants.ID));
	}

	@Test
	@DisplayName("Test addMultipleFieldsOfLevelUnderVid3 -- Check with Null Asset Hierarchy")
	void testAddMultipleFieldsOfLevelUnderVid_Negative1() {
		List<Map<String, Object>> outputList = new ArrayList<>();
		JSONUtil.addMultipleFieldsOfLevelUnderVid(null, null, JSONUtilConstants.LEVEL_PROJECTS,
				List.of(JSONUtilConstants.TECHNOLOGYCODEOG, JSONUtilConstants.EQUIPMENTCODE),
				List.of(JSONUtilConstants.ID), outputList, false);
		assertTrue(outputList.isEmpty());
	}

	@Test
	@DisplayName("Test addMultipleFieldsOfLevelUnderVid4 -- Check with Null Asset Hierarchy")
	void testAddMultipleFieldsOfLevelUnderVid_Negative2() {
		List<Map<String, Object>> outputList = new ArrayList<>();
		JSONUtil.addMultipleFieldsOfLevelUnderVid(null, null, JSONUtilConstants.LEVEL_PROJECTS,
				List.of(JSONUtilConstants.TECHNOLOGYCODEOG, JSONUtilConstants.EQUIPMENTCODE),
				List.of(JSONUtilConstants.ID), outputList, false);
		assertTrue(outputList.isEmpty());
	}

	@Test
	@DisplayName("Test addFieldsOfVidToMap1 -- Get Fields of Vid -- Current Asset Fields")
	void testAddFieldsOfVidToMap_Positive1() {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addFieldsOfVidToMap(assetHierarchy, TestConstants.MC_TEST1,
				List.of(TestConstants.TESTMCFIELDKEY1, TestConstants.TESTMCFIELDKEY2), outputMap, false, null);
		assertEquals(TestConstants.MC_TEST1VALUE1, outputMap.get(TestConstants.TESTMCFIELDKEY1));
		assertEquals(TestConstants.MC_TEST1 + "_name", outputMap.get(TestConstants.TESTMCFIELDKEY2));
	}

	@Test
	@DisplayName("Test addFieldsOfVidToMap2 -- Get Fields of Vid -- Parent Asset Fields")
	void testAddFieldsOfVidToMap_Positive2() {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addFieldsOfVidToMap(assetHierarchy, TestConstants.MC_TEST1,
				List.of(TestConstants.TESTLNFIELDKEY1, TestConstants.TESTLNFIELDKEY2, JSONUtilConstants.ID), outputMap,
				true, null);
		assertEquals(TestConstants.LN_TEST1VALUE1, outputMap.get(TestConstants.TESTLNFIELDKEY1));
		assertEquals(TestConstants.LN_TEST1 + "_name", outputMap.get(TestConstants.TESTLNFIELDKEY2));
		assertEquals(TestConstants.TEST1, outputMap.get(JSONUtilConstants.ID));
	}

	@Test
	@DisplayName("Test addFieldsOfVidToMap3 -- Check with Null Vid")
	void testAddFieldsOfVidToMap_Negative1() {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.addFieldsOfVidToMap(assetHierarchy, null,
				List.of(TestConstants.TESTPRFIELDKEY1, TestConstants.TESTPRFIELDKEY2), outputMap, false, null);
		assertTrue(outputMap.isEmpty());
	}

	@Test
	@DisplayName("Test addFieldsOfVidToMap4 -- Get Fields of Vid -- Get Parent fields with Project vid")
	void testAddFieldsOfVidToMap_Negative2() {
		Map<String, Object> outputMap = new HashMap<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.addFieldsOfVidToMap(assetHierarchy, TestConstants.PR_TEST1,
				List.of(TestConstants.TESTPRFIELDKEY1, TestConstants.TESTPRFIELDKEY2), outputMap, true, null);
		assertEquals(TestConstants.PR_TEST1VALUE1, outputMap.get(TestConstants.TESTPRFIELDKEY1));
		assertEquals(TestConstants.PR_TEST1 + "_name", outputMap.get(TestConstants.TESTPRFIELDKEY2));
	}

	@Test
	@DisplayName("Test getSubTree1 -- Get SubTree of Vid")
	void testGetSubTree_Positive1() {
		List<Map<String, Object>> output = JSONUtil.getSubTree(assetHierarchy, TestConstants.PL_TEST1);
		String outputStr = new JSONArray(output).toString();
		assertFalse(outputStr.contains(TestConstants.PR_TEST1));
		assertFalse(outputStr.contains(TestConstants.PL_TEST1));
		assertTrue(outputStr.contains(TestConstants.TR_TEST1));
		assertTrue(outputStr.contains(TestConstants.LN_TEST1));
		assertTrue(outputStr.contains(TestConstants.MC_TEST1));
		assertTrue(outputStr.contains(TestConstants.MC_TEST2));
	}

	@Test
	@DisplayName("Test getSubTree2 -- Get SubTree of Vid")
	void testGetSubTree_Positive2() {
		List<Map<String, Object>> output = JSONUtil.getSubTree(assetHierarchy, TestConstants.PR_TEST3);
		String outputStr = new JSONArray(output).toString();
		assertFalse(outputStr.contains(TestConstants.PR_TEST1));
		assertFalse(outputStr.contains(TestConstants.PR_TEST3));
		assertFalse(outputStr.contains("null"));
	}

	@Test
	@DisplayName("Test getSubTree3 -- Check with Null Vid")
	void testGetSubTree_Negative1() {
		List<Map<String, Object>> output = JSONUtil.getSubTree(assetHierarchy, null);
		assertTrue(output.isEmpty());
	}

	@Test
	@DisplayName("Test getSubTree4 -- Check with Null Vid")
	void testGetSubTree_Negative2() {
		List<Map<String, Object>> children = new ArrayList<>();
		children.add(null);
		List<Map<String, Object>> output = JSONUtil.getSubTree(children, "temp");
		assertTrue(output.isEmpty());
	}

	@Test
	@DisplayName("Test getIdsForVid -- Get Ids of Vid")
	void testGetIdsForVid_Positive1() {
		List<String> outputList = new ArrayList<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.getIdsForVid(assetHierarchy,
				List.of(TestConstants.MC_TEST1, TestConstants.PR_TEST3, TestConstants.LN_TEST1), outputList);
		assertEquals(List.of(TestConstants.TEST1, TestConstants.TEST1, TestConstants.TEST3), outputList);
	}

	@Test
	@DisplayName("Test validateVid1 -- Check valid Vid or not -- Valid vid")
	void testValidateVid_Positive1() {
		Map<String, Boolean> outputMap = new HashMap<>();
		JSONUtil.removeNullParents(assetHierarchy, null);
		JSONUtil.validateVid(assetHierarchy, TestConstants.PR_TEST2, outputMap);
		assertTrue(outputMap.get(JSONUtilConstants.MATCHFOUND));
	}

	@Test
	@DisplayName("Test validateVid2 -- Check valid Vid or not -- Null vid")
	void testValidateVid_Negative1() {
		Map<String, Boolean> outputMap = new HashMap<>();
		JSONUtil.validateVid(assetHierarchy, null, outputMap);
		assertTrue(outputMap.isEmpty());
	}

	@Test
	@DisplayName("Test isAnyNull1 -- Check if any object null - with null object")
	void testIsAnyNull_Positive1() {
		assertTrue(JSONUtil.isAnyNull("abc", null, "def"));
	}

	@Test
	@DisplayName("Test isAnyNull2 -- Check if any object null - without Null Object")
	void testIsAnyNull_Negative1() {
		assertFalse(JSONUtil.isAnyNull("abc", "def", "ghi"));
	}

}