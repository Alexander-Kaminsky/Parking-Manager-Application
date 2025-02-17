package aii.converter;

import org.springframework.stereotype.Component;
import aii.boundary.CommandBoundary;
import aii.boundary.ObjectId;
import aii.boundary.UserBoundary;
import aii.data.CommandEntity;

@Component
public class CommandConverter {

    // Convert CommandBoundary to CommandEntity
    public CommandEntity toEntity(CommandBoundary boundary) {
        if (boundary == null) {
            return null; // Handle null input safely
        }

        CommandEntity entity = new CommandEntity();

        // Set commandId as a concatenated string of systemID and id
        if (boundary.getCommandId() != null) {
            String commandId = generateId(boundary.getCommandId().getId(), boundary.getCommandId().getSystemID());
            entity.setCommandId(commandId);
        }

        // Set command
        entity.setCommand(boundary.getCommand());

        // Set invokedBy as a concatenated string of userSystemID and email
        if (boundary.getInvokedBy() != null && boundary.getInvokedBy().getUserId() != null) {
            UserBoundary.UserId userId = boundary.getInvokedBy().getUserId();
            String invokedBy = generateId(userId.getEmail(), userId.getSystemID());
            entity.setInvokedBy(invokedBy);
        }

        // Set targetObject as a concatenated string of objectSystemID and objectId
        if (boundary.getTargetObject() != null && boundary.getTargetObject().getObjectId() != null) {
            ObjectId objectId = boundary.getTargetObject().getObjectId();
            String targetObject = generateId(objectId.getId(), objectId.getSystemID());
            entity.setTargetObject(targetObject);
        }

        // Set invocation timestamp
        entity.setInvocationTimestamp(boundary.getInvocationTimestamp());

        // Set command attributes
        entity.setCommandAttributes(boundary.getCommandAttributes());

        return entity;
    }

    // Convert CommandEntity to CommandBoundary
    public CommandBoundary toBoundary(CommandEntity entity) {
        if (entity == null) {
            return null; // Handle null input safely
        }

        CommandBoundary boundary = new CommandBoundary();

        // Split and set commandId
        String[] commandIdParts = splitId(entity.getCommandId());
        boundary.setCommandId(new CommandBoundary.CommandId(commandIdParts[1], commandIdParts[0]));

        // Set command
        boundary.setCommand(entity.getCommand());

        // Split and set invokedBy
        if (entity.getInvokedBy() != null) {
            String[] invokedByParts = splitId(entity.getInvokedBy());
            UserBoundary.UserId userId = new UserBoundary.UserId(invokedByParts[1], invokedByParts[0]);
            boundary.setInvokedBy(new CommandBoundary.InvokedBy(userId));
        }

        // Split and set targetObject
        if (entity.getTargetObject() != null) {
            String[] targetObjectParts = splitId(entity.getTargetObject());
            ObjectId objectId = new ObjectId(targetObjectParts[1], targetObjectParts[0]);
            boundary.setTargetObject(new CommandBoundary.TargetObject(objectId));
        }

        // Set invocation timestamp
        boundary.setInvocationTimestamp(entity.getInvocationTimestamp());

        // Set command attributes
        boundary.setCommandAttributes(entity.getCommandAttributes());

        return boundary;
    }

    // Helper method to generate a concatenated ID
    private String generateId(String id, String systemID) {
        return id + "@@" + systemID;
    }

    // Helper method to split a concatenated ID
    private String[] splitId(String combinedId) {
        return combinedId.split("@@", 2); // Split into two parts (id, systemID)
    }
}
