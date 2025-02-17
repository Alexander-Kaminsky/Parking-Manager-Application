package aii.data;

import java.util.Date;
import java.util.Map;

import aii.boundary.CreatedBy;
import aii.boundary.Location;
import aii.converter.ParkingLotMapToStringConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity
@Table(name = "OBJECTS")
public class ObjectEntity {
	@Id
	private String objectId;       	// Combine the 2 strings with delimiter between them

	//save to DB - different types
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTimestamp;

	private String type;
	private String alias;
	private String status; 

	private boolean active;

	@Lob
	@Convert(converter = ParkingLotMapToStringConverter.class)
	private Map<String, Object> objectDetails;

	//	private String location;	
	private double lat;
	private double lng;

	private String createdBy;


	public ObjectEntity() {
	}

	// Getters and Setters
	public String getId() {
		return objectId;
	}

	public void setId(String id) {
		this.objectId = id;
	}

	//TODO add hard-coded? extract from full id?
	//	public String getSystemId() {
	//		
	//	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}

	public void setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}

//	public String getLocation() {
//		return location;
//	}
//
//	public void setLocation(String location) {
//		this.location = location;
//	}
	
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {		
		this.createdBy = createdBy;
	}
}
