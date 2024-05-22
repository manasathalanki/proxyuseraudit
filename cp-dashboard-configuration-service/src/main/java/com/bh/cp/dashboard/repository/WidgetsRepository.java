package com.bh.cp.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.dashboard.entity.Widgets;

@Repository
public interface WidgetsRepository extends JpaRepository<Widgets, Integer> {

	public Widgets findByIdAndWidgetTypesDescription(Integer widgetId, String type);

}
