package com.bh.cp.proxy.helper;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.asset.service.UMSClientService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.pojo.ServicesDirectory;
import com.bh.cp.proxy.util.DateUtility;
import com.bh.cp.proxy.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

@Component
@Qualifier("static")
public class ReplaceStaticValueHelper implements ReplaceValueHelper {

	private static final Logger logger = LoggerFactory.getLogger(ReplaceStaticValueHelper.class);

	private Integer fleetDataWidgetId;

	private AssetHierarchyFilterService assetHierarchyFilterService;

	private UMSClientService umsClientService;

	public ReplaceStaticValueHelper(@Autowired AssetHierarchyFilterService assetHierarchyFilterService,
			@Autowired UMSClientService umsClientService, @Value("${fleet.data.widget-id}") Integer fleetDataWidgetId) {
		super();
		this.assetHierarchyFilterService = assetHierarchyFilterService;
		this.umsClientService = umsClientService;
		this.fleetDataWidgetId = fleetDataWidgetId;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getReplaceValues(Map<String, Object> request,
			List<Map<String, Object>> userAssetHierarchyList, ServicesDirectory servicesDirectory)
			throws JsonProcessingException {
		Map<String, String> replaceValues = (Map<String, String>) request.getOrDefault(ProxyConstants.REPLACE_VALUES,
				new HashMap<String, String>());
		String vid = (String) request.get(ProxyConstants.VID);
		String dateRange = (String) request.getOrDefault(ProxyConstants.DATE_RANGE, ProxyConstants.DATE_RANGE_3M);
		String level = (String) request.get(ProxyConstants.LEVEL);

		Map<String, Object> assetsMap = (Map<String, Object>) request.get(ProxyConstants.ASSETSIDMAP);
		Map<String, Map<String, Set<String>>> fieldsMap = assetHierarchyFilterService
				.getFieldsAndEnabledServicesToMap(userAssetHierarchyList);

		logger.info("Adding Static replace values...");
		addDateRangeUtc(dateRange, replaceValues);
		addFromAndToDatesYYYYMMDDHHMISSUTC(dateRange, replaceValues);
		addFromAndToDatesYYYYMMDDUTC(dateRange, replaceValues);
		addSerialNosAsCSVUnderVid(vid, fieldsMap, replaceValues);
		addFromAndToDatesDDMMYYYYUTC(dateRange, replaceValues);
		addDateRangeUtcInMS(dateRange, replaceValues);
		addFromAndToDateYYYYMMDDThhmmssZ(dateRange, replaceValues);
		addApplicableMachines((HttpServletRequest) request.get(ProxyConstants.HTTPSERVLETREQUEST),
				userAssetHierarchyList, replaceValues, vid,
				(Integer) request.getOrDefault(ProxyConstants.WIDGET_ID, -1));
		addIdOfVid(assetsMap, replaceValues, level);
		addProjectId(assetsMap, replaceValues);
		addCarbonOptimizer(assetsMap, replaceValues);
		addGibSerialNos(vid, fieldsMap, replaceValues);
		return replaceValues;
	}

	private void addGibSerialNos(String vid, Map<String, Map<String, Set<String>>> fieldsMap,
			Map<String, String> replaceValues) {
		Map<String, Set<String>> vidFieldMap = fieldsMap.getOrDefault(vid, new HashMap<>());
		Set<String> gibSerialNos = vidFieldMap.getOrDefault(JSONUtilConstants.GIBSERIALNOS, new HashSet<>());
		String machineGIBSNsUnderVid = StringUtil.toCSV(new ArrayList<>(gibSerialNos), ",", "");
		replaceValues.put(ProxyConstants.KEY_GIB_SNS_CSV, machineGIBSNsUnderVid);
	}

	public void addCarbonOptimizer(Map<String, Object> assetsMap, Map<String, String> replaceValues) {
		JSONArray projectsAndPlantsArray = new JSONArray(assetHierarchyFilterService.getProjectsAndPlants(assetsMap));
		replaceValues.put(ProxyConstants.CARBON_OPTIMIZER, projectsAndPlantsArray.toString());
	}

	private void addApplicableMachines(HttpServletRequest httpServletRequest,
			List<Map<String, Object>> userAssetHierarchyList, Map<String, String> replaceValues, String vid,
			Integer widgetId) throws JsonProcessingException {

		if (widgetId.equals(fleetDataWidgetId)) {
			return;
		}

		List<String> applicableMachinesList = umsClientService.getApplicableMachinesForWidget(httpServletRequest, vid,
				widgetId, ProxyConstants.VID);
		replaceValues.put(ProxyConstants.KEY_APPLICABLE_MACHINE_VIDS_CSV,
				StringUtil.toCSV(applicableMachinesList, ",", ""));
		replaceValues.put(ProxyConstants.KEY_APPLICABLE_MACHINE_IDS_CSV, StringUtil
				.toCSV(assetHierarchyFilterService.getIdList(userAssetHierarchyList, applicableMachinesList), ",", ""));
		replaceValues.put(ProxyConstants.KEY_ASSET_VID, vid);
		addApplicableLineups(userAssetHierarchyList, replaceValues, applicableMachinesList);
	}

	@SuppressWarnings("unchecked")
	private void addApplicableLineups(List<Map<String, Object>> userAssetHierarchyList,
			Map<String, String> replaceValues, List<String> applicableMachinesList) {
		List<String> applicableLineupsList = new ArrayList<>();
		applicableMachinesList.stream()
				.forEach(machine -> applicableLineupsList.addAll(
						(List<String>) assetHierarchyFilterService.getAssetsMap(userAssetHierarchyList, machine, true)
								.getOrDefault(JSONUtilConstants.LEVEL_LINEUPS, new ArrayList<>())));
		replaceValues.put(ProxyConstants.KEY_APPLICABLE_LINEUP_VIDS_TEXT,
				StringUtil.toCSV(applicableLineupsList, ",", "\""));
		replaceValues.put(ProxyConstants.KEY_APPLICABLE_LINEUP_IDS_TEXT, StringUtil.toCSV(
				assetHierarchyFilterService.getIdList(userAssetHierarchyList, applicableLineupsList), ",", "\""));
	}

	private void addSerialNosAsCSVUnderVid(String vid, Map<String, Map<String, Set<String>>> fieldsMap,
			Map<String, String> replaceValues) {
		Map<String, Set<String>> vidFieldMap = fieldsMap.getOrDefault(vid, new HashMap<>());
		Set<String> serialNos = vidFieldMap.getOrDefault(JSONUtilConstants.SERIALNOS, new HashSet<>());
		replaceValues.put(ProxyConstants.KEY_MACHINE_SERIAL_NOS_CSV,
				StringUtil.toCSV(new ArrayList<>(serialNos), ",", ""));
		replaceValues.put(ProxyConstants.KEY_MACHINE_SERIAL_NOS_TEXT,
				StringUtil.toCSV(new ArrayList<>(serialNos), ",", "\""));
	}

	@SuppressWarnings("unchecked")
	private void addProjectId(Map<String, Object> assetsMap, Map<String, String> replaceValues) {
		String projectId = ((List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_PROJECTS, new ArrayList<>()))
				.get(0);
		replaceValues.put(ProxyConstants.KEY_PROJECT_ID_TEXT, projectId != null ? "\"" + projectId + "\"" : "");
	}

	@SuppressWarnings("unchecked")
	private void addIdOfVid(Map<String, Object> assetsMap, Map<String, String> replaceValues, String level) {

		String assetId = ((List<String>) assetsMap.getOrDefault(level != null ? level : ProxyConstants.LEVEL_PROJECTS,
				new ArrayList<>())).get(0);
		String plantId = assetsMap.get(JSONUtilConstants.CURRENTLEVEL).equals(ProxyConstants.LEVEL_PLANTS) ? assetId
				: null;
		String trainId = assetsMap.get(JSONUtilConstants.CURRENTLEVEL).equals(ProxyConstants.LEVEL_TRAINS) ? assetId
				: null;
		String lineupId = assetsMap.get(JSONUtilConstants.CURRENTLEVEL).equals(ProxyConstants.LEVEL_LINEUPS) ? assetId
				: null;
		String machineId = assetsMap.get(JSONUtilConstants.CURRENTLEVEL).equals(ProxyConstants.LEVEL_MACHINES) ? assetId
				: null;

		List<String> machineIds = (List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_MACHINES,
				new ArrayList<>());
		String machineIdsUnderVid = StringUtil.toCSV(machineIds, ",", "\"");

		level = level != null ? level : " ";
		replaceValues.put(ProxyConstants.KEY_ASSET_LEVEL, level);
		replaceValues.put(ProxyConstants.KEY_ASSET_LEVEL_SINGULAR,
				level.substring(0, level.length() - 1).toUpperCase());
		replaceValues.put(ProxyConstants.KEY_ASSET_ID, assetId != null ? assetId : "");
		replaceValues.put(ProxyConstants.KEY_ASSET_ID_TEXT, assetId != null ? "\"" + assetId + "\"" : "");
		replaceValues.put(ProxyConstants.KEY_PLANT_ID_TEXT, plantId != null ? "\"" + plantId + "\"" : "");
		replaceValues.put(ProxyConstants.KEY_TRAIN_ID_TEXT, trainId != null ? "\"" + trainId + "\"" : "");
		replaceValues.put(ProxyConstants.KEY_LINEUP_ID_TEXT, lineupId != null ? "\"" + lineupId + "\"" : "");
		replaceValues.put(ProxyConstants.KEY_LINEUPID_01, lineupId != null ? "\"" + lineupId + "\"" : "");
		replaceValues.put(ProxyConstants.KEY_MACHINE_ID_TEXT, machineId != null ? "\"" + machineId + "\"" : "");
		replaceValues.put(ProxyConstants.KEY_MACHINE_IDS_TEXT,
				machineIdsUnderVid.length() > 0 ? machineIdsUnderVid : "\"" + machineId + "\"");
		replaceValues.put(ProxyConstants.KEY_ASSET_LEVEL_TEXT,
				level.equals(" ") ? "\"" + level + "\"" : ProxyConstants.LEVEL_MACHINES);
		addLineupIdsCsv(assetsMap, replaceValues, lineupId, machineId);
	}

	@SuppressWarnings("unchecked")
	private void addLineupIdsCsv(Map<String, Object> assetsMap, Map<String, String> replaceValues, String lineupId,
			String machineId) {
		List<String> lineupIdsUnderVids = (List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_LINEUPS,
				new ArrayList<>());
		String lineupIdsText = null;
		String lineupIdsCsv = null;
		if (lineupIdsUnderVids == null || lineupIdsUnderVids.isEmpty()) {
			if (lineupId != null) {
				lineupIdsText = "\"" + lineupId + "\"";
				lineupIdsCsv = lineupId;
			} else if (machineId != null) {
				String parentLineupId = ((List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_LINEUPS,
						new ArrayList<>())).get(0);
				if (parentLineupId != null) {
					lineupIdsText = "\"" + parentLineupId + "\"";
					lineupIdsCsv = parentLineupId;
				}
			}
		} else {
			lineupIdsText = StringUtil.toCSV(lineupIdsUnderVids, ",", "\"");
			lineupIdsCsv = lineupIdsUnderVids.toString().replace("[", "").replace("]", "");
		}
		replaceValues.put(ProxyConstants.KEY_LINEUP_IDS_TEXT,
				lineupIdsText != null ? lineupIdsText : "\"" + lineupId + "\"");
		replaceValues.put(ProxyConstants.KEY_LINEUP_IDS_CSV, lineupIdsCsv != null ? lineupIdsCsv : lineupId);
	}

	private void addDateRangeUtc(String dateRange, Map<String, String> replaceValues) {
		replaceValues.put(ProxyConstants.KEY_DATE_RANGE, "\"" + dateRange + "\"");
		String[] datesRanges = DateUtility.getFromAndToDatesUTC(dateRange);
		if (datesRanges.length >= 2) {
			replaceValues.put(ProxyConstants.KEY_FROM_DT_UTC, "\"" + datesRanges[0] + "\"");
			replaceValues.put(ProxyConstants.KEY_TO_DT_UTC, "\"" + datesRanges[1] + "\"");

		}
	}

	private void addFromAndToDatesYYYYMMDDHHMISSUTC(String dateRange, Map<String, String> replaceValues) {
		String[] dateRangeYYYYMMDDHHMISS = DateUtility.getFromAndToDatesYYYYMMDDHHMISSUTC(dateRange);
		replaceValues.put(ProxyConstants.KEY_OPEN_DATE, "\"" + dateRangeYYYYMMDDHHMISS[0] + "\"");
		replaceValues.put(ProxyConstants.KEY_FROM_DT_YYYY_MM_DD_HHMMSS_UTC, dateRangeYYYYMMDDHHMISS[0]);
		replaceValues.put(ProxyConstants.KEY_TO_DT_YYYY_MM_DD_HHMMSS_UTC, dateRangeYYYYMMDDHHMISS[1]);

	}

	private void addFromAndToDatesYYYYMMDDUTC(String dateRange, Map<String, String> replaceValues) {
		String[] dateRangeYYYYMMDD = DateUtility.getFromAndToDatesYYYYMMDDUTC(dateRange);
		replaceValues.put(ProxyConstants.KEY_FROM_DT_YYYY_MM_DD, "\"" + dateRangeYYYYMMDD[0] + "\"");
		replaceValues.put(ProxyConstants.KEY_TO_DT_YYYY_MM_DD, "\"" + dateRangeYYYYMMDD[1] + "\"");
		replaceValues.put(ProxyConstants.KEY_FROM_DT_YYYY_MM_DD_NO_QUOTUES, dateRangeYYYYMMDD[0]);
		replaceValues.put(ProxyConstants.KEY_TO_DT_YYYY_MM_DD_NO_QUOTUES, dateRangeYYYYMMDD[1]);
	}

	private void addFromAndToDatesDDMMYYYYUTC(String dateRange, Map<String, String> replaceValues) {
		String[] dateRangeDDMMYYYYHHMMISS = DateUtility.getFromAndToDatesUTC(dateRange);
		replaceValues.put(ProxyConstants.KEY_FROM_DT_DD_MM_YYYY_HHMMSS_UTC, "\"" + dateRangeDDMMYYYYHHMMISS[0] + "\"");
		replaceValues.put(ProxyConstants.KEY_TO_DT_DD_MM_YYYY_HHMMSS_UTC, "\"" + dateRangeDDMMYYYYHHMMISS[1] + "\"");
	}

	private void addDateRangeUtcInMS(String dateRange, Map<String, String> replaceValues) {
		String[] dateRanges = DateUtility.getFromAndToDatesYYYYMMDDHHMISSUTC(dateRange);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		replaceValues.put(ProxyConstants.KEY_FROM_DT_UTC_IN_MS, String
				.valueOf(LocalDate.parse(dateRanges[0], dtf).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));
		replaceValues.put(ProxyConstants.KEY_TO_DT_UTC_IN_MS, String
				.valueOf(LocalDate.parse(dateRanges[1], dtf).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()));
	}

	private void addFromAndToDateYYYYMMDDThhmmssZ(String dateRange, Map<String, String> replaceValues) {
		String[] dateRangeYYYYMMDDTHHMMSSZ = DateUtility.getFromAndToDatesYYYYMMDDTHHMISSZUTC(dateRange);
		replaceValues.put(ProxyConstants.KEY_FROM_DT_DD_MM_YYYY_T_HHMMSS_Z_UTC, dateRangeYYYYMMDDTHHMMSSZ[0]);
		replaceValues.put(ProxyConstants.KEY_TO_DT_DD_MM_YYYY_T_HHMMSS_Z_UTC, dateRangeYYYYMMDDTHHMMSSZ[1]);

	}

}
