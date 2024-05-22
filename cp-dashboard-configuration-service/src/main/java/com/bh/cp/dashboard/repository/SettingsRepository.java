package com.bh.cp.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.dashboard.entity.Settings;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Integer> {

}
