package aii.logic;

import java.util.List;

import aii.boundary.NewUserBoundary;
import aii.boundary.UserBoundary;
import aii.data.UserRole;

public interface UsersLogic {

    // Create a new user
	public UserBoundary createUser(NewUserBoundary newUserBoundary);

    // Login an existing user
	public UserBoundary loginUser(String systemID, String email);

    // Update user details
	public UserBoundary updateUser(String systemID, String email, UserBoundary updatedUser);

    // Retrieve all users
	public List<UserBoundary> getAllUsers(String adminSystemID, String adminEmail, int page, int size);

    // Delete all users
	public void deleteAllUsers(String adminSystemID, String adminEmail);
	
    // New method to retrieve user role (check if the user exist in the DB)
	public UserRole getUserRole(String systemID, String email);

}
