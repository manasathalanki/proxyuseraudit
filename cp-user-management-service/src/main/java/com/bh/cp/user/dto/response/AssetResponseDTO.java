package com.bh.cp.user.dto.response;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bh.cp.user.constants.JSONUtilConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssetResponseDTO implements Serializable {

	private static final long serialVersionUID = 4287138232373811972L;

	private String searchVid;

	private boolean matchFound;

	private String previousLevel;

	private String currentLevel;

	private String nextLevel;

	@JsonInclude(Include.NON_NULL)
	private List<String> projects;

	@JsonInclude(Include.NON_NULL)
	private List<String> plants;

	@JsonInclude(Include.NON_NULL)
	private List<String> trains;

	@JsonInclude(Include.NON_NULL)
	private List<String> lineups;

	@JsonInclude(Include.NON_NULL)
	private List<String> machines;

	@SuppressWarnings("unchecked")
	public AssetResponseDTO(Map<String, Object> assetsMap) {
		super();
		this.searchVid = (String) assetsMap.getOrDefault(JSONUtilConstants.SEARCHVID, null);
		this.matchFound = (boolean) assetsMap.getOrDefault(JSONUtilConstants.MATCHFOUND, false);
		this.previousLevel = (String) assetsMap.getOrDefault(JSONUtilConstants.PREVIOUSLEVEL, null);
		this.currentLevel = (String) assetsMap.getOrDefault(JSONUtilConstants.CURRENTLEVEL, null);
		this.nextLevel = (String) assetsMap.getOrDefault(JSONUtilConstants.NEXTLEVEL, null);
		this.projects = (List<String>) assetsMap.get(JSONUtilConstants.LEVEL_PROJECTS);
		this.plants = (List<String>) assetsMap.get(JSONUtilConstants.LEVEL_PLANTS);
		this.trains = (List<String>) assetsMap.get(JSONUtilConstants.LEVEL_TRAINS);
		this.lineups = (List<String>) assetsMap.get(JSONUtilConstants.LEVEL_LINEUPS);
		this.machines = (List<String>) assetsMap.get(JSONUtilConstants.LEVEL_MACHINES);
	}

}
