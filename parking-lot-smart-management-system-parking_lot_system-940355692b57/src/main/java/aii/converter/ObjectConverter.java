package aii.converter;

import org.springframework.stereotype.Component;

import aii.boundary.CreatedBy;
import aii.boundary.Location;
import aii.boundary.ObjectBoundary;
import aii.boundary.ObjectId;
import aii.data.ObjectEntity;

@Component
public class ObjectConverter {
	// Object boundary to entity
	public ObjectEntity toEntity(ObjectBoundary boundary) {
		ObjectEntity entity = new ObjectEntity();
		String combinedId = generateId(boundary.getObjectId().getId(), boundary.getObjectId().getSystemID());
		entity.setId(combinedId); 
		entity.setType(boundary.getType());
		entity.setAlias(boundary.getAlias());
		entity.setStatus(boundary.getStatus());		
		
		Location loc = boundary.getLocation();
		//String combinedLocation = generateId(String.valueOf(loc.getLat()), String.valueOf(loc.getLng()));
		//entity.setLocation(combinedLocation);		
		entity.setLat(loc.getLat());
		entity.setLng(loc.getLng());

		entity.setActive(boundary.isActive());
		entity.setCreationTimestamp(boundary.getCreationTimestamp());		
		aii.boundary.UserBoundary.UserId uId = boundary.getCreatedBy().getUserId();
		String combinedCreatedBy = generateId(uId.getEmail(), uId.getSystemID());		
		entity.setCreatedBy(combinedCreatedBy);		
		entity.setObjectDetails(boundary.getObjectDetails());
		
		return entity;
	}
	
	// Object entity to boundary
    public ObjectBoundary toBoundary(ObjectEntity entity) {
    	ObjectBoundary boundary = new ObjectBoundary();
    	String[] idParts = splitId(entity.getId());
    	ObjectId objectId = new ObjectId(idParts[1], idParts[0]);
    	boundary.setObjectId(objectId);
    	boundary.setType(entity.getType());
    	boundary.setAlias(entity.getAlias());
    	boundary.setStatus(entity.getStatus());	    	
    	
 //   	String[] partsLocation = splitId(entity.getLocation());
 //   	Location loc = new Location(Double.parseDouble(partsLocation[0]), Double.parseDouble(partsLocation[1]));
    	 Location loc = new Location(entity.getLat(), entity.getLng());
    	boundary.setLocation(loc);   	
    	
    	boundary.setActive(entity.isActive());
    	boundary.setCreationTimestamp(entity.getCreationTimestamp());   	
    	String[] partsCreatedBy = splitId(entity.getCreatedBy());
    	CreatedBy cb = new CreatedBy(new aii.boundary.UserBoundary.UserId(partsCreatedBy[1], partsCreatedBy[0]));
    	boundary.setCreatedBy(cb);   	
    	boundary.setObjectDetails(entity.getObjectDetails());
    
    	return boundary;
    }
    
    // Helper method to generate Id
    private String generateId(String id, String systemID) {
        return id + "@@" + systemID;
    }
    
 // Helper method to split the combined Id
    public static String[] splitId(String combinedId) {
        // Split the string by the "@@" delimiter
        return combinedId.split("@@", 2); // Limit to 2 parts to handle cases where systemID might contain "@@"
    }

}
