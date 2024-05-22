package com.bh.cp.proxy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bh.cp.proxy.constants.JSONUtilConstants;

public class JSONUtil {

	private JSONUtil() {
		super();
	}

	public static List<Map<String, Object>> filterList(List<Map<String, Object>> original, List<String> vids) {
		List<Map<String, Object>> filtered = new ArrayList<>();
		if (isAnyNull(original, vids)) {
			return filtered;
		}
		Map<String, Object> filteredHierarchy;
		for (Map<String, Object> children : original) {
			if (children == null || children.get(JSONUtilConstants.DATA) == null) {
				return filtered;
			}
			filteredHierarchy = filterHierarchy(children, vids);
			if (!filteredHierarchy.isEmpty()) {
				filtered.add(filteredHierarchy);
			}
		}
		return filtered;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> filterHierarchy(Map<String, Object> children, List<String> vids) {
		Map<String, Object> filteredMap = new HashMap<>();
		List<Map<String, Object>> data = (List<Map<String, Object>>) children.get(JSONUtilConstants.DATA);
		if (data == null) {
			return filteredMap;
		}
		Map<String, Object> filteredChildren;
		for (Map<String, Object> entity : data) {
			if (entity.get(JSONUtilConstants.VID) != null && vids.contains(entity.get(JSONUtilConstants.VID))) {
				filteredMap.put(JSONUtilConstants.LEVEL, children.get(JSONUtilConstants.LEVEL));
				filteredMap.put(JSONUtilConstants.DATA, getFilterdData(filteredMap, entity));
			} else if (entity.get(JSONUtilConstants.CHILDREN) != null) {
				filteredChildren = filterHierarchy((Map<String, Object>) entity.get(JSONUtilConstants.CHILDREN), vids);
				if (!filteredChildren.isEmpty()) {
					filteredMap.put(JSONUtilConstants.LEVEL, children.get(JSONUtilConstants.LEVEL));
					filteredMap.put(JSONUtilConstants.DATA,
							getFilterdData(filteredMap, getParentMap(entity, filteredChildren)));
				}
			}
		}

		return filteredMap;
	}

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> getFilterdData(Map<String, Object> filteredMap,
			Map<String, Object> entity) {
		List<Map<String, Object>> filteredData = (List<Map<String, Object>>) filteredMap.get(JSONUtilConstants.DATA);
		if (filteredData == null) {
			filteredData = new ArrayList<>();
		}
		filteredData.add(entity);
		return filteredData;
	}

	private static Map<String, Object> getParentMap(Map<String, Object> entity, Map<String, Object> filteredChildren) {
		Map<String, Object> parentMap = new HashMap<>();
		parentMap.put(JSONUtilConstants.ID, entity.get(JSONUtilConstants.ID));
		parentMap.put(JSONUtilConstants.VID, entity.get(JSONUtilConstants.VID));
		parentMap.put(JSONUtilConstants.FIELDS, entity.get(JSONUtilConstants.FIELDS));
		parentMap.put(JSONUtilConstants.CHILDREN, filteredChildren);
		return parentMap;
	}

	public static void addDisplayName(List<Map<String, Object>> filtered, boolean compact) {
		if (filtered == null) {
			return;
		}
		for (Map<String, Object> children : filtered) {
			if (children == null || children.get(JSONUtilConstants.DATA) == null) {
				return;
			}
			addFieldsToEntities(children, JSONUtilConstants.LEVEL_PROJECTS, compact);
		}
	}

	@SuppressWarnings("unchecked")
	private static void addFieldsToEntities(Map<String, Object> children, String level, boolean compact) {
		List<Map<String, Object>> data = (List<Map<String, Object>>) children.get(JSONUtilConstants.DATA);
		if (data == null) {
			return;
		}
		Map<String, Object> fields;
		String id;
		String technologyCodeOg;
		String equipmentCode;
		Map<String, Object> nextLevelChildren;
		for (Map<String, Object> entity : data) {
			fields = (Map<String, Object>) entity.get(JSONUtilConstants.FIELDS);
			if (fields == null) {
				continue;
			}
			id = String.valueOf(entity.get(JSONUtilConstants.ID));
			fields.put(JSONUtilConstants.ID, id);
			entity.put(JSONUtilConstants.DISPLAYNAME, getDisplayName(level, fields, id));
			technologyCodeOg = (String) fields.get(JSONUtilConstants.TECHNOLOGYCODEOG);
			equipmentCode = (String) fields.get(JSONUtilConstants.EQUIPMENTCODE);
			entity.put(JSONUtilConstants.ICON,
					(technologyCodeOg == null ? level : technologyCodeOg) + JSONUtilConstants.EXTNSVG);
			entity.put(JSONUtilConstants.TOOLTIP, level + "{" + displayNames.get(level) + "}");

			String imageSrc = JSONUtilConstants.IMG_BASE_FOLDER.concat(level).concat("/").concat(id);
			entity.put(JSONUtilConstants.IMAGESRC,
					(technologyCodeOg != null && equipmentCode != null
							? JSONUtilConstants.IMG_BASE_FOLDER.concat(level).concat("/").concat(technologyCodeOg)
									.concat("_").concat(equipmentCode.replaceAll("[\\\\/]", "_"))
							: imageSrc).concat(JSONUtilConstants.IMAGE_EXTENSION));

			addFieldToEntity(fields, entity, JSONUtilConstants.TECHNOLOGYCODEOG);
			addFieldToEntity(fields, entity, JSONUtilConstants.EQUIPMENTCODE);
			addFieldToEntity(fields, entity, JSONUtilConstants.LOWNOX);
			addFieldToEntity(fields, entity, JSONUtilConstants.GIBSERIALNO);
			addFieldToEntity(fields, entity, JSONUtilConstants.SERIALNO);

			if (compact) {
				entity.remove(JSONUtilConstants.FIELDS);
			}
			nextLevelChildren = (Map<String, Object>) entity.get(JSONUtilConstants.CHILDREN);
			if (nextLevelChildren != null) {
				addFieldsToEntities(nextLevelChildren, (String) nextLevelChildren.get(JSONUtilConstants.LEVEL),
						compact);
			}

		}
	}

	private static void addFieldToEntity(Map<String, Object> fields, Map<String, Object> entityToAdd,
			String fieldName) {
		String field = (String) fields.get(fieldName);
		if (field != null) {
			entityToAdd.put(fieldName, field);
		}
	}

	private static String getDisplayName(String level, Map<String, Object> fields, String defaultValue) {
		String[] displayFields = displayNames.get(level).split("\\|");
		for (int i = 0; i < displayFields.length; i++) {
			String displayField = displayFields[i];
			Matcher matcher = Pattern.compile("[a-zA-Z]+").matcher(displayField);
			boolean hasFieldValue = false;
			while (matcher.find()) {
				String fieldValue = (String) fields.get(matcher.group());
				if (fieldValue != null && !"null".equals(fieldValue)) {
					displayField = displayField.replaceAll(matcher.group(), fieldValue);
					hasFieldValue = true;
				} else {
					displayField = displayField.replaceAll(matcher.group(), validateDefaultValue(defaultValue));
				}
			}
			if (hasFieldValue) {
				return displayField;
			}
		}
		return validateDefaultValue(defaultValue);
	}

	private static String validateDefaultValue(String defaultValue) {
		return !"null".equals(defaultValue) ? defaultValue : "[Not available]";
	}

	private static final Map<String, String> displayNames;
	static {
		displayNames = new HashMap<>();
		displayNames.put(JSONUtilConstants.LEVEL_PROJECTS, "customer");
		displayNames.put(JSONUtilConstants.LEVEL_PLANTS, "plantName");
		displayNames.put(JSONUtilConstants.LEVEL_TRAINS, "description");
		displayNames.put(JSONUtilConstants.LEVEL_LINEUPS, "lnupName (id)");
		displayNames.put(JSONUtilConstants.LEVEL_MACHINES, "oemSerialNo (id)");
	}

	@SuppressWarnings("unchecked")
	public static void addTechEquipCodeAndEnabledService(List<Map<String, Object>> assetHierarchy,
			List<Map<String, Object>> parentObject) {

		for (Map<String, Object> map : assetHierarchy) {
			List<Map<String, Object>> data = (List<Map<String, Object>>) map.getOrDefault(JSONUtilConstants.DATA,
					new ArrayList<>());
			for (Map<String, Object> map1 : data) {

				if (map1.containsKey(JSONUtilConstants.CHILDREN)) {
					Map<String, Object> children = (Map<String, Object>) map1.get(JSONUtilConstants.CHILDREN);
					addTechEquipCodeAndEnabledService(Arrays.asList(children), Arrays.asList(map1));
				}

				if (parentObject == null) {
					continue;
				}

				Map<String, Object> fields = (Map<String, Object>) map1.getOrDefault(JSONUtilConstants.FIELDS,
						new HashMap<>());
				String technologyCode = (String) fields.getOrDefault(JSONUtilConstants.TECHNOLOGYCODEOG, null);
				String equipmentCode = (String) fields.getOrDefault(JSONUtilConstants.EQUIPMENTCODE, null);
				String lowNox = (String) fields.getOrDefault(JSONUtilConstants.LOWNOX, null);
				String gibSerialNo = (String) fields.getOrDefault(JSONUtilConstants.GIBSERIALNO, null);
				String serialNo = (String) fields.getOrDefault(JSONUtilConstants.SERIALNO, null);

				addFieldsListToParent(parentObject, map1, JSONUtilConstants.TECHNOLOGYCODES, technologyCode);
				addFieldsListToParent(parentObject, map1, JSONUtilConstants.EQUIPMENTCODES, equipmentCode);
				addFieldsListToParent(parentObject, map1, JSONUtilConstants.LOWNOXS, lowNox);
				addFieldsListToParent(parentObject, map1, JSONUtilConstants.GIBSERIALNOS, gibSerialNo);
				addFieldsListToParent(parentObject, map1, JSONUtilConstants.SERIALNOS, serialNo);
				addEnabledServicesListToParent(parentObject, map1, JSONUtilConstants.ENABLEDSERVICES);
			}
		}

	}

	@SuppressWarnings("unchecked")
	private static void addFieldsListToParent(List<Map<String, Object>> parentObject, Map<String, Object> childObject,
			String listName, String code) {
		Collection<String> childCodeList = (Collection<String>) childObject.get(listName);
		Map<String, Object> parentMap = parentObject.get(0);
		if (childCodeList != null) {
			if (!parentMap.containsKey(listName)) {
				parentMap.put(listName, new HashSet<>(childCodeList));
			} else {
				Collection<String> parentCodeList = (Collection<String>) parentMap.get(listName);
				parentCodeList.addAll(childCodeList);
			}
		} else if (code != null) {
			Collection<String> codeList = (Collection<String>) parentMap.getOrDefault(listName, new HashSet<>());
			codeList.add(code);
			parentMap.put(listName, codeList);
		}
	}

	@SuppressWarnings("unchecked")
	private static void addEnabledServicesListToParent(List<Map<String, Object>> parentObject,
			Map<String, Object> childObject, String listName) {
		Collection<String> childEsList = (Collection<String>) childObject.get(listName);
		Map<String, Object> parentMap = parentObject.get(0);
		if (childEsList != null) {
			if (!parentMap.containsKey(listName)) {
				parentMap.put(listName, new HashSet<>(childEsList));
			} else {
				Collection<String> parentEsList = (Collection<String>) parentMap.get(listName);
				parentEsList.addAll(childEsList);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void removeNullParents(List<Map<String, Object>> assetHierarchy, Map<String, Object> parentObject) {

		if (isAnyNull(assetHierarchy) || isAnyNull(assetHierarchy.get(0))) {
			return;
		}
		for (Map<String, Object> map : assetHierarchy) {
			List<Map<String, Object>> data = (List<Map<String, Object>>) map.getOrDefault(JSONUtilConstants.DATA,
					new ArrayList<>());
			for (Map<String, Object> map1 : data) {
				String vid = (String) map1.get(JSONUtilConstants.VID);
				if (vid == null && map1.containsKey(JSONUtilConstants.CHILDREN)) {
					parentObject.put(JSONUtilConstants.CHILDREN, map1.get(JSONUtilConstants.CHILDREN));
					removeNullParents(Arrays.asList((Map<String, Object>) map1.get(JSONUtilConstants.CHILDREN)),
							parentObject);
				} else if (map1.containsKey(JSONUtilConstants.CHILDREN)) {
					removeNullParents(Arrays.asList((Map<String, Object>) map1.get(JSONUtilConstants.CHILDREN)), map1);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void addExtFieldToOutputMap(List<Map<String, Object>> assetHierarchy, Map<String, String> outputMap,
			String extFieldName) {
		if (isAnyNull(assetHierarchy, outputMap) || assetHierarchy.isEmpty() || isAnyNull(assetHierarchy.get(0))) {
			return;
		}
		List<Map<String, Object>> data;
		Map<String, Object> children;
		for (Map<String, Object> map : assetHierarchy) {
			data = (List<Map<String, Object>>) map.getOrDefault(JSONUtilConstants.DATA, new ArrayList<>());
			for (Map<String, Object> map1 : data) {
				outputMap.put((String) map1.get(JSONUtilConstants.VID),
						(String) map1.getOrDefault(extFieldName, map1.get(JSONUtilConstants.ID)));
				children = (Map<String, Object>) map1.get(JSONUtilConstants.CHILDREN);
				addExtFieldToOutputMap(Arrays.asList(children), outputMap, extFieldName);
			}
		}

	}

	@SuppressWarnings("unchecked")
	public static void addFieldsAndEnabledServicesToMap(List<Map<String, Object>> assetHierarchy,
			Map<String, Map<String, Set<String>>> outputMap) {

		if (isAnyNull(assetHierarchy, outputMap) || assetHierarchy.isEmpty() || assetHierarchy.get(0) == null) {
			return;
		}

		List<Map<String, Object>> data = null;
		Set<String> enabledServices = null;
		Map<String, Object> children = null;
		for (Map<String, Object> map : assetHierarchy) {
			data = (List<Map<String, Object>>) map.getOrDefault(JSONUtilConstants.DATA, new ArrayList<>());
			for (Map<String, Object> map1 : data) {
				String vid = (String) map1.get(JSONUtilConstants.VID);
				String technologyCode = (String) map1.getOrDefault(JSONUtilConstants.TECHNOLOGYCODEOG, null);
				String equipmentCode = (String) map1.getOrDefault(JSONUtilConstants.EQUIPMENTCODE, null);
				String lowNox = (String) map1.getOrDefault(JSONUtilConstants.LOWNOX, null);
				String gibSerialNo = (String) map1.getOrDefault(JSONUtilConstants.GIBSERIALNO, null);
				String serialNo = (String) map1.getOrDefault(JSONUtilConstants.SERIALNO, null);
				Collection<String> technologyCodes = (Collection<String>) map1
						.getOrDefault(JSONUtilConstants.TECHNOLOGYCODES, null);
				Collection<String> equipmentCodes = (Collection<String>) map1
						.getOrDefault(JSONUtilConstants.EQUIPMENTCODES, null);
				Collection<String> lowNoxs = (Collection<String>) map1.getOrDefault(JSONUtilConstants.LOWNOXS, null);
				Collection<String> gibSerialNos = (Collection<String>) map1.getOrDefault(JSONUtilConstants.GIBSERIALNOS,
						null);
				Collection<String> serialNos = (Collection<String>) map1.getOrDefault(JSONUtilConstants.SERIALNOS,
						null);
				Map<String, Set<String>> innerMap = new HashMap<>();

				if (map1.get(JSONUtilConstants.ENABLEDSERVICES) instanceof List<?> list) {
					enabledServices = new HashSet<>((Collection<String>) list);
				} else {
					enabledServices = (Set<String>) map1.get(JSONUtilConstants.ENABLEDSERVICES);
				}

				if (enabledServices != null) {
					innerMap.put(JSONUtilConstants.ENABLEDSERVICES, enabledServices);
				}
				addFieldsToOutputMap(innerMap, JSONUtilConstants.TECHNOLOGYCODES, technologyCode, technologyCodes);
				addFieldsToOutputMap(innerMap, JSONUtilConstants.EQUIPMENTCODES, equipmentCode, equipmentCodes);
				addFieldsToOutputMap(innerMap, JSONUtilConstants.LOWNOXS, lowNox, lowNoxs);
				addFieldsToOutputMap(innerMap, JSONUtilConstants.GIBSERIALNOS, gibSerialNo, gibSerialNos);
				addFieldsToOutputMap(innerMap, JSONUtilConstants.SERIALNOS, serialNo, serialNos);
				outputMap.put(vid, innerMap);

				children = (Map<String, Object>) map1.get(JSONUtilConstants.CHILDREN);
				addFieldsAndEnabledServicesToMap(Arrays.asList(children), outputMap);
			}
		}
	}

	private static void addFieldsToOutputMap(Map<String, Set<String>> innerMap, String codeType, String code,
			Collection<String> codesList) {
		if (code != null) {
			innerMap.put(codeType, Set.of(code));
		} else if (codesList != null) {
			innerMap.put(codeType, new HashSet<>(codesList));
		}
	}

	@SuppressWarnings("unchecked")
	public static void addAssetsToMap(List<Map<String, Object>> assetHierarchy, String vidToFind, boolean collectAsVid,
			Map<String, Object> outputMap) {

		if (isAnyNull(assetHierarchy, outputMap, assetHierarchy.get(0))) {
			return;
		}

		List<Map<String, Object>> data = null;
		Map<String, Object> children = null;
		String level = null;
		String vid = null;
		for (Map<String, Object> map : assetHierarchy) {
			level = (String) map.get(JSONUtilConstants.LEVEL);
			data = (List<Map<String, Object>>) map.getOrDefault(JSONUtilConstants.DATA, new ArrayList<>());
			for (Map<String, Object> map1 : data) {
				vid = (String) Optional.ofNullable(map1.get(JSONUtilConstants.VID)).orElse("");
				children = (Map<String, Object>) map1.get(JSONUtilConstants.CHILDREN);

				if (vidToFind == null || (boolean) outputMap.getOrDefault(JSONUtilConstants.MATCHFOUND, false)) {
					outputMap.putIfAbsent(JSONUtilConstants.NEXTLEVEL, level);
					addAssetToOutputMap(outputMap, level, map1, collectAsVid);
					addAssetsToMap(Arrays.asList(children), vidToFind, collectAsVid, outputMap);
					continue;
				} else if (!vid.equals(vidToFind)) {
					addAssetsToMap(Arrays.asList(children), vidToFind, collectAsVid, outputMap);
				} else {
					outputMap.put(JSONUtilConstants.SEARCHVID, vidToFind);
					outputMap.put(JSONUtilConstants.MATCHFOUND, true);
					outputMap.putIfAbsent(JSONUtilConstants.CURRENTLEVEL, level);
					Optional.ofNullable(children).ifPresent(child -> outputMap.putIfAbsent(JSONUtilConstants.NEXTLEVEL,
							child.get(JSONUtilConstants.LEVEL)));
					addAssetToOutputMap(outputMap, level, map1, collectAsVid);
					addAssetsToMap(Arrays.asList(children), vidToFind, collectAsVid, outputMap);
					return;
				}

				if (!outputMap.isEmpty()) {
					outputMap.putIfAbsent(JSONUtilConstants.PREVIOUSLEVEL, level);
					addAssetToOutputMap(outputMap, level, map1, collectAsVid);
					return;
				}

			}

		}

	}

	@SuppressWarnings("unchecked")
	private static void addAssetToOutputMap(Map<String, Object> outputMap, String level, Map<String, Object> assetMap,
			boolean collectAsVid) {
		String requiredId = collectAsVid ? JSONUtilConstants.VID : JSONUtilConstants.ID;
		String assetId = (String) assetMap.get(requiredId);
		List<String> fieldList = (List<String>) outputMap.getOrDefault(level, new ArrayList<>());
		if (!fieldList.contains(assetId)) {
			fieldList.add(assetId);
			outputMap.put(level, fieldList);
		}
	}

	@SuppressWarnings("unchecked")
	public static void addMultipleFieldsOfLevelUnderVid(List<Map<String, Object>> assetHierarchy, String vidToFetchFrom,
			String levelToFetchFrom, List<String> fieldNames, List<String> outerFieldNames,
			List<Map<String, Object>> outputList, boolean matched) {
		if (isAnyNull(assetHierarchy, outputList) || isAnyNull(assetHierarchy.get(0))) {
			return;
		}

		List<Map<String, Object>> data = null;
		Map<String, Object> children = null;
		String level = null;
		String vid = null;
		for (Map<String, Object> map : assetHierarchy) {
			level = (String) map.get(JSONUtilConstants.LEVEL);
			data = (List<Map<String, Object>>) map.getOrDefault(JSONUtilConstants.DATA, new ArrayList<>());
			for (Map<String, Object> map1 : data) {
				vid = (String) Optional.ofNullable(map1.get(JSONUtilConstants.VID)).orElse("");
				children = (Map<String, Object>) map1.get(JSONUtilConstants.CHILDREN);

				if (matched || vidToFetchFrom == null) {
					if (!level.equals(levelToFetchFrom)) {
						addMultipleFieldsOfLevelUnderVid(Arrays.asList(children), vidToFetchFrom, levelToFetchFrom,
								fieldNames, outerFieldNames, outputList, true);
						continue;
					}

					Map<String, Object> current = new HashMap<>();
					fieldNames.forEach(fieldName -> current.put(fieldName,
							((Map<String, Object>) map1.getOrDefault(JSONUtilConstants.FIELDS, new HashMap<>()))
									.getOrDefault(fieldName, null)));
					outerFieldNames.forEach(
							outerFieldName -> current.put(outerFieldName, map1.getOrDefault(outerFieldName, null)));
					outputList.add(current);

				} else if (!vid.equals(vidToFetchFrom)) {
					addMultipleFieldsOfLevelUnderVid(Arrays.asList(children), vidToFetchFrom, levelToFetchFrom,
							fieldNames, outerFieldNames, outputList, false);
				} else {
					addMultipleFieldsOfLevelUnderVid(Arrays.asList(children), vidToFetchFrom, levelToFetchFrom,
							fieldNames, outerFieldNames, outputList, true);
					return;
				}

			}

		}

	}

	@SuppressWarnings("unchecked")
	public static void addFieldsOfVidToMap(List<Map<String, Object>> assetHierarchy, String vidToFind,
			List<String> fieldNames, Map<String, Object> outputMap, boolean takeFromParent,
			List<Map<String, Object>> parentObject) {

		if (isAnyNull(assetHierarchy, vidToFind, assetHierarchy.get(0))) {
			return;
		}

		List<Map<String, Object>> data = null;
		Map<String, Object> children = null;
		String vid = null;
		for (Map<String, Object> map : assetHierarchy) {
			data = (List<Map<String, Object>>) map.get(JSONUtilConstants.DATA);
			for (Map<String, Object> map1 : data) {
				vid = (String) Optional.ofNullable(map1.get(JSONUtilConstants.VID)).orElse("");
				children = (Map<String, Object>) map1.get(JSONUtilConstants.CHILDREN);

				if (Boolean.TRUE.equals(outputMap.getOrDefault(JSONUtilConstants.MATCHFOUND, false))) {
					return;
				} else if (!vid.equals(vidToFind)) {
					addFieldsOfVidToMap(Arrays.asList(children), vidToFind, fieldNames, outputMap, takeFromParent,
							Arrays.asList(map1));
				} else {
					outputMap.put(JSONUtilConstants.MATCHFOUND, true);
					if (takeFromParent && parentObject != null) {
						addFieldsToOutputMap(fieldNames, outputMap, parentObject.get(0));
						return;
					}

					addFieldsToOutputMap(fieldNames, outputMap, map1);
					return;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void addFieldsToOutputMap(List<String> fieldNames, Map<String, Object> outputMap,
			Map<String, Object> map) {
		fieldNames.forEach(fieldName -> outputMap.put(fieldName,
				map.getOrDefault(fieldName, null) != null ? map.get(fieldName)
						: ((Map<String, Object>) map.getOrDefault(JSONUtilConstants.FIELDS, new HashMap<>()))
								.getOrDefault(fieldName, null)));
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getSubTree(List<Map<String, Object>> assetHierarchy, String vid) {
		if (isAnyNull(assetHierarchy, vid)) {
			return Collections.emptyList();
		}
		List<Map<String, Object>> dataList;
		List<Map<String, Object>> subTree;
		for (Map<String, Object> data : assetHierarchy) {
			if (data == null || data.get(JSONUtilConstants.DATA) == null) {
				return Collections.emptyList();
			}
			for (Map<String, Object> entity : (List<Map<String, Object>>) data.get(JSONUtilConstants.DATA)) {
				dataList = new ArrayList<>();
				dataList.add((Map<String, Object>) entity.get(JSONUtilConstants.CHILDREN));
				if (vid.equals(entity.get(JSONUtilConstants.VID))) {
					return dataList;
				} else if (entity.get(JSONUtilConstants.CHILDREN) != null) {
					subTree = getSubTree(dataList, vid);
					if (!subTree.isEmpty()) {
						return subTree;
					}
				}
			}
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public static void getIdsForVid(List<Map<String, Object>> assetHierarchy, List<String> vids,
			List<String> outputList) {

		if (isAnyNull(assetHierarchy, outputList, assetHierarchy.get(0))) {
			return;
		}

		List<Map<String, Object>> data = null;
		Map<String, Object> children = null;
		String id = null;
		String vid = null;
		for (Map<String, Object> map : assetHierarchy) {
			data = (List<Map<String, Object>>) map.getOrDefault(JSONUtilConstants.DATA, new ArrayList<>());
			for (Map<String, Object> map1 : data) {
				id = (String) map1.get(JSONUtilConstants.ID);
				vid = (String) map1.get(JSONUtilConstants.VID);
				children = (Map<String, Object>) map1.get(JSONUtilConstants.CHILDREN);
				getIdsForVid(Arrays.asList(children), vids, outputList);
				if (vid != null && vids.contains(vid)) {
					outputList.add(id);
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	public static void validateVid(List<Map<String, Object>> assetHierarchy, String vidToFind,
			Map<String, Boolean> outputMap) {
		if (isAnyNull(assetHierarchy, vidToFind, assetHierarchy.get(0))) {
			return;
		}

		List<Map<String, Object>> data = null;
		Map<String, Object> children = null;
		String vid = null;
		for (Map<String, Object> map : assetHierarchy) {
			data = (List<Map<String, Object>>) map.get(JSONUtilConstants.DATA);
			for (Map<String, Object> map1 : data) {
				vid = (String) Optional.ofNullable(map1.get(JSONUtilConstants.VID)).orElse("");
				children = (Map<String, Object>) map1.get(JSONUtilConstants.CHILDREN);
				if (Boolean.TRUE.equals(outputMap.getOrDefault(JSONUtilConstants.MATCHFOUND, false))) {
					break;
				} else if (!vid.equals(vidToFind)) {
					validateVid(Arrays.asList(children), vidToFind, outputMap);
				} else {
					outputMap.put(JSONUtilConstants.MATCHFOUND, true);
				}
			}
		}
	}

	public static boolean isAnyNull(Object... objects) {
		for (Object object : objects) {
			if (object == null) {
				return true;
			}
		}
		return false;
	}
}
