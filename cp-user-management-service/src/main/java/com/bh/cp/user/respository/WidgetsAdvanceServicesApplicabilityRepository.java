package com.bh.cp.user.respository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bh.cp.user.entity.WidgetsAdvanceServicesApplicability;

@Repository
public interface WidgetsAdvanceServicesApplicabilityRepository
		extends JpaRepository<WidgetsAdvanceServicesApplicability, Integer> {

	@Query(value = "select case when (select count(w2_0.id) from widgets_advance_services_applicability w2_0 "
			+ "where w2_0.widget_id=:widgetId)=0 then true when count(w1_0.id)>0 then true "
			+ "else false end as matched_in_sql from widgets_advance_services_applicability w1_0 "
			+ "join advance_services a1_0 on w1_0.advance_service_id = a1_0.id "
			+ "where w1_0.widget_id=:widgetId and a1_0.service_name in :enabledServices", nativeQuery = true)
	boolean matchEnabledServicesForWidget(@Param("widgetId") Integer widgetId,
			@Param("enabledServices") Set<String> enabledServices);

}
