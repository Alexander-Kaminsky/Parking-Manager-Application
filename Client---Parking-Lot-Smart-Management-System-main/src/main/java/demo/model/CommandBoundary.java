package demo.model;

import java.util.Date;
import java.util.Map;

import demo.model.UserBoundary.UserId;

public class CommandBoundary {

	private String command;
    private TargetObject targetObject;
    private Date invocationTimestamp;
    private InvokedBy invokedBy;
    private Map<String, Object> commandAttributes;

    public static class TargetObject {
        private ObjectId objectId;
        
        public ObjectId getObjectId() { return objectId; }
        public void setObjectId(ObjectId objectId) { this.objectId = objectId; }
    }

    public static class InvokedBy {
        private UserId userId;
        
        public UserId getUserId() { return userId; }
        public void setUserId(UserId userId) { this.userId = userId; }
    }

    // Getters and setters
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    
    public TargetObject getTargetObject() { return targetObject; }
    public void setTargetObject(TargetObject targetObject) { this.targetObject = targetObject; }
    
    public Date getInvocationTimestamp() { return invocationTimestamp; }
    public void setInvocationTimestamp(Date invocationTimestamp) { this.invocationTimestamp = invocationTimestamp; }
    
    public InvokedBy getInvokedBy() { return invokedBy; }
    public void setInvokedBy(InvokedBy invokedBy) { this.invokedBy = invokedBy; }
    
    public Map<String, Object> getCommandAttributes() { return commandAttributes; }
    public void setCommandAttributes(Map<String, Object> commandAttributes) { this.commandAttributes = commandAttributes; }
}
