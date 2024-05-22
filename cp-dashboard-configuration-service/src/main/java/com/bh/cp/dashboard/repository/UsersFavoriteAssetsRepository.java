package com.bh.cp.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.dashboard.entity.Users;
import com.bh.cp.dashboard.entity.UsersFavoriteAssets;

@Repository
public interface UsersFavoriteAssetsRepository extends JpaRepository<UsersFavoriteAssets, Integer> {

	public UsersFavoriteAssets findByUsersAndVid(Users users, String vid);

	public List<UsersFavoriteAssets> findAllByUsers(Users users);
}
