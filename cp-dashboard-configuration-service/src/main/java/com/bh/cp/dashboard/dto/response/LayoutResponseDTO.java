package com.bh.cp.dashboard.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LayoutResponseDTO {

	private HeaderResponseDTO header;

	private boolean myDashboard;

	private Integer customizationId;

	private String level;

	private String parentVid;

	private String selectedVid;

	private String selectedId;

	private String selectedTitle;

	private List<LayoutAssetResponseDTO> assets;

	private List<LayoutWidgetResponseDTO> kpis;

	private List<LayoutWidgetResponseDTO> summary;

	private String dateRange;

}
