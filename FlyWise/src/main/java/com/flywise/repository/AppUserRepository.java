package com.flywise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flywise.pojos.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
	
//	AppUser findByUsernameAndPassword(String username, String password);
	
	AppUser findByEmail(String email);
}
