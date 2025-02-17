package aii.dal;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aii.data.UserEntity;

public interface UsersCrud extends JpaRepository<UserEntity, String> {
	
	List<UserEntity> findByUniqueIdStartingWith(String emailPrefix);	
}