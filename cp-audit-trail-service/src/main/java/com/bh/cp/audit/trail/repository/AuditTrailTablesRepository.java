package com.bh.cp.audit.trail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.audit.trail.entity.AuditTrailTables;
	



@Repository
public interface AuditTrailTablesRepository extends JpaRepository<AuditTrailTables, Integer> {

}

