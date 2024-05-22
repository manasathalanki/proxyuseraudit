package com.bh.cp.audit.trail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.audit.trail.entity.AuditTrailPerformance;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface AuditTrailPerformanceRepository extends JpaRepository<AuditTrailPerformance, Integer> {

	List<AuditTrailPerformance> findByUserSso(String sso);

}
