package com.bh.cp.proxy.pojo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaintenanceOptimizerResponse {

	private Integer taskId;
	private String lineupId;
	private Integer maintEventId;
	private String eventTypeDesc;
	private LocalDateTime taskdate;
	private String groupName;
	private Integer caseId;
	private Long eventCount;
	private Boolean isUrgent;
	private String status;

}
