package aii.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import aii.boundary.CommandBoundary;
import aii.boundary.UserBoundary;
import aii.logic.CommandsLogic;
import aii.logic.ForbiddenAccessException;
import aii.logic.InvalidInputException;
import aii.logic.NotFoundException;
import aii.logic.ObjectsLogic;
import aii.logic.UsersLogic;
import aii.data.UserRole;

import java.util.List;

@RestController
@RequestMapping("/aii/admin")
public class AdminController {

    private final UsersLogic usersLogic; // Logic for user operations
    private final ObjectsLogic objectLogic; // Service for object operations
    private final CommandsLogic commandService; // Service for command operations

    // Constructor for dependency injection
    public AdminController(UsersLogic usersLogic, ObjectsLogic objectLogic, CommandsLogic commandService) {
        this.usersLogic = usersLogic;
        this.objectLogic = objectLogic;
        this.commandService = commandService;
    }

    // DELETE all users - Requires admin credentials
    @DeleteMapping("/users")
    public void deleteAllUsers(
            @RequestParam("userSystemID") String userSystemID,
            @RequestParam("userEmail") String userEmail) {
        validateAdminAccess(userSystemID, userEmail);
        try {
            usersLogic.deleteAllUsers(userSystemID, userEmail);
        } catch (Exception e) {
            throw new InvalidInputException("Error occurred while attempting to delete users.", e);
        }
    }

    // DELETE all objects
    @DeleteMapping("/objects")
    public void deleteAllObjects(
            @RequestParam("userSystemID") String userSystemID,
            @RequestParam("userEmail") String userEmail) {
        validateAdminAccess(userSystemID, userEmail);
        try {
            objectLogic.deleteAllObjects(userSystemID, userEmail);
        } catch (Exception e) {
            throw new InvalidInputException("Error occurred while attempting to delete objects.", e);
        }
    }

    // DELETE all commands
    @DeleteMapping("/commands")
    public void deleteAllCommands(
            @RequestParam("userSystemID") String userSystemID,
            @RequestParam("userEmail") String userEmail) {
        validateAdminAccess(userSystemID, userEmail);
        try {
            commandService.deleteAllCommands();
        } catch (Exception e) {
            throw new InvalidInputException("Error occurred while attempting to delete commands.", e);
        }
    }

    // GET all users with pagination - Requires admin credentials
    @GetMapping(path = "/users", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<UserBoundary> getAllUsers(
            @RequestParam("userSystemID") String userSystemID,
            @RequestParam("userEmail") String userEmail,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        validateAdminAccess(userSystemID, userEmail);
        try {
            return usersLogic.getAllUsers(userSystemID, userEmail, page, size);
        } catch (Exception e) {
            throw new InvalidInputException("Error occurred while attempting to retrieve users.", e);
        }
    }

    // GET all commands with pagination
    @GetMapping(path = "/commands", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<CommandBoundary> getAllCommands(
            @RequestParam("userSystemID") String userSystemID,
            @RequestParam("userEmail") String userEmail,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        validateAdminAccess(userSystemID, userEmail);
        try {
            return commandService.getAllCommands(page, size);
        } catch (NotFoundException e) {
            throw new NotFoundException("No commands found.");
        } catch (Exception e) {
            throw new InvalidInputException("Error occurred while attempting to retrieve commands.", e);
        }
    }

    // Helper method to validate admin access
    private void validateAdminAccess(String userSystemID, String userEmail) {
        UserBoundary user = usersLogic.loginUser(userSystemID, userEmail);
        if (user.getRole() != UserRole.ADMIN) {
            throw new ForbiddenAccessException("Access denied: Only ADMIN users can perform this operation.");
        }
    }
}
