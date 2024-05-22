package com.bh.cp.dashboard.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

public final class SimpleMapper {
	
	private static final String POST = "post";
	private static final String PRE = "pre";
	private SimpleMapper() {}
	
	static Logger logger = org.slf4j.LoggerFactory.getLogger(SimpleMapper.class);
	public static void map(Object from, Object to) {
		Method[] methods = from.getClass().getMethods();
		Class<?> toClass = to.getClass();
		for (Method method : methods) {
			String methodName = method.getName();
			if (!methodName.startsWith("get") || "getClass".equals(methodName)) {
				continue;
			}
			String setMethodName = "set" + methodName.substring(3);
			Class<?> returnType = method.getReturnType();
			try {
				toClass.getMethod(setMethodName, returnType).invoke(to, method.invoke(from));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		
		}
	}
	
	public static void map(Object from, Object to, String[] fields) {
		Class<?> toClass = to.getClass();
		for (String field: fields) {
			try {
				field = Character.toUpperCase(field.charAt(0)) + field.substring(1)  ;
				Method method = from.getClass().getMethod("get" + field);
				String setMethodName = "set" + field;
				Class<?> returnType = method.getReturnType();
				toClass.getMethod(setMethodName, returnType).invoke(to, method.invoke(from));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public static Map<Object, Object> getMap(Object from, String... excludeFields) {
		Map<Object, Object> fieldsMap = new HashMap<>();
		Method[] methods = from.getClass().getMethods();
		List<String> excludedFieldList = Arrays.asList(excludeFields);
		for (Method method : methods) {
			String methodName = method.getName();
			char result = methodName.charAt(3);
			String fieldName = Character.toLowerCase(result) + methodName.substring(4); 
			if (!methodName.startsWith("get") || "getClass".equals(methodName)|| excludeFields != null && excludedFieldList.contains(fieldName)) {
				continue;
			}
			try {
				fieldsMap.put(fieldName, method.invoke(from));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}		
		}
		return fieldsMap;
	}

	public static Map<Object, Object> diffObject(Object from, Object to, String... excludeKeys) {
		Map<Object, Object> fromMap = getMap(from);
		Map<Object, Object> tomMap = getMap(to);
		return diffMap(fromMap, tomMap, excludeKeys);
	}
	
	public static Map<Object, Object> diffMap(Map<Object, Object> fromMap , Map<Object, Object> tomMap, String... excludeKeys) {
		Map<Object, Object> diff = new HashMap<>();
		List<String> excludedKeyList = Arrays.asList(excludeKeys);
		for (Object key : fromMap.entrySet()) {
			if (excludeKeys != null && excludedKeyList.contains(key)|| fromMap.get(key)!= null && fromMap.get(key).equals(tomMap.get(key))) {
				continue;
				}
			
			Map<String, Object> diffVal = new HashMap<>();
			diffVal.put(PRE, fromMap.get(key));
			diffVal.put(POST, tomMap.get(key));
			diff.put(key, diffVal);
			tomMap.remove(key);			
		}	
		for (Object key : tomMap.entrySet()) {
			if (excludeKeys != null && excludedKeyList.contains(key)||tomMap.get(key)!= null && tomMap.get(key).equals(fromMap.get(key))) { 
				continue;
				}
			Map<String, Object> diffVal = new HashMap<>();
			diffVal.put(PRE, fromMap.get(key));
			diffVal.put(POST, tomMap.get(key));
			diff.put(key, diffVal);
		}		
		return diff;
	}
}
