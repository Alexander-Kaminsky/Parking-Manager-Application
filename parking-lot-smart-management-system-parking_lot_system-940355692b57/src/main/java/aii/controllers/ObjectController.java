package aii.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import aii.boundary.ObjectBoundary;
import aii.data.DistanceUnits;
import aii.logic.InvalidInputException;
import aii.logic.NotFoundException;
import aii.logic.ObjectsLogic;
import aii.logic.ObjectsLogicImplementation;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/aii/objects")
public class ObjectController {

	private final ObjectsLogic objectLogic;

	// Constructor to inject ObjectService
	public ObjectController(ObjectsLogic objectLogic) {
		this.objectLogic = objectLogic;
	}

	// Create a new object
	@PostMapping(
			consumes = {MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary createObject(
			@RequestBody ObjectBoundary objectBoundary) {	

		String userSystemID = objectBoundary.getCreatedBy().getUserId().getSystemID();
		String userEmail = objectBoundary.getCreatedBy().getUserId().getEmail();
		return objectLogic.create(userSystemID, userEmail, objectBoundary);
	}


	// Retrieve a specific object
	@GetMapping(
			path = {"/{systemID}/{id}"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary getObject(
			@PathVariable("systemID") String objectSystemID,
			@PathVariable("id") String objectId,
			@RequestParam("userSystemID") String userSystemID,
			@RequestParam("userEmail") String userEmail){

		// Retrieve the object
		Optional<ObjectBoundary> optionalObject = objectLogic.getObject(userSystemID, userEmail, objectSystemID, objectId);
		// Check if object exists
		if (optionalObject.isEmpty()) {
			throw new NotFoundException("Object not found for the given IDs.");
		}
		// Retrieve and cast the object
		ObjectBoundary objectBoundary = optionalObject.get();
		return objectBoundary;
	}


	// Update an existing object
	@PutMapping(
			path = {"/{systemID}/{id}"},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary updateObject(
			@PathVariable("systemID") String objectSystemID,
			@PathVariable("id") String objectId,
			@RequestParam("userSystemID") String userSystemID,
			@RequestParam("userEmail") String userEmail,
			@RequestBody ObjectBoundary updatedObject) {

		return objectLogic.update(userSystemID, userEmail, objectSystemID, objectId, updatedObject);
	}


	// Retrieve all objects
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<ObjectBoundary> getAllObjects(			
			@RequestParam("userSystemID") String userSystemID,
			@RequestParam("userEmail") String userEmail, 
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size) {

		return objectLogic.getAllObjects(userSystemID, userEmail, page, size);
	}

	// New Search Objects by exact alias with pagination support
	@GetMapping(
			path = "/search/byAlias/{alias}",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<ObjectBoundary> getObjectsByAlias(
			@PathVariable("alias") String alias,
			@RequestParam("userSystemID") String userSystemID,
			@RequestParam("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		// Validate inputs
		if (alias == null || alias.isEmpty()) {
			throw new InvalidInputException("Alias cannot be null or empty.");
		}

		// Delegate to logic layer
		return this.objectLogic.getObjectsByAlias(userSystemID, userEmail, alias, page, size);
	}

	// New Search Objects by alias pattern with pagination support
	@GetMapping(
			path = "/search/byAliasPattern/{pattern}",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<ObjectBoundary> getObjectsByAliasPattern(
			@PathVariable("pattern") String pattern,
			@RequestParam("userSystemID") String userSystemID,
			@RequestParam("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		// Validate inputs
		if (pattern == null || pattern.isEmpty()) {
			throw new InvalidInputException("Pattern cannot be null or empty.");
		}

		// Delegate to logic layer
		return this.objectLogic.getObjectsByAliasPattern(userSystemID, userEmail, pattern, page, size);
	}

	// New Search Objects by type with pagination support
	@GetMapping(
			path = "/search/byType/{type}",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<ObjectBoundary> getObjectsByType(
			@PathVariable("type") String type,
			@RequestParam("userSystemID") String userSystemID,
			@RequestParam("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		// Delegate to logic layer
		return this.objectLogic.getObjectsByType(userSystemID, userEmail, type, page, size);
	}

	// New Search Objects by type and status with pagination support
	@GetMapping(
			path = "/search/byTypeAndStatus/{type}/{status}",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<ObjectBoundary> getObjectsByTypeAndStatus(
			@PathVariable("type") String type,
			@PathVariable("status") String status,
			@RequestParam("userSystemID") String userSystemID,
			@RequestParam("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		// Delegate to logic layer
		return this.objectLogic.getObjectsByTypeAndStatus(userSystemID, userEmail, type, status, page, size);
	}
	
	// New Search Objects by location with pagination support
	@GetMapping(
			path = "/search/byLocation/{lat}/{lng}/{distance}",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<ObjectBoundary> getObjectsByLocation(
			@PathVariable("lat") double lat,
			@PathVariable("lng") double lng,
			@PathVariable("distance") double distance,
			@RequestParam(value = "units", defaultValue = "NEUTRAL") DistanceUnits units,
			@RequestParam(name = "useCircle", required = false, defaultValue = "false") boolean useCircle,
			@RequestParam("userSystemID") String userSystemID,
			@RequestParam("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		// Delegate to logic layer
		return this.objectLogic.getObjectsByLocation(userSystemID, userEmail, lat, lng, distance, units, useCircle, page, size);
	}

}
