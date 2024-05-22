package com.bh.cp.dashboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.dashboard.entity.UsersDefaultAssets;

@Repository
public interface UsersDefaultAssetsRepository extends JpaRepository<UsersDefaultAssets, Integer> {

	public Optional<UsersDefaultAssets> findByUsersSso(String sso);

}
