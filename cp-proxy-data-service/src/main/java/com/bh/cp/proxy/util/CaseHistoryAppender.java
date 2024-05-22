package com.bh.cp.proxy.util;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.bh.cp.proxy.dto.request.CSASRequestDTO;

import jakarta.validation.Valid;

@Component
public class CaseHistoryAppender {

	public Map<String, Object> appendRequestData(@Valid CSASRequestDTO dataRequest,
			Map<String, Object> widgetsDataRequest) {

		if (!StringUtil.isEmptyCaseString(dataRequest.getDateRange())) {

			String[] datesRanges = DateUtility.getFromAndToDatesUTC(dataRequest.getDateRange());

			widgetsDataRequest.put("startDate", "1960-01-01 09:00:00");
			widgetsDataRequest.put("endDate", datesRanges[1]);
		} else {

			widgetsDataRequest.remove("dateRange");
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getFromDate()))) {
			widgetsDataRequest.put("fromDate", DateUtility.convertStringDateToUTCDateFormat(dataRequest.getFromDate()));
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getToDate()))) {
			widgetsDataRequest.put("toDate", DateUtility.convertStringDateToUTCDateFormat(dataRequest.getToDate()));
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getLineupId()))) {
			widgetsDataRequest.put("lineupId", dataRequest.getLineupId());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getIssueId()))) {
			widgetsDataRequest.put("issueId", Integer.parseInt(dataRequest.getIssueId()));
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getTokenQuantity()))) {
			Double d = Double.parseDouble(dataRequest.getTokenQuantity());
			Integer tokenQuantityValue = d.intValue();
			widgetsDataRequest.put("tokenQuantity", tokenQuantityValue);

		}
		return widgetsDataRequest;
	}
}
