package aii.boundary;

// class to handle objectId structure
public class ObjectId {
	private String systemID = "2025a.Shir.Falach"; // Constant systemID, predefined
	//private String systemID; // System ID for the object
	private String id;       // Unique object ID

	// Default Constructor
	public ObjectId() {}

	// Full Constructor
	public ObjectId(String systemID, String id) {
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
		return "ObjectId{" +
				"systemID='" + systemID + '\'' +
				", id='" + id + '\'' +
				'}';
	}
}
