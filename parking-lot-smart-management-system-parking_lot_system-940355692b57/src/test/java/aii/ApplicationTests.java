package aii;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import aii.boundary.*;
import aii.boundary.CommandBoundary.InvokedBy;
import aii.boundary.CommandBoundary.TargetObject;
import aii.data.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ApplicationTests {

	@LocalServerPort
	private int port;

	private String baseUrl;
	private RestTemplate restTemplate;

	@Value("${spring.application.name}")
	private String systemID;

	@BeforeEach
	public void setup() {
		this.baseUrl = "http://localhost:" + this.port;
		this.restTemplate = new RestTemplate();
	}

	@AfterEach
	public void tearDown() {
		try {
			this.restTemplate
					.delete(baseUrl + "/aii/admin/users?userSystemID=" + systemID + "&userEmail=admin@demo.org");
		} catch (Exception e) {
			System.err.println("Error clearing users: " + e.getMessage());
		}
		try {
			this.restTemplate
					.delete(baseUrl + "/aii/admin/objects?userSystemID=" + systemID + "&userEmail=admin@demo.org");
		} catch (Exception e) {
			System.err.println("Error clearing objects: " + e.getMessage());
		}
		try {
			this.restTemplate
					.delete(baseUrl + "/aii/admin/commands?userSystemID=" + systemID + "&userEmail=admin@demo.org");
		} catch (Exception e) {
			System.err.println("Error clearing commands: " + e.getMessage());
		}
	}

	@Test
	@DisplayName("Test: Context Loads Successfully")
	public void contextLoads() {
		assertThat(systemID).isNotNull();
	}

	@Test
	@DisplayName("Test: Create and Retrieve a User")
	public void testCreateAndRetrieveUser() {
		NewUserBoundary newUser = new NewUserBoundary("user1@demo.org", UserRole.END_USER, "User One", "avatar-url");
		UserBoundary createdUser = restTemplate.postForObject(baseUrl + "/aii/users", newUser, UserBoundary.class);

		assertThat(createdUser).isNotNull();
		assertThat(createdUser.getUserId().getEmail()).isEqualTo("user1@demo.org");

		UserBoundary retrievedUser = restTemplate.getForObject(baseUrl + "/aii/users/login/{systemID}/{email}",
				UserBoundary.class, systemID, "user1@demo.org");

		assertThat(retrievedUser).isNotNull();
		assertThat(retrievedUser.getUserId().getEmail()).isEqualTo("user1@demo.org");
	}

	@Test
	@DisplayName("Test: Forbidden Access for ADMIN to Create Objects")
	public void testForbiddenAccessForAdminToCreateObjects() {
		// Create ADMIN user
		NewUserBoundary adminUser = new NewUserBoundary("admin@demo.org", UserRole.ADMIN, "Admin User", "admin-avatar");
		restTemplate.postForObject(baseUrl + "/aii/users", adminUser, UserBoundary.class);

		// Prepare an object for creation
		ObjectBoundary newObject = new ObjectBoundary();
		newObject.setType("TestType");
		newObject.setAlias("TestAlias");
		newObject.setActive(true);
		newObject.setCreatedBy(new CreatedBy(new UserBoundary.UserId(systemID, "admin@demo.org")));

		// Validate that creating an object with ADMIN role is forbidden
		assertThrows(HttpClientErrorException.Forbidden.class, () -> {
			restTemplate.postForObject(baseUrl + "/aii/objects", newObject, ObjectBoundary.class);
		});
	}

	@Test
	@DisplayName("Test: Forbidden Access for END_USER to Create Objects")
	public void testForbiddenAccessForEndUserToCreateObjects() {
		// Create END_USER
		NewUserBoundary endUser = new NewUserBoundary("user2@demo.org", UserRole.END_USER, "End User", "avatar-end");
		restTemplate.postForObject(baseUrl + "/aii/users", endUser, UserBoundary.class);

		// Prepare an object for creation
		ObjectBoundary newObject = new ObjectBoundary();
		newObject.setType("TestType");
		newObject.setAlias("TestAlias");
		newObject.setActive(true);
		newObject.setCreatedBy(new CreatedBy(new UserBoundary.UserId(systemID, "user2@demo.org")));

		// Validate that creating an object with END_USER role is forbidden
		assertThrows(HttpClientErrorException.Forbidden.class, () -> {
			restTemplate.postForObject(baseUrl + "/aii/objects", newObject, ObjectBoundary.class);
		});
	}

	@Test
	@DisplayName("Test: Update User")
	public void testUpdateUser() {
		// Create a new user (END_USER role)
		NewUserBoundary newUser = new NewUserBoundary("user1@demo.org", UserRole.END_USER, "User One", "avatar-url");
		UserBoundary createdUser = restTemplate.postForObject(baseUrl + "/aii/users", newUser, UserBoundary.class);

		// Ensure the user was created successfully
		assertThat(createdUser).isNotNull();
		assertThat(createdUser.getUserId().getEmail()).isEqualTo("user1@demo.org");
		assertThat(createdUser.getUsername()).isEqualTo("User One"); // Initial username
		assertThat(createdUser.getAvatar()).isEqualTo("avatar-url"); // Initial avatar

		// Update the user's username and avatar
		createdUser.setUsername("UpdatedUsername");
		createdUser.setAvatar("updated-avatar-url");

		// Send PUT request to update the user information (username and avatar)
		restTemplate.put(baseUrl + "/aii/users/{systemID}/{email}", createdUser, systemID, "user1@demo.org");

		// Retrieve the updated user
		UserBoundary updatedUser = restTemplate.getForObject(baseUrl + "/aii/users/login/{systemID}/{email}",
				UserBoundary.class, systemID, "user1@demo.org");

		// Ensure the user's username and avatar have been updated correctly
		assertThat(updatedUser).isNotNull();
		assertThat(updatedUser.getUsername()).isEqualTo("UpdatedUsername");
		assertThat(updatedUser.getAvatar()).isEqualTo("updated-avatar-url");
	}

	@Test
	@DisplayName("Test: Invalid Object Creation with Empty Fields")
	public void testInvalidObjectCreationWithEmptyFields() {
		// Create an OPERATOR user
		NewUserBoundary operatorUser = new NewUserBoundary("operator@demo.org", UserRole.OPERATOR, "Operator User",
				"operator-avatar");
		restTemplate.postForObject(baseUrl + "/aii/users", operatorUser, UserBoundary.class);

		// Prepare an object for creation with empty type
		ObjectBoundary newObject = new ObjectBoundary();
		newObject.setType(""); // Type cannot be empty
		newObject.setAlias("TestAlias");
		newObject.setStatus("ACTIVE");
		newObject.setCreatedBy(new CreatedBy(new UserBoundary.UserId(systemID, "operator@demo.org")));

		// Verify that creating an object with empty type should throw an
		// InvalidInputException
		assertThrows(HttpClientErrorException.BadRequest.class, () -> {
			restTemplate.postForObject(baseUrl + "/aii/objects", newObject, ObjectBoundary.class);
		});

		// Prepare an object with empty alias
		newObject.setType("TestType");
		newObject.setAlias(""); // Alias cannot be empty

		// Verify that creating an object with empty alias should throw an
		// InvalidInputException
		assertThrows(HttpClientErrorException.BadRequest.class, () -> {
			restTemplate.postForObject(baseUrl + "/aii/objects", newObject, ObjectBoundary.class);
		});

		// Prepare an object with empty status
		newObject.setAlias("TestAlias");
		newObject.setStatus(""); // Status cannot be empty

		// Verify that creating an object with empty status should throw an
		// InvalidInputException
		assertThrows(HttpClientErrorException.BadRequest.class, () -> {
			restTemplate.postForObject(baseUrl + "/aii/objects", newObject, ObjectBoundary.class);
		});	
	}
	
	@Test
	@DisplayName("Test: Creating a User with an Existing Email Fails")
	public void testCreateUserWithExistingEmail() {
	    NewUserBoundary user = new NewUserBoundary("duplicate@demo.org", UserRole.END_USER, "Duplicate User", "avatar");
	    restTemplate.postForObject(baseUrl + "/aii/users", user, UserBoundary.class);

	    // Try to create the same user again
	    assertThrows(HttpClientErrorException.BadRequest.class, () -> {
	        restTemplate.postForObject(baseUrl + "/aii/users", user, UserBoundary.class);
	    });
	}

	@Test
	@DisplayName("Test: Login with an Unregistered Email Fails")
	public void testLoginWithUnregisteredEmail() {
	    assertThrows(HttpClientErrorException.NotFound.class, () -> {
	        restTemplate.getForObject(baseUrl + "/aii/users/login/{systemID}/{email}",
	                UserBoundary.class, systemID, "notexist@demo.org");
	    });
	}

	@Test
	@DisplayName("Test: Creating an Object with an Invalid Type Fails")
	public void testCreateObjectWithInvalidType() {
	    NewUserBoundary operator = new NewUserBoundary("operator@demo.org", UserRole.OPERATOR, "Operator User", "avatar");
	    restTemplate.postForObject(baseUrl + "/aii/users", operator, UserBoundary.class);

	    ObjectBoundary invalidObject = new ObjectBoundary();
	    invalidObject.setType(""); // Invalid type
	    invalidObject.setAlias("InvalidAlias");
	    invalidObject.setActive(true);
	    invalidObject.setCreatedBy(new CreatedBy(new UserBoundary.UserId(systemID, "operator@demo.org")));

	    assertThrows(HttpClientErrorException.BadRequest.class, () -> {
	        restTemplate.postForObject(baseUrl + "/aii/objects", invalidObject, ObjectBoundary.class);
	    });
	}


	
	

}
