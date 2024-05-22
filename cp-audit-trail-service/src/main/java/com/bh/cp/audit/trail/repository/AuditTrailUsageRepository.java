package com.bh.cp.audit.trail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.audit.trail.entity.AuditTrailUsage;

@Repository
public interface AuditTrailUsageRepository  extends JpaRepository<AuditTrailUsage, Integer>{

	AuditTrailUsage findByThreadName(String threadName);
	
}
