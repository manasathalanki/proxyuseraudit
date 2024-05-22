package com.bh.cp.proxy.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.asset.service.AssetHierarchyFilterService;
import com.bh.cp.proxy.constants.JSONUtilConstants;
import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.pojo.ServicesDirectory;
import com.bh.cp.proxy.pojo.ServicesDynamicParameters;
import com.bh.cp.proxy.util.StringUtil;

@Component
@Qualifier("dynamic")
public class ReplaceDynamicValueHelper implements ReplaceValueHelper {

	private AssetHierarchyFilterService assetHierarchyFilterService;

	@Autowired
	public ReplaceDynamicValueHelper(AssetHierarchyFilterService assetHierarchyFilterService) {
		this.assetHierarchyFilterService = assetHierarchyFilterService;
	}

	private static final Logger logger = LoggerFactory.getLogger(ReplaceDynamicValueHelper.class);

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getReplaceValues(Map<String, Object> request,
			List<Map<String, Object>> userAssetHierarchyList, ServicesDirectory servicesDirectory) {

		// for User Assigned projects if search contains startDate and endDate
		if (StringUtil.isDateSearch(request)) {

			Map<String, Object> assetsMap = assetHierarchyFilterService.getAssetsMap(userAssetHierarchyList, null,
					false);
			List<String> projectsList = (List<String>) assetsMap.getOrDefault(JSONUtilConstants.LEVEL_PROJECTS,
					new ArrayList<>());
			request.put("projectId", projectsList);
		}

		logger.info("Adding Dynamic replace values...");
		Map<String, String> replaceValues = (Map<String, String>) request.getOrDefault(ProxyConstants.REPLACE_VALUES,
				new HashMap<String, String>());
		StringBuilder parameters = new StringBuilder();
		if (servicesDirectory.getDynamicParameters() != null) {
			String comma = "";
			String and = "";
			String queryParam = "?";
			String pathParam = "/";
			for (ServicesDynamicParameters dynamicParameters : servicesDirectory.getDynamicParameters()) {
				if (request.containsKey(dynamicParameters.getField())) {

					String input = dynamicParameters.getInputData();
					String data = request.get(dynamicParameters.getField()).toString();
					input = validateInputAndReturn(input, data);

					if (servicesDirectory.getMethod().equals(ProxyConstants.POST_METHOD)) {
						parameters.append(comma).append(input);
						comma = ",";
					} else {
						input = input.replace("\"", "");
						appendInputParams(queryParam, pathParam, input, parameters, and, request);
						and = "&";
						queryParam = "";
					}

				}
			}
		}
		replaceValues.put(ProxyConstants.KEY_DYNAMIC_PRAMETERS, parameters.toString());
		return replaceValues;
	}

	  void appendInputParams(String queryParam, String pathParam, String input, StringBuilder parameters,
			String and, Map<String, Object> request) {
		if (request.get(ProxyConstants.INPUT_PARAM) != null
				&& request.get(ProxyConstants.INPUT_PARAM).equals(ProxyConstants.PATH_PARAM)) {
			input = StringUtil.encodeString(input);
			parameters.append(pathParam).append(and).append(input);
		} else {
			parameters.append(queryParam).append(and).append(input);

		}

	}

	String validateInputAndReturn(String input, String data) {
		if (data.contains("[")) {
			JSONArray array = new JSONArray(data);
			data = array.toString();
			data = data.replace("[", "");
			data = data.replace("]", "");
			return input.replaceAll(ProxyConstants.KEY_CASE_REPLACE_VALUE, data);
		} else {
			return input.replaceAll(ProxyConstants.KEY_CASE_REPLACE_VALUE, "\"" + data + "\"");
		}
	}

}
