package aii.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import aii.boundary.NewUserBoundary;
import aii.boundary.UserBoundary;
import aii.logic.InvalidInputException;
import aii.logic.NotFoundException;
import aii.logic.UsersLogic;

@RestController
@RequestMapping("/aii/users")
public class UserController {

    private final UsersLogic usersLogic; // Use the interface, not the implementation

    public UserController(UsersLogic usersLogic) {
        this.usersLogic = usersLogic; // Dependency injection for the UsersLogic interface
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserBoundary createUser(@RequestBody NewUserBoundary newUserBoundary) {
        if (newUserBoundary == null) {
            throw new InvalidInputException("NewUserBoundary cannot be null");
        }
        return this.usersLogic.createUser(newUserBoundary);
    }

    @GetMapping(
            path = "/login/{systemID}/{userEmail}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserBoundary loginUser(
            @PathVariable("systemID") String systemID,
            @PathVariable("userEmail") String userEmail) {
        try {
            return this.usersLogic.loginUser(systemID, userEmail);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("User not found or invalid credentials: " + e.getMessage(), e);
        }
    }

    @PutMapping(
            path = "/{systemID}/{userEmail}",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void updateUser(
            @PathVariable("systemID") String systemID,
            @PathVariable("userEmail") String userEmail,
            @RequestBody UserBoundary updatedUser) {
        if (updatedUser == null || updatedUser.getUserId() == null) {
            throw new InvalidInputException("UpdatedUser or UserId cannot be null");
        }
        if (!systemID.equals(updatedUser.getUserId().getSystemID()) ||
            !userEmail.equals(updatedUser.getUserId().getEmail())) {
            throw new InvalidInputException("SystemID and Email in URL must match the ones in the UserBoundary");
        }
        
        this.usersLogic.updateUser(systemID, userEmail, updatedUser);
    }
}
