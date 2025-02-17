package aii.boundary;

import aii.data.UserEntity;
import aii.data.UserRole;
import aii.logic.InvalidInputException;

public class NewUserBoundary {
    private String email;   // Email of the user
    private UserRole role;    // Role of the user (ADMIN, OPERATOR, END_USER)
    private String username; // Name of the user
    private String avatar;   // Avatar URL or identifier

    // Default Constructor
    public NewUserBoundary() {}

    // Full Constructor
    public NewUserBoundary(String email, UserRole role, String username, String avatar) {
        this.setEmail(email);
        this.role = role;
        this.username = username;
        this.avatar = avatar;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
    	if (!isValidEmail(email)) {
            throw new InvalidInputException("Email must be in a valid format, e.g., myemail@example.org");
        }
        this.email = email;
    }
    // Validate email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        return email != null && email.matches(emailRegex);
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
}
