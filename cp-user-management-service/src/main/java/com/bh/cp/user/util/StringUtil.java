package com.bh.cp.user.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class StringUtil {

	private static ThreadLocal<String> threadLocal;

	private StringUtil() {
		super();
	}

	public static boolean isEmptyString(String input) {
		return input == null || "".equals(input);
	}

	public static boolean isNull(Object input) {
		return input == null || "".equals(input) || "null".equalsIgnoreCase(input.toString());
	}

	public static String replaceAll(String input, Map<String, String> valuesMap) {
		if (input == null) {
			return input;
		}
		String copy = input;
		if (valuesMap != null) {
			for (Map.Entry<String, String> entry : valuesMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (key != null && value != null) {
					copy = copy.replaceAll(key, value);
				}
			}
		}
		return copy;
	}

	public static Map<String, String> splitKeyValues(String text, String primaryDelimiter, String secondaryDelimiter) {
		Map<String, String> valuesMap = new HashMap<>();
		if (text == null) {
			return valuesMap;
		}
		String[] primaryValues = text.split(primaryDelimiter);
		for (int primaryCount = 0; primaryCount < primaryValues.length; primaryCount++) {
			String[] secondaryValues = primaryValues[primaryCount].split(secondaryDelimiter);
			valuesMap.put(secondaryValues[0], secondaryValues[1]);
		}
		return valuesMap;
	}

	public static String toCSV(List<String> listOfValues, String separator, String surroundValueWith) {
		StringJoiner joiner = new StringJoiner(separator);
		for (String value : listOfValues) {
			if (value != null) {
				joiner.add(surroundValueWith + value + surroundValueWith);
			}
		}
		return joiner.toString();
	}

	public static String limitTo(String input, int maxCharCount, String appendString) {
		if (input == null) {
			return null;
		}
		if (input.length() < maxCharCount) {
			return input;
		}
		StringBuilder builder = new StringBuilder();
		return builder.append(input.substring(0, maxCharCount)).append(appendString).toString();
	}

	public static String limitChars(String input, int limit) {
		if (input == null) {
			return null;
		}
		if (input.length() <= limit) {
			return input;
		}
		return input.substring(0, limit);
	}

	public static boolean isEmptyList(List<String> input) {
		return input == null || input.contains("string") || input.isEmpty() || input.contains("");
	}

	public static boolean isEmptyCaseString(String input) {
		return input == null || input.equals("string") || "".equals(input);
	}

	public static String encodeString(String tags) {
		if (tags.contains("[")) {
			tags = tags.replace("[", "");
			tags = tags.replace("]", "");
			tags = tags.replace("\"", "");
		}
		return tags;
	}

	public static boolean isDateSearch(Map<String, Object> request) {
		return ((!request.containsKey("projectId") && (!request.containsKey("plantId"))
				&& (!request.containsKey("trainId")) && (!request.containsKey("lineupId"))
				&& (!request.containsKey("machineId")) && (request.containsKey("startDate"))
				&& (request.containsKey("endDate"))) || request.containsKey("caseNumber"));
	}

	public static String getUUID() {
		try {
			if (threadLocal == null) {
				threadLocal = new ThreadLocal<>();
			}
			threadLocal.set(UUID.randomUUID().toString() + System.nanoTime());
			return threadLocal.get();
		} finally {
			threadLocal.remove();
		}
	}
}