package aii.boundary;

import aii.data.UserEntity;
import aii.data.UserRole;
import aii.logic.InvalidInputException;

public class UserBoundary {

    // Inner class to represent userId
    public static class UserId {
        private String systemID; // System ID for the user
        private String email;    // Email of the user

        // Default Constructor
        public UserId() {}

        // Full Constructor
        public UserId(String systemID, String email) {
            this.systemID = systemID;
            this.setEmail(email); // Use the setter to include validation
        }

        // Getters and Setters
        public String getSystemID() {
            return systemID;
        }

        public void setSystemID(String systemID) {
            this.systemID = systemID;
        }

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
        @Override
        public String toString() {
            return "UserId{" +
                    "systemID='" + systemID + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }

    // Fields in UserBoundary
    private UserId userId;  // Contains systemID and email
    private UserRole role;    // User role (ADMIN, OPERATOR, END_USER)
    private String username;
    private String avatar;

    // Default Constructor
    public UserBoundary() {}

    // Full Constructor
    public UserBoundary(UserId userId, UserRole role, String username, String avatar) {
        this.userId = userId;
        this.role = role;
        this.username = username;
        this.avatar = avatar;
    }
    
    // Constructor to initialize from UserEntity
    public UserBoundary(UserEntity entity) {
        // Decompose uniqueId into email and systemID
        String[] parts = entity.getUniqueId().split("@@");
        String email = parts[0];
        String systemID = parts[1];

        this.userId = new UserId(systemID, email);
        this.role = entity.getRole();
        this.username = entity.getUsername();
        this.avatar = entity.getAvatar();
    }

    // Convert UserBoundary to UserEntity
    public UserEntity toEntity() {
        // Generate uniqueId from email and systemID
        String uniqueId = this.userId.getEmail() + "@@" + this.userId.getSystemID();

        // Create UserEntity
        return new UserEntity(
            uniqueId,
            this.username,
            this.avatar,
            this.role
        );
    }

    // Getters and Setters
    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
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

    // toString Method
    @Override
    public String toString() {
        return "UserBoundary{" +
                "userId=" + userId +
                ", role='" + role + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
