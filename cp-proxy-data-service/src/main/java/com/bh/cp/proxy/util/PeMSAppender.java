package com.bh.cp.proxy.util;

import com.bh.cp.proxy.dto.request.PeMSRequestDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PeMSAppender {

	public Map<String, Object> appendRequestData(@Valid PeMSRequestDTO dataRequest,
			Map<String, Object> widgetsDataRequest) {

		if (dataRequest.getVidList() != null && !dataRequest.getVidList().isEmpty()) {
			widgetsDataRequest.put("vidList", dataRequest.getVidList());
		}
		if (dataRequest.getAssetIdList() != null && !dataRequest.getAssetIdList().isEmpty()) {
			widgetsDataRequest.put("assetIdList", dataRequest.getAssetIdList());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getFrom()))) {
			widgetsDataRequest.put("from", dataRequest.getFrom());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getTo()))) {
			widgetsDataRequest.put("to", dataRequest.getTo());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getSampling()))) {
			widgetsDataRequest.put("sampling", dataRequest.getSampling());
		}
		if (!(StringUtil.isEmptyCaseString(dataRequest.getMode()))) {
			widgetsDataRequest.put("mode", dataRequest.getMode());
		}
		return widgetsDataRequest;
	}
}
