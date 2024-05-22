package com.bh.cp.dashboard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.dashboard.entity.WidgetsLevel;

@Repository
public interface WidgetsLevelRepository extends JpaRepository<WidgetsLevel, Integer> {

	public Optional<WidgetsLevel> findByAssetLevelAndWidgetsId(String level, Integer widgetId);

	public Optional<WidgetsLevel> findByAssetLevelAndWidgetsIdAndWidgetsStatusesStatusIndicator(String level,
			Integer widgetId, Integer statusIndicator);

	public List<WidgetsLevel> findByAssetLevelAndWidgetsWidgetTypesDescriptionAndWidgetsStatusesStatusIndicator(
			String level, String widgetType, Integer indicator);

	public WidgetsLevel findByAssetLevelAndWidgetsIdAndWidgetsWidgetTypesDescription(String level, Integer widgetId,
			String description);

	public List<WidgetsLevel> findAllByAssetLevel(String level);

}
