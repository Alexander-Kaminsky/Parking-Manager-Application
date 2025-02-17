package aii.boundary;

import java.util.Date;
import java.util.Map;

public class CommandBoundary {

    // Inner class to handle commandId structure
    public static class CommandId {
    	private String systemID;
        private String id;

        // Default Constructor
        public CommandId() {}

        // Full Constructor
        public CommandId(String systemID, String id) {
            this.systemID = systemID;
            this.id = id;
        }

        // Getters and Setters
        public String getSystemID() {
            return systemID;
        }

        public void setSystemID(String systemID) {
            this.systemID = systemID;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "CommandId{" +
                    "systemID='" + systemID + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    // Inner class to handle targetObject structure
    public static class TargetObject {
        private ObjectId objectId;

        // Default Constructor
        public TargetObject() {}

        // Full Constructor
        public TargetObject(ObjectId objectId) {
            this.objectId = objectId;
        }

        // Getters and Setters
        public ObjectId getObjectId() {
            return objectId;
        }

        public void setObjectId(ObjectId objectId) {
            this.objectId = objectId;
        }

        @Override
        public String toString() {
            return "TargetObject{" +
                    "objectId=" + objectId +
                    '}';
        }
    }

    // Inner class to handle invokedBy structure
    public static class InvokedBy {
        private UserBoundary.UserId userId;

        // Default Constructor
        public InvokedBy() {}

        // Full Constructor
        public InvokedBy(UserBoundary.UserId userId) {
            this.userId = userId;
        }

        // Getters and Setters
        public UserBoundary.UserId getUserId() {
            return userId;
        }

        public void setUserId(UserBoundary.UserId userId) {
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "InvokedBy{" +
                    "userId=" + userId +
                    '}';
        }
    }

    // Fields for CommandBoundary
    private CommandId commandId;
    private String command;
    private TargetObject targetObject;
    private Date invocationTimestamp;
    private InvokedBy invokedBy;
    private Map<String, Object> commandAttributes;

    // Default Constructor
    public CommandBoundary() {}

    // Full Constructor
    public CommandBoundary(CommandId commandId, String command, TargetObject targetObject,
                           Date invocationTimestamp, InvokedBy invokedBy, Map<String, Object> commandAttributes) {
        this.commandId = commandId;
        this.command = command;
        this.targetObject = targetObject;
        this.invocationTimestamp = invocationTimestamp;
        this.invokedBy = invokedBy;
        this.commandAttributes = commandAttributes;
    }

    // Getters and Setters
    public CommandId getCommandId() {
        return commandId;
    }

    public void setCommandId(CommandId commandId) {
        this.commandId = commandId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public TargetObject getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(TargetObject targetObject) {
        this.targetObject = targetObject;
    }

    public Date getInvocationTimestamp() {
        return invocationTimestamp;
    }

    public void setInvocationTimestamp(Date invocationTimestamp) {
        this.invocationTimestamp = invocationTimestamp;
    }

    public InvokedBy getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(InvokedBy invokedBy) {
        this.invokedBy = invokedBy;
    }

    public Map<String, Object> getCommandAttributes() {
        return commandAttributes;
    }

    public void setCommandAttributes(Map<String, Object> commandAttributes) {
        this.commandAttributes = commandAttributes;
    }

    // toString Method
    @Override
    public String toString() {
        return "CommandBoundary{" +
                "commandId=" + commandId +
                ", command='" + command + '\'' +
                ", targetObject=" + targetObject +
                ", invocationTimestamp=" + invocationTimestamp +
                ", invokedBy=" + invokedBy +
                ", commandAttributes=" + commandAttributes +
                '}';
    }
}
