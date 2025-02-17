package aii.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import aii.boundary.CommandBoundary;
import aii.logic.CommandsLogic;
import aii.logic.InvalidInputException;

import java.util.List;

@RestController
@RequestMapping("/aii/commands")
public class CommandController {

    private final CommandsLogic commandService; // Use the interface instead of the implementation

    // Constructor to inject CommandsLogic
    public CommandController(CommandsLogic commandService) {
        this.commandService = commandService; // Inject the service via constructor
    }

    // Invoke a new command
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE}, 
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Object> invokeCommand(@RequestBody CommandBoundary commandBoundary) {
    	
        try {
            // Validate and delegate to the service layer
            return commandService.invokeCommand(commandBoundary);
        } catch (InvalidInputException e) {
            // Translate the custom exception to a 400 Bad Request HTTP response
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
    //old for validation with the team
/*    public List<Object> invokeCommand(@RequestBody CommandBoundary commandBoundary) {
        // Validate command input
        if (commandBoundary.getCommand() == null || commandBoundary.getCommand().isEmpty()) {
            throw new InvalidInputException("Command must not be null or empty");
        }

        if (commandBoundary.getInvokedBy() == null ||
            commandBoundary.getInvokedBy().getUserId() == null ||
            commandBoundary.getInvokedBy().getUserId().getEmail() == null ||
            commandBoundary.getInvokedBy().getUserId().getEmail().isEmpty()) {
            throw new InvalidInputException("Command must include a valid user email");
        }
        
      
        // Delegate to the command service
        return commandService.invokeCommand(commandBoundary);
    }
    */

