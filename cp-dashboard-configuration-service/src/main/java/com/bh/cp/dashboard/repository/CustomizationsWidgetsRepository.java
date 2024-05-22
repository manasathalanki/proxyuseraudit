package com.bh.cp.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.bh.cp.dashboard.entity.Customizations;
import com.bh.cp.dashboard.entity.CustomizationsWidgets;

import jakarta.transaction.Transactional;

@Repository
public interface CustomizationsWidgetsRepository extends JpaRepository<CustomizationsWidgets, Integer> {

	public List<CustomizationsWidgets> findAllByCustomizations(Customizations customization);

	public List<CustomizationsWidgets> findAllByCustomizationsAndAndWidgetsWidgetTypesDescription(
			Customizations customization, String widgetType);

	public List<CustomizationsWidgets> findByCustomizationsId(Integer id);

	public List<CustomizationsWidgets> findByCustomizationsIdAndWidgetsWidgetTypesDescription(Integer id, String type);

	@Transactional
	@Modifying
	public void deleteByCustomizationsIdAndWidgetsWidgetTypesDescriptionAndWidgetsIdIn(Integer customizationId,
			String type, List<Integer> widgetIds);

	public List<CustomizationsWidgets> findByCustomizationsIdAndWidgetsWidgetTypesDescriptionAndWidgetsStatusesStatusIndicator(
			Integer id, String upperCase, Integer indicator);

	@Transactional
	@Modifying
	public void deleteByCustomizations(Customizations existingDefaultCustomization);

}
