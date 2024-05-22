package com.bh.cp.user.respository;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.user.entity.Widgets;

@Repository
public interface WidgetsRepository extends JpaRepository<Widgets, Integer> {

	@Cacheable(value = "widget", key = "#id")
	public Optional<Widgets> findById(Integer id);

}
