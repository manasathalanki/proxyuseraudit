package com.bh.cp.proxy.helper;

import java.util.HashMap;
import java.util.Map;

import com.bh.cp.proxy.constants.WidgetConstants;

public class CecoWidgetHelper {
	private CecoWidgetHelper() {
	}

	public static String retriveUnits(String categoryName) {
		Map<String, String> unitsMap = new HashMap<>();
		unitsMap.put("Polytropic Head", "J/(Kg K)");
		unitsMap.put("Polytropic Efficiency", "%");
		unitsMap.put("Mass Flow", "kg/s");
		unitsMap.put("Inlet Volumetric Flow", "m3/s");
		unitsMap.put("Gas Power", "kW");
		unitsMap.put("Suction Pressure", "barG");
		unitsMap.put("Discharge Pressure", "barG");
		unitsMap.put("Discharge Temperature", "barG");
		unitsMap.put("Suction Temperature", "degC");
		unitsMap.put("Anti Surge Valve Position", "%");
		unitsMap.put("Speed", "rpm");
		unitsMap.put("Molecular Weight", "-");
		unitsMap.put("Pressure Ratio", "-");
		unitsMap.put("Side stream pressure", "barG");
		unitsMap.put("Side stream temperature", "degC");
		unitsMap.put("Anti Surge Valve Stage 1 Position", "%");
		unitsMap.put("Ambient Temperature", "degC");
		unitsMap.put("Torque meter power", "kW");
		return unitsMap.getOrDefault(categoryName, WidgetConstants.EMPTYSTRING);
	}

}
