package com.bh.cp.dashboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.dashboard.entity.Customizations;
import com.bh.cp.dashboard.entity.Personas;

@Repository
public interface CustomizationsRepository extends JpaRepository<Customizations, Integer> {

	public Customizations findByUsersSsoAndAssetLevel(String sso, String level);

	public Customizations findByUsersSsoIsNullAndPersonasIdAndAssetLevel(Integer personaId, String level);

	public Customizations findByUsersSsoIsNullAndPersonasAndAssetLevel(Personas personas, String level);

	public Optional<Customizations> findByIdAndAssetLevel(Integer id, String level);

	public Optional<Customizations> findByIsDefaultAndUsersSso(boolean isDefault, String sso);

	public Optional<Customizations> findByPersonasIsNullAndIsDefaultAndUsersSsoAndAssetLevel(boolean isDefault,
			String sso, String level);

}
