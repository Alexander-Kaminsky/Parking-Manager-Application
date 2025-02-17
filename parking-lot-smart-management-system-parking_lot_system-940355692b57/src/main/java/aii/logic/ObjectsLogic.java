package aii.logic;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import aii.boundary.ObjectBoundary;
import aii.data.DistanceUnits;

public interface ObjectsLogic {
	
	// Create new object
    public ObjectBoundary create(String userSystemID, String userEmail, ObjectBoundary objectBoundary);

	// Update object
    public ObjectBoundary update(String userSystemID, String userEmail, String objectSystemID, String objectId, ObjectBoundary updatedObject);

	// Get all objects
    public List<ObjectBoundary> getAllObjects(String userSystemID, String userEmail, int page, int size);

	// Get specific object
	public Optional<ObjectBoundary> getObject(String systemID, String userEmail, String objectSystemID, String objectId);

	// Delete all objects 
    public void deleteAllObjects(String adminSystemID, String adminEmail);

    // Search Objects by exact alias
    public List<ObjectBoundary> getObjectsByAlias(String userSystemID, String userEmail, String alias, int page, int size);

    // Search Objects by alias pattern
    public List<ObjectBoundary> getObjectsByAliasPattern(String userSystemID, String userEmail, String pattern, int page, int size);
    
    // Search Objects by type
    public List<ObjectBoundary> getObjectsByType(String userSystemID, String userEmail, String type, int page, int size);

    // Search Objects by type and status
    public List<ObjectBoundary> getObjectsByTypeAndStatus(String userSystemID, String userEmail, String type, String status, int page, int size);

    // Search Objects by location
	public List<ObjectBoundary> getObjectsByLocation(String userSystemID, String userEmail, double lat, double lng, double distance, DistanceUnits distanceUnits, boolean useCircle, int page, int size);


}
