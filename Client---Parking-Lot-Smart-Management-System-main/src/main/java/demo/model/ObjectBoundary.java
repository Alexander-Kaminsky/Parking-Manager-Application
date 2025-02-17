package demo.model;

import java.util.Date;
import java.util.Map;

public class ObjectBoundary {
	private ObjectId objectId;
    private String type;
    private String alias;
    private String status;
    private Boolean active = false;;
    private Location location;
    private Date createdTimestamp;
    private CreatedBy createdBy;
    private Map<String, Object> objectDetails;
    private String objectDetailsJson; // Add this field


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
		return createdTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.createdTimestamp = creationTimestamp;
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
	
	public String getObjectDetailsJson() {
	    return objectDetailsJson;
	}

	public void setObjectDetailsJson(String objectDetailsJson) {
	    this.objectDetailsJson = objectDetailsJson;
	}
	
//	public class ObjectId {
//		private String systemID = "2025a.Shir.Falach"; // Constant systemID
//		private String id;       // Unique object ID
//
//		public String getSystemID() {
//			return systemID;
//		}
//
//		public void setSystemID(String systemID) {
//			this.systemID = systemID;
//		}
//
//		public String getId() {
//			return id;
//		}
//
//		public void setId(String id) {
//			this.id = id;
//		}
//	}
	
//	public class Location {
//
//		private Double lat; // Latitude
//		private Double lng; // Longitude
//
//		public Location() {} 
//		
//		public double getLat() {
//			return lat;
//		}
//
//		public void setLat(double lat) {
//			this.lat = lat;
//		}
//
//		public double getLng() {
//			return lng;
//		}
//
//		public void setLng(double lng) {
//			this.lng = lng;
//		} 
//	}
//	
//	public class CreatedBy {
//		private UserBoundary.UserId userId;
//
//		public CreatedBy() {}
//		
//		public UserBoundary.UserId getUserId() {
//			return userId;
//		}
//
//		public void setUserId(UserBoundary.UserId userId) {
//			this.userId = userId;
//		}
//	}
}
