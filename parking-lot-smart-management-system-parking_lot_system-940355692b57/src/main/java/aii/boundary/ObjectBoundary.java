package aii.boundary;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import aii.boundary.UserBoundary.UserId;
import aii.data.ObjectEntity;
import aii.data.UserEntity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

public class ObjectBoundary {

	// Fields for ObjectBoundary
	private ObjectId objectId;
	private String type;
	private String alias;
	private String status; // e.g., "AVAILABLE"
	private Location location;
	private boolean active;
	private Date creationTimestamp;
	private CreatedBy createdBy;
	private Map<String, Object> objectDetails;

	// Default Constructor
	public ObjectBoundary() {
	}
	
	// Full Constructor
	public ObjectBoundary(ObjectId objectId, String type, String alias, String status,
			Location location, boolean active, Date creationTimestamp,
			CreatedBy createdBy, Map<String, Object> objectDetails) {
		this.objectId = objectId;
		this.type = type;
		this.alias = alias;
		this.status = status;
		this.location = location;
		this.active = active;
		this.creationTimestamp = creationTimestamp;
		this.createdBy = createdBy;
		this.objectDetails = objectDetails;
	}   

	// Getters and Setters
	public ObjectId getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public CreatedBy getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(CreatedBy createdBy) {
		this.createdBy = createdBy;
	}

	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}

	public void setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}

	@Override
	public String toString() {
		return "ObjectBoundary{" +
				"objectId=" + objectId +
				", type='" + type + '\'' +
				", alias='" + alias + '\'' +
				", status='" + status + '\'' +
				", location=" + location +
				", active=" + active +
				", creationTimestamp=" + creationTimestamp +
				", createdBy=" + createdBy +
				", objectDetails=" + objectDetails +
				'}';
	}
}
