package aii.data;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "USERS")
public class UserEntity {

    @Id
    private String uniqueId; // Combined Primary Key (email + systemID)
    private String username;
    private String avatar;

    @Enumerated(EnumType.STRING) // Store the role as a string in the database
    private UserRole role;

    public UserEntity() {
        // Default constructor
    }

    // Constructor for creating UserEntity
    public UserEntity(String uniqueId, String username, String avatar, UserRole role) {
        this.uniqueId = uniqueId;
        this.username = username;
        this.avatar = avatar;
        this.role = role;
    }

    // Getters and Setters
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
