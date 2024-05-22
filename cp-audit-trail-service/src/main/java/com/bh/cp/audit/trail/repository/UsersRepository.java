package com.bh.cp.audit.trail.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.audit.trail.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

	public Users findBySso(String sso);

	public Optional<Users> findByEmail(String email);

}
