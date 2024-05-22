package com.bh.cp.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.dashboard.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

	public Users findBySso(String sso);

}
