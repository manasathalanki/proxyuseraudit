package com.bh.cp.user.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.user.entity.WidgetsFieldsApplicability;

@Repository
public interface WidgetsFieldsApplicabilityRepository extends JpaRepository<WidgetsFieldsApplicability, Integer> {

	public List<WidgetsFieldsApplicability> findByWidgetsId(Integer id);

}
