package com.bh.cp.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.bh.cp.dashboard.entity.Customizations;
import com.bh.cp.dashboard.entity.CustomizationsAssets;

import jakarta.transaction.Transactional;

@Repository
public interface CustomizationsAssetsRepository extends JpaRepository<CustomizationsAssets, Integer> {

	public List<CustomizationsAssets> findByCustomizationsId(Integer id);

	public CustomizationsAssets findByCustomizationsIdAndVid(Integer id, String vid);

	public List<CustomizationsAssets> findByCustomizations(Customizations customizations);

	public List<CustomizationsAssets> findByCustomizationsAndVidIn(Customizations customization, List<String> vids);

	@Transactional
	@Modifying
	public void deleteByCustomizations(Customizations existingDefaultCustomization);

	public List<CustomizationsAssets> findAllByCustomizations(Customizations currentCustomization);

}
