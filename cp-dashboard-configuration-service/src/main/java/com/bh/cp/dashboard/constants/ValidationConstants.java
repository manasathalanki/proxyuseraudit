package com.bh.cp.dashboard.constants;

public class ValidationConstants {

	private ValidationConstants() {
		super();
	}

	public static final String LEVELS_REG_EXP = "^(projects|plants|trains|lineups|machines)$";
	public static final String KPI_SUMMARY_REG_EXP = "^(KPI|SUMMARY|kpi|summary)$";
	public static final String VALID_VID_REG_EXP = "[A-Za-z\\d\\_\\-]*";
	public static final String SETTING_VALUE_REG_EXP = "[A-Za-z\\d\s]*";
	public static final String ASSET_NAME_REG_EXP = "[A-Za-z\\d\\_\\-]*";

	public static final String LEVELS_NOT_VALID_MESSAGE = "Enter any one valid level";
	public static final String KPI_SUMMARY_NOT_VALID_MESSAGE = "Either value should kpi or summary";
	public static final String CUSTOMIZATION_ID_NOT_VALID_MESSAGE = "Enter valid customization id";
	public static final String ORDER_NUMBER_NOT_VALID_MESSAGE = "Enter valid customization id";
	public static final String VID_NOT_VALID_MESSAGE = "Enter valid vid";
	public static final String WIDGET_ID_NOT_VALID_MESSAGE = "Enter valid widgetId";
	public static final String SETTING_VALUE_NOT_VALID_MESSAGE = "Enter valid setting value";
	public static final String PERSONA_NOT_VALID_MESSAGE = "Enter valid persona id";
	public static final String ASSET_NAME_NOT_VALID_MESSAGE = "Enter valid asset name";

	public static final String NULL_VID_MESSAGE = "Vid should not be null";
	public static final String NULL_LEVEL_MESSAGE = "Level should not be null";
	public static final String NULL_WIDGET_ID_MESSAGE = "Widget id should not be null";
	public static final String NULL_WIDGET_TYPE_MESSAGE = "Widget type should not be null";
	public static final String NULL_ASSET_NAME_MESSAGE = "Asset name should not be null";
	public static final String NULL_SETTING_VALUE_MESSAGE = "Settings value should not be null";
	public static final String NULL_CUSTOMIZATION_ID_MESSAGE = "Customization id should not be null";
	public static final String NULL_PERSONA_ID_MESSAGE = "Persona id should not be null";
	public static final String NULL_ORDER_NUMBER_MESSAGE = "Order number should not be null";
	public static final String NULL_ASSET_ORDER_LIST_MESSAGE = "Asset order list should not be null";
	public static final String NULL_WIDGET_ORDER_LIST_MESSAGE = "Widget order list should not be null";
	public static final String NULL_ASSET_VID_LIST_MESSAGE = "Asset vid list should not be null";
	public static final String NULL_WIDGET_VID_LIST_MESSAGE = "Widget vid list should not be null";
	public static final String NULL_OR_FALSE_WIDGET_CHECKED_MESSAGE = "Widget checked should be true";
	public static final String EMPTY_ASSET_ORDER_MESSAGE = "Enter atleast one asset order details";
	public static final String EMPTY_ASSET_VID_MESSAGE = "Enter atleast one asset vid";
	public static final String EMPTY_WIDGET_ID_MESSAGE = "Enter atleast one widgetId";
	public static final String NULL_BOOLEAN_MESSAGE = "Enter either true or false";

}
