package com.bh.cp.user.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bh.cp.user.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

	public Optional<Users> findBySso(String sso);

}
