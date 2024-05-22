package com.bh.cp.proxy.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.bh.cp.proxy.dto.request.CSASRequestDTO;

class CaseHistoryAppenderTest {

	@Mock
	DateUtility dateUtility;
	
	@InjectMocks
	private CaseHistoryAppender handler;

	@Mock
	CSASRequestDTO dataRequest;

	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for CommentList Edit")
	void appendData() throws Exception {
		dataRequest = new CSASRequestDTO();
		dataRequest.setDateRange("6M");
		String date[] = new String[2];
		date[0]="02-02-2022 00:00:00";
		date[1]="02-02-2024 00:00:00";
		Map<String, Object> widgetsDataRequest = new HashMap<>();
		dataRequest.setFromDate("2023-03-25 0:0:0");
		dataRequest.setToDate("2024-03-25 0:0:0");
		dataRequest.setLineupId("LN_L0574");
		dataRequest.setIssueId("123456");
		dataRequest.setTokenQuantity("20");
		handler = new CaseHistoryAppender();
		Map<String, Object> result = handler.appendRequestData(dataRequest, widgetsDataRequest);
		assertNotNull(result);
	}
}