package com.bh.cp.proxy.repository;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.proxy.entity.ServicesDirectory;

@Repository
public interface ServicesDirectoryRepository extends JpaRepository<ServicesDirectory, Integer> {

	@Cacheable(value = "servicedirectory", key = "'cachedbywidgetId'.concat(#widgetId)")
	public Optional<ServicesDirectory> findByWidgetId(Integer widgetId);

	@Cacheable(value = "servicedirectory", key = "'cachedbyserviceId'.concat(#id)")
	public Optional<ServicesDirectory> findById(Integer id);

}
