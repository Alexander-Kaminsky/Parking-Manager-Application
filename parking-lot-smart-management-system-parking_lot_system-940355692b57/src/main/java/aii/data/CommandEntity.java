package aii.data;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Map;

import aii.converter.CommandAttributesMapToStringConverter;

@Entity
@Table(name = "COMMANDS")
public class CommandEntity {

    @Id
    private String commandId; // Combination of systemID and unique command identifier (e.g., "2025a.demo@@5")

    private String command; // The command name (e.g., "shut-down")

    private String invokedBy; // Combination of userSystemID and email (e.g., "2025a.demo@@joannane@demo.org")

    private String targetObject; // Combination of objectSystemID and objectId (e.g., "2025a.demo@@101")

    @Temporal(TemporalType.TIMESTAMP)
    private Date invocationTimestamp; // Timestamp of when the command was invoked

    @Lob
    @Convert(converter = CommandAttributesMapToStringConverter.class)
    private Map<String, Object> commandAttributes; // Command attributes stored as a JSON string in the DB

    // Getters and Setters
    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(String invokedBy) {
        this.invokedBy = invokedBy;
    }

    public String getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }

    public Date getInvocationTimestamp() {
        return invocationTimestamp;
    }

    public void setInvocationTimestamp(Date invocationTimestamp) {
        this.invocationTimestamp = invocationTimestamp;
    }

    public Map<String, Object> getCommandAttributes() {
        return commandAttributes;
    }

    public void setCommandAttributes(Map<String, Object> commandAttributes) {
        this.commandAttributes = commandAttributes;
    }
}
