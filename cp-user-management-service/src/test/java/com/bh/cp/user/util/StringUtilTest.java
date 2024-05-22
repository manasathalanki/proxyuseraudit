package com.bh.cp.user.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class StringUtilTest {

	@Test
	void testIsEmptyStringSuccess() {
		assertFalse(StringUtil.isEmptyString("testString"));
		try (MockedStatic<StringUtil> stringUtil = Mockito.mockStatic(StringUtil.class)) {
			stringUtil.when(() -> StringUtil.isEmptyString("testString")).thenReturn(false);
			assertFalse(StringUtil.isEmptyString("testString"));
		}
		assertFalse(StringUtil.isEmptyString("testString"));
	}

	@Test
	void testIsEmptyStringNullFailure() {
		assertTrue(StringUtil.isEmptyString(null));
		try (MockedStatic<StringUtil> stringUtil = Mockito.mockStatic(StringUtil.class)) {
			stringUtil.when(() -> StringUtil.isEmptyString(null)).thenReturn(true);
			assertTrue(StringUtil.isEmptyString(null));
		}
		assertTrue(StringUtil.isEmptyString(null));
	}

	@Test
	void testIsEmptyStringEmptyFailure() {
		assertTrue(StringUtil.isEmptyString(""));
		try (MockedStatic<StringUtil> stringUtil = Mockito.mockStatic(StringUtil.class)) {
			stringUtil.when(() -> StringUtil.isEmptyString("")).thenReturn(true);
			assertTrue(StringUtil.isEmptyString(""));
		}
		assertTrue(StringUtil.isEmptyString(""));
	}

	@Test
	void testIsNullSuccess() {
		assertFalse(StringUtil.isNull(new Object()));
		try (MockedStatic<StringUtil> isNull = Mockito.mockStatic(StringUtil.class)) {
			isNull.when(() -> StringUtil.isNull(new Object())).thenReturn(false);
			assertFalse(StringUtil.isNull(new Object()));
		}
		assertFalse(StringUtil.isNull(new Object()));

	}

	@Test
	void testIsNullFailure() {
		assertTrue(StringUtil.isNull(null));
		try (MockedStatic<StringUtil> isNull = Mockito.mockStatic(StringUtil.class)) {
			isNull.when(() -> StringUtil.isNull(null)).thenReturn(true);
			assertTrue(StringUtil.isNull(null));
		}
		assertTrue(StringUtil.isNull(null));

	}

	@Test
	void testIsNullEmptyFailure() {
		Object input = new Object();
		input = "";
		assertTrue(StringUtil.isNull(input));
		try (MockedStatic<StringUtil> isNull = Mockito.mockStatic(StringUtil.class)) {
			isNull.when(() -> {
				Object inputData = new Object();
				inputData = "";
				StringUtil.isNull(inputData);
			}).thenReturn(true);
			assertTrue(StringUtil.isNull(input));
		}
		assertTrue(StringUtil.isNull(input));

	}

	@Test
	void testIsNullFailureNull() {
		Object input = new Object();
		input = "null";
		assertTrue(StringUtil.isNull(input.toString()));
		try (MockedStatic<StringUtil> isNull = Mockito.mockStatic(StringUtil.class)) {
			isNull.when(() -> {
				Object inputData = new Object();
				inputData = "null";
				StringUtil.isNull(inputData.toString());
			}).thenReturn(true);
			assertTrue(StringUtil.isNull(input.toString()));
		}
		assertTrue(StringUtil.isNull(input.toString()));

	}

	@Test
	void testLimitChars() {
		assertNotNull(StringUtil.limitChars("test", 3));
		try (MockedStatic<StringUtil> limitChars = mockStatic(StringUtil.class)) {
			limitChars.when(() -> StringUtil.limitChars("test", 3)).thenReturn("test");
			assertNotNull(StringUtil.limitChars("test", 3));
		}
		assertNotNull(StringUtil.limitChars("test", 3));
	}

	@Test
	void testLimitCharsGreater() {
		assertNotNull(StringUtil.limitChars("test", 4));
		try (MockedStatic<StringUtil> limitChars = mockStatic(StringUtil.class)) {
			limitChars.when(() -> StringUtil.limitChars("test", 4)).thenReturn("test");
			assertNotNull(StringUtil.limitChars("test", 4));
		}
		assertNotNull(StringUtil.limitChars("test", 4));
	}

	@Test
	void testLimitCharsNull() {
		assertNull(StringUtil.limitChars(null, 4));
		try (MockedStatic<StringUtil> limitChars = mockStatic(StringUtil.class)) {
			limitChars.when(() -> StringUtil.limitChars(null, 4)).thenReturn(null);
			assertNull(StringUtil.limitChars(null, 4));
		}
		assertNull(StringUtil.limitChars(null, 4));
	}

	@Nested
	class TestReplaceAll {

		@Test
		@DisplayName("Test replaceAll1 -- Check Placeholder Replacement")
		void testReplaceAll_Positive1() {
			String output = StringUtil.replaceAll("Test ReplaceAll -- <TEST_PLACEHOLDER1> -- <TEST_PLACEHOLDER2>",
					Map.of("<TEST_PLACEHOLDER1>", "REPLACED_TEXT1", "<TEST_PLACEHOLDER2>", "REPLACED_TEXT2"));
			assertEquals("Test ReplaceAll -- REPLACED_TEXT1 -- REPLACED_TEXT2", output);
		}

		@Test
		@DisplayName("Test replaceAll2 -- Null Input")
		void testReplaceAll_Negative1() {
			String output = StringUtil.replaceAll(null,
					Map.of("<TEST_PLACEHOLDER1>", "REPLACED_TEXT1", "<TEST_PLACEHOLDER2>", "REPLACED_TEXT2"));
			assertEquals(null, output);
		}

		@Test
		@DisplayName("Test replaceAll3 -- Null ValuesMap")
		void testReplaceAll_Negative2() {
			String output = StringUtil.replaceAll("Test ReplaceAll", null);
			assertEquals("Test ReplaceAll", output);
		}

		@Test
		@DisplayName("Test replaceAll4 -- Null Key in ValuesMap")
		void testReplaceAll_Negative3() {
			Map<String, String> valuesMap = new HashMap<>();
			valuesMap.put("<TEST_PLACEHOLDER1>", "REPLACED_TEXT1");
			valuesMap.put(null, "REPLACED_TEXT2");
			String output = StringUtil.replaceAll("Test ReplaceAll -- <TEST_PLACEHOLDER1> -- <TEST_PLACEHOLDER2>",
					valuesMap);
			assertEquals("Test ReplaceAll -- REPLACED_TEXT1 -- <TEST_PLACEHOLDER2>", output);
		}

		@Test
		@DisplayName("Test replaceAll5 -- Null Value in ValuesMap")
		void testReplaceAll_Negative4() {
			Map<String, String> valuesMap = new HashMap<>();
			valuesMap.put("<TEST_PLACEHOLDER1>", "REPLACED_TEXT1");
			valuesMap.put("<TEST_PLACEHOLDER2>", null);
			String output = StringUtil.replaceAll("Test ReplaceAll -- <TEST_PLACEHOLDER1> -- <TEST_PLACEHOLDER2>",
					valuesMap);
			assertEquals("Test ReplaceAll -- REPLACED_TEXT1 -- <TEST_PLACEHOLDER2>", output);
		}

	}

	@ParameterizedTest
	@CsvSource({ "[\"test1\"],test1", "test2,test2" })
	@DisplayName("Test encodeString -- Check replacement with valid and invalid values")
	void testEncodeString_Positive(String msg, String expected) {
		assertEquals(expected, StringUtil.encodeString(msg));
	}

	@Test
	void testLimitTo() {
		assertNotNull(StringUtil.limitTo("test", 3, "test"));
		try (MockedStatic<StringUtil> limitTo = Mockito.mockStatic(StringUtil.class)) {
			limitTo.when(() -> StringUtil.limitTo("test", 3, "test")).thenReturn("testtest");
			assertNotNull(StringUtil.limitTo("test", 3, "test"));
		}
		assertNotNull(StringUtil.limitTo("test", 3, "test"));
	}

	@Test
	void testLimitToGreater() {
		assertNotNull(StringUtil.limitTo("test", 5, "test"));
		try (MockedStatic<StringUtil> limitTo = Mockito.mockStatic(StringUtil.class)) {
			limitTo.when(() -> StringUtil.limitTo("test", 5, "test")).thenReturn("test");
			assertNotNull(StringUtil.limitTo("test", 5, "test"));
		}
		assertNotNull(StringUtil.limitTo("test", 5, "test"));
	}

	@Test
	void testLimitToNull() {
		assertNull(StringUtil.limitTo(null, 3, "test"));
		try (MockedStatic<StringUtil> limitTo = Mockito.mockStatic(StringUtil.class)) {
			limitTo.when(() -> StringUtil.limitTo(null, 3, "test")).thenReturn(null);
			assertNull(StringUtil.limitTo(null, 3, "test"));
		}
		assertNull(StringUtil.limitTo(null, 3, "test"));
	}

	@Test
	void testSplitKeyValuesTextNull() {
		Map<String, String> valuesMap = new HashMap<>();
		assertNotNull(StringUtil.splitKeyValues(null, ",", ","));
		try (MockedStatic<StringUtil> splitKeyValues = Mockito.mockStatic(StringUtil.class)) {
			splitKeyValues.when(() -> {
				StringUtil.splitKeyValues(null, ",", ",");
			}).thenReturn(valuesMap);
			assertNotNull(StringUtil.splitKeyValues(null, ",", ","));
		}
		assertNotNull(StringUtil.splitKeyValues(null, ",", ","));
	}

	@Test
	void testSplitKeyValuesTextNotNull() {
		Map<String, String> valuesMap = new HashMap<>();
		valuesMap.put("data", "data");
		assertNotNull(StringUtil.splitKeyValues("datadata,datadata,datadata,datadata", ",", "a"));
		try (MockedStatic<StringUtil> splitKeyValues = Mockito.mockStatic(StringUtil.class)) {
			splitKeyValues.when(() -> {
				StringUtil.splitKeyValues("datadata,datadata,datadata,datadata", ",", "a");
			}).thenReturn(valuesMap);
			assertNotNull(StringUtil.splitKeyValues("datadata,datadata,datadata,datadata", ",", "a"));
		}
		assertNotNull(StringUtil.splitKeyValues("datadata,datadata,datadata,datadata", ",", "a"));
	}

	@Test
	@DisplayName("Test toCSV1 -- Check TEXT Format")
	void testToCSV_Positive1() {
		List<String> listOfValues = new ArrayList<>();
		listOfValues.add("data1");
		listOfValues.add("data2");
		listOfValues.add("data3");
		String output = StringUtil.toCSV(listOfValues, ",", "\"");
		assertEquals("\"data1\",\"data2\",\"data3\"", output);
	}

	@Test
	@DisplayName("Test toCSV2 -- Check CSV Format")
	void testToCSV_Positive2() {
		List<String> listOfValues = new ArrayList<>();
		listOfValues.add("data1");
		listOfValues.add("data2");
		listOfValues.add(null);
		String output = StringUtil.toCSV(listOfValues, ",", "");
		assertEquals("data1,data2", output);
	}

	@Nested
	class TestIsEmptyLimit {

		@Test
		@DisplayName("Test isEmptyList1 -- Empty List")
		void testIsEmptyList_Positive1() {
			assertTrue(StringUtil.isEmptyList(List.of()));
		}

		@Test
		@DisplayName("Test isEmptyList2 -- Null")
		void testIsEmptyList_Positive2() {
			assertTrue(StringUtil.isEmptyList(null));
		}

		@Test
		@DisplayName("Test isEmptyList3 -- List contains ''")
		void testIsEmptyList_Positive3() {
			assertTrue(StringUtil.isEmptyList(List.of("")));
		}

		@Test
		@DisplayName("Test isEmptyList4 -- List contains 'string'")
		void testIsEmptyList_Positive4() {
			assertTrue(StringUtil.isEmptyList(List.of("string")));
		}

		@Test
		@DisplayName("Test isEmptyList5 -- Valid List")
		void testIsEmptyList_Negative1() {
			assertFalse(StringUtil.isEmptyList(List.of("Test")));
		}
	}

	@Nested
	class TestIsEmptyCaseString {

		@Test
		@DisplayName("Test isEmptyCaseString1 -- Empty String")
		void testIsEmptyCaseString_Positive1() {
			assertTrue(StringUtil.isEmptyCaseString(""));
		}

		@Test
		@DisplayName("Test isEmptyCaseString2 -- Null")
		void testIsEmptyCaseString_Positive2() {
			assertTrue(StringUtil.isEmptyCaseString(null));
		}

		@Test
		@DisplayName("Test isEmptyCaseString3 -- string")
		void testIsEmptyCaseString_Positive3() {
			assertTrue(StringUtil.isEmptyCaseString("string"));
		}

		@Test
		@DisplayName("Test isEmptyCaseString4 -- Test")
		void testIsEmptyCaseString_Negative1() {
			assertFalse(StringUtil.isEmptyCaseString("Test"));
		}
	}

	@Nested
	class TestIsDateSearch {

		@ParameterizedTest
		@CsvSource({ "projectId", "plantId", "trainId", "lineupId", "machineId", "startDate", "endDate" })
		@DisplayName("Test isDateSearch1 -- Only one Asset id/Date field")
		void testIsDateSearch_Positive1(String key) {
			Map<String, Object> request = new HashMap<>();
			request.put(key, "data");
			assertFalse(StringUtil.isDateSearch(request));
		}

		@Test
		@DisplayName("Test isDateSearch2 -- startDate and endDate fields")
		void testIsDateSearch_Positive2() {
			Map<String, Object> request = new HashMap<>();
			request.put("startDate", "data");
			request.put("endDate", "data");
			assertTrue(StringUtil.isDateSearch(request));
		}

		@Test
		@DisplayName("Test isDateSearch3 -- caseNumber field")
		void testIsDateSearch_Positive3() {
			Map<String, Object> request = new HashMap<>();
			request.put("caseNumber", "data");
			assertTrue(StringUtil.isDateSearch(request));
		}

	}

	@Test
	void testGetUUID() {
		assertNotNull(StringUtil.getUUID());
		try (MockedStatic<StringUtil> getUUID = Mockito.mockStatic(StringUtil.class)) {
			getUUID.when(() -> StringUtil.getUUID()).thenReturn("thread_name");
			assertNotNull(StringUtil.getUUID());
		}
		assertNotNull(StringUtil.getUUID());
	}

}
