package aii.logic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aii.boundary.NewUserBoundary;
import aii.boundary.UserBoundary;
import aii.boundary.UserBoundary.UserId;
import aii.converter.UserConverter;
import aii.dal.UsersCrud;
import aii.data.UserEntity;
import aii.data.UserRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsersLogicImplementation implements UsersLogic {

    private final UsersCrud usersCrud; // Repository for database access
    private final UserConverter userConverter;
    private final Log logger = LogFactory.getLog(UsersLogicImplementation.class); // Logger instance


    @Value("${spring.application.name}")
    private String systemID; // System ID value from application.properties

    // Constructor for dependency injection of UsersCrud repository
    public UsersLogicImplementation(UsersCrud usersCrud, UserConverter userConverter) {
        this.usersCrud = usersCrud;
        this.userConverter = userConverter;
    }

    @Override
    @Transactional(readOnly = false) // Write transaction
    public UserBoundary createUser(NewUserBoundary newUserBoundary) {
        validateNewUserBoundary(newUserBoundary); // Perform input validation

        // Check if user with this email already exists
        String emailPrefix = newUserBoundary.getEmail() + "@@";
        List<UserEntity> existingUsers = usersCrud.findByUniqueIdStartingWith(emailPrefix);
        if (!existingUsers.isEmpty()) {
            throw new InvalidInputException("User with email " + newUserBoundary.getEmail() + " already exists");
        }
        
        // Generate uniqueId
        String uniqueId = generateUniqueId(newUserBoundary.getEmail(), systemID);

        // Convert NewUserBoundary to UserEntity using uniqueId
        UserEntity entity = new UserEntity(
            uniqueId,
            newUserBoundary.getUsername(),
            newUserBoundary.getAvatar(),
            newUserBoundary.getRole()
        );
        

        // Save the new user entity in the database
        usersCrud.save(entity);
        UserBoundary createdUser = userConverter.toBoundary(entity);
        logger.info("User created: " + createdUser); // Log user creation

        return createdUser;
    }

    @Override
    @Transactional(readOnly = true) // Read-only transaction
    public UserBoundary loginUser(String systemID, String email) {
        // Generate uniqueId for lookup
        String uniqueId = generateUniqueId(email, systemID);

        // Find user in database by uniqueId
        UserEntity entity = usersCrud.findById(uniqueId).orElseThrow(() ->
                new NotFoundException("User with email " + email + " not found"));

        UserBoundary user = userConverter.toBoundary(entity);
        logger.info("User login: " + user); // Log user login

        // Convert the entity to UserBoundary and return
        return user;
    }

    @Override
    @Transactional(readOnly = false) // Write transaction
    public UserBoundary updateUser(String systemID, String email, UserBoundary updatedUser) {
        // Generate uniqueId for lookup
        String uniqueId = generateUniqueId(email, systemID);

        // Find user in database by uniqueId
        UserEntity entity = usersCrud.findById(uniqueId).orElseThrow(() ->
                new NotFoundException("User not found or invalid systemID"));

        // Update allowed fields
        if (updatedUser.getRole() != null) {
            if (updatedUser.getRole() != UserRole.ADMIN &&
                updatedUser.getRole() != UserRole.OPERATOR &&
                updatedUser.getRole() != UserRole.END_USER) {
                throw new InvalidInputException("Invalid role: " + updatedUser.getRole());
            }
            entity.setRole(updatedUser.getRole());
        }
        if (updatedUser.getUsername() != null) {
        	if (updatedUser.getUsername().isEmpty()) {
                throw new InvalidInputException("Username cannot be empty");
            }
            entity.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getAvatar() != null) {
        	 if (updatedUser.getAvatar().isEmpty()) {
                 throw new InvalidInputException("Avatar cannot be empty");
             }
            entity.setAvatar(updatedUser.getAvatar());
        }

        // Save the updated user entity in the database
        usersCrud.save(entity);
        UserBoundary updatedUserBoundary = userConverter.toBoundary(entity);
        logger.info("User updated: " + updatedUserBoundary); // Log user update

        return updatedUserBoundary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBoundary> getAllUsers(String adminSystemID, String adminEmail, int page, int size) {
        // Verify admin access
        String adminUniqueId = generateUniqueId(adminEmail, adminSystemID);
        UserEntity admin = usersCrud.findById(adminUniqueId).orElseThrow(() ->
                new UnauthorizedAccessException("Admin user not found"));

        if (admin.getRole() != UserRole.ADMIN) {
            throw new ForbiddenAccessException("Only ADMIN users can retrieve all users");
        }

        List<UserBoundary> users = usersCrud.findAll(PageRequest.of(page, size))
                .stream()
                .map(userConverter::toBoundary)
                .collect(Collectors.toList());

        logger.info("Admin " + adminEmail + " retrieved all users. Total users: " + users.size()); // Log user retrieval
        return users;
    }

    @Override
    @Transactional(readOnly = false) // Write transaction
    public void deleteAllUsers(String adminSystemID, String adminEmail) {
        // Generate uniqueId for admin lookup
        String adminUniqueId = generateUniqueId(adminEmail, adminSystemID);

        // Verify that the requesting user is an admin
        UserEntity admin = usersCrud.findById(adminUniqueId).orElseThrow(() ->
                new UnauthorizedAccessException("Admin user not found"));

        if (admin.getRole() != UserRole.ADMIN) {
            throw new ForbiddenAccessException("Only ADMIN users can delete all users");
        }

        // Delete all users from the database
        usersCrud.deleteAll();
        logger.warn("Admin " + adminEmail + " deleted all users!"); // Log deletion of all users

    }
    
    @Override
    @Transactional(readOnly = true)
    public UserRole getUserRole(String systemID, String email) {
    	
        // Generate unique ID
        String uniqueId = generateUniqueId(email, systemID);

        // Retrieve user from database
        UserEntity user = usersCrud.findById(uniqueId).orElseThrow(() ->
                new NotFoundException("User with systemID " + systemID + " and email " + email + " not found"));

        logger.info("User role retrieved: " + email + " - Role: " + user.getRole()); // Log role retrieval
        // Return the user's role
        return user.getRole();
    }


    // Helper method to generate uniqueId
    private String generateUniqueId(String email, String systemID) {
        return email + "@@" + systemID;
    }
    
    private void validateNewUserBoundary(NewUserBoundary newUserBoundary) {
        if (newUserBoundary.getEmail() == null || newUserBoundary.getEmail().isEmpty()) {
            throw new InvalidInputException("Email cannot be null or empty");
        }
        if (newUserBoundary.getUsername() == null || newUserBoundary.getUsername().isEmpty()) {
            throw new InvalidInputException("Username cannot be null or empty");
        }
        if (newUserBoundary.getAvatar() == null || newUserBoundary.getAvatar().isEmpty()) {
            throw new InvalidInputException("Avatar cannot be null or empty");
        }
        if (newUserBoundary.getRole() == null) {
            throw new InvalidInputException("Role cannot be null");
        }
        if (newUserBoundary.getRole() != UserRole.ADMIN &&
            newUserBoundary.getRole() != UserRole.OPERATOR &&
            newUserBoundary.getRole() != UserRole.END_USER) {
            throw new InvalidInputException("Invalid role: " + newUserBoundary.getRole());
        }
    }
    
    

    // Helper method to convert UserEntity to UserBoundary
    private UserBoundary toBoundary(UserEntity entity) {
        // Decompose uniqueId into email and systemID
        String[] parts = entity.getUniqueId().split("@@");
        String email = parts[0];
        String systemID = parts[1];

        return new UserBoundary(
                new UserId(systemID, email),
                entity.getRole(),
                entity.getUsername(),
                entity.getAvatar()
        );
    }
}
