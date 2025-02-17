package aii.logic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aii.boundary.CommandBoundary;
import aii.boundary.ObjectBoundary;
import aii.converter.CommandConverter;
import aii.dal.CommandsCrud;
import aii.data.CommandEntity;
import aii.data.UserRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommandsLogicImplementation implements CommandsLogic {

    private final CommandsCrud commandsCrud; // Repository for database access
    private final CommandConverter commandConverter; // Converter for Command objects
    //new
    private final ObjectsLogic objectsLogic; // Inject object logic to verify object state
    private final UsersLogic usersLogic; // To retrieve user roles
    private final Log logger = LogFactory.getLog(CommandsLogicImplementation.class); // Logger instance

    
    @Value("${spring.application.name}")
    private String systemID; // System ID from application.properties
    
    // Constructor for dependency injection
    public CommandsLogicImplementation(CommandsCrud commandsCrud, CommandConverter commandConverter, ObjectsLogic objectsLogic, UsersLogic usersLogic) {
        this.commandsCrud = commandsCrud;
        this.commandConverter = commandConverter;
        this.objectsLogic = objectsLogic;
        this.usersLogic = usersLogic;
    }

    // Invoke a new command
    @Override
    @Transactional
    public List<Object> invokeCommand(CommandBoundary commandBoundary) {    	
        validateCommandBoundary(commandBoundary);
        
        // Get user's role
        UserRole userRole = usersLogic.getUserRole(
                commandBoundary.getInvokedBy().getUserId().getSystemID(),               
                commandBoundary.getInvokedBy().getUserId().getEmail()
        );
        
        // Enforce role-based restrictions
        if (userRole == UserRole.ADMIN || userRole == UserRole.OPERATOR) {
            throw new ForbiddenAccessException("ADMIN / OPERATOR users are not allowed to execute commands.");
        }

        // Check object existence and active status for END_USER
        if (userRole == UserRole.END_USER) {
            verifyTargetObjectIsActive(commandBoundary);
        }
        
        commandBoundary.setInvocationTimestamp(new Date());
        commandBoundary.setCommandId(new CommandBoundary.CommandId(systemID, UUID.randomUUID().toString()));
        
        commandBoundary.getInvokedBy().getUserId().setSystemID(systemID);
        commandBoundary.getTargetObject().getObjectId().setSystemID(systemID);
        
        CommandEntity entity = this.commandConverter.toEntity(commandBoundary);
        commandsCrud.save(entity);
        
        CommandBoundary createdCommand = commandConverter.toBoundary(entity);
        logger.info("Command invoked: " + createdCommand); // Log command invocation


        return List.of(
            Map.of(
                "commandId", Map.of("systemID", commandBoundary.getCommandId().getSystemID(), "id", commandBoundary.getCommandId().getId()),
                "command", commandBoundary.getCommand(),
                "targetObject", Map.of("objectId", Map.of("systemID", commandBoundary.getTargetObject().getObjectId().getSystemID(), "id", commandBoundary.getTargetObject().getObjectId().getId())),
                "invocationTimestamp", commandBoundary.getInvocationTimestamp(),
                "invokedBy", Map.of("userId", Map.of("systemID", commandBoundary.getInvokedBy().getUserId().getSystemID(), "email", commandBoundary.getInvokedBy().getUserId().getEmail())),
                "commandAttributes", commandBoundary.getCommandAttributes()
            )
        );
    }
 // Helper method to validate CommandBoundary
    private void validateCommandBoundary(CommandBoundary commandBoundary) {
        if (commandBoundary.getCommand() == null || commandBoundary.getCommand().isEmpty()) {
            throw new InvalidInputException("Command must not be null or empty.");
        }
        if (commandBoundary.getInvokedBy() == null || commandBoundary.getInvokedBy().getUserId() == null ||
            commandBoundary.getInvokedBy().getUserId().getSystemID() == null ||
            commandBoundary.getInvokedBy().getUserId().getSystemID().isEmpty() ||
            commandBoundary.getInvokedBy().getUserId().getEmail() == null ||
            commandBoundary.getInvokedBy().getUserId().getEmail().isEmpty()) {
            throw new InvalidInputException("InvokedBy must have valid systemID and email.");
        }
        if (commandBoundary.getTargetObject() == null || commandBoundary.getTargetObject().getObjectId() == null ||
            commandBoundary.getTargetObject().getObjectId().getSystemID() == null ||
            commandBoundary.getTargetObject().getObjectId().getSystemID().isEmpty() ||
            commandBoundary.getTargetObject().getObjectId().getId() == null ||
            commandBoundary.getTargetObject().getObjectId().getId().isEmpty()) {
            throw new InvalidInputException("TargetObject must have valid systemID and ID.");
        }
    }
   
    private void verifyTargetObjectIsActive(CommandBoundary commandBoundary) {
    	
        String objectSystemID = commandBoundary.getTargetObject().getObjectId().getSystemID();
        String objectId = commandBoundary.getTargetObject().getObjectId().getId();

        Optional<ObjectBoundary> targetObject = objectsLogic.getObject(
        	    commandBoundary.getInvokedBy().getUserId().getSystemID(),
        	    commandBoundary.getInvokedBy().getUserId().getEmail(), 
        	    objectSystemID,
        	    objectId
        	);

        if (targetObject.isEmpty() || !targetObject.get().isActive()) {
            throw new InvalidInputException("The target Object is either not active or does not exist.");
        }
    }
    // Get all commands
    @Override
    @Transactional(readOnly = true)
    public List<CommandBoundary> getAllCommands(int page, int size) {
    	
        List<CommandBoundary> commands = commandsCrud.findAll(PageRequest.of(page, size))
                .stream()
                .map(commandConverter::toBoundary)
                .collect(Collectors.toList());

        logger.info("Retrieved all commands, total count: " + commands.size()); // Log all commands retrieval

        return commands;
    }


    // Delete all commands
    @Override
    @Transactional
    public void deleteAllCommands() {
        commandsCrud.deleteAll();
    }

    // Helper method to generate dummy objects
    @SuppressWarnings("unused")
	private List<Object> generateDummyObjects() {
        // Placeholder logic for dummy objects
        logger.warn("All commands deleted!"); // Log all commands deletion
        return List.of(
            new Object() {
                public String id = systemID + "-DummyObject1";
                public String type = "ExampleType1";
                public String alias = "ExampleAlias1";
            },
            new Object() {
                public String id = systemID + "-DummyObject2";
                public String type = "ExampleType2";
                public String alias = "ExampleAlias2";
            }
            
            
        );
        
        
    }
}
