package aii.logic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import aii.boundary.CreatedBy;
import aii.boundary.Location;
import aii.boundary.ObjectBoundary;
import aii.boundary.ObjectId;
import aii.converter.ObjectConverter;
import aii.dal.ObjectCrud;
import aii.data.DistanceUnits;
import aii.data.ObjectEntity;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aii.data.UserRole;

@Service
public class ObjectsLogicImplementation implements ObjectsLogic {
	private ObjectCrud objectCrud; // Repository for database access
	private ObjectConverter converter;
	private final UsersLogic usersLogic;
    private final Log logger = LogFactory.getLog(ObjectsLogicImplementation.class); // Logger instance


	@Value("${spring.application.name}")
	private String systemID; // System ID value from application.properties

	public ObjectsLogicImplementation(ObjectCrud objectCrud, ObjectConverter converter, UsersLogic usersLogic) {
		this.objectCrud = objectCrud;
		this.converter = converter;
		this.usersLogic = usersLogic;
	}

	// Create new Object
	@Override
	@Transactional(/* readOnly = false */)
	public ObjectBoundary create(String userSystemID, String userEmail, ObjectBoundary objectBoundary) {
		UserRole role = validateUserRoleAndDB(userSystemID, userEmail);
		if (role == UserRole.END_USER)
			throw new ForbiddenAccessException("END_USER users cannot create objects.");

		// Validate objectBoundary fields
		if (objectBoundary == null) {
			throw new InvalidInputException("ObjectBoundary cannot be null.");
		}
		if (objectBoundary.getType() == null || objectBoundary.getType().trim().isEmpty()) {
			throw new InvalidInputException("Type cannot be null or empty.");
		}
		if (objectBoundary.getAlias() == null || objectBoundary.getAlias().trim().isEmpty()) {
			throw new InvalidInputException("Alias cannot be null or empty.");
		}
		if (objectBoundary.getStatus() == null || objectBoundary.getStatus().trim().isEmpty()) {
			throw new InvalidInputException("Status cannot be null or empty.");
		}

		objectBoundary.setObjectId(new ObjectId(systemID, UUID.randomUUID().toString())); // Assign a unique ID and
		// system ID
		objectBoundary.setCreationTimestamp(new Date()); // Set creation time stamp
		objectBoundary.setCreatedBy(new CreatedBy(new aii.boundary.UserBoundary.UserId(userSystemID, userEmail) // Set
				// createdBy
				// field
				));
        ObjectBoundary createdObject = this.converter.toBoundary(this.objectCrud.save(this.converter.toEntity(objectBoundary)));
        logger.info("Object created: " + createdObject); // Log object creation

		// Convert to ObjectEntity, save the new object entity in the database, convert back to UserBoundary and return
		return createdObject;
	}

	// get a specified object
	@Override
	@Transactional(readOnly = true)
	public Optional<ObjectBoundary> getObject(String systemID, String userEmail, String objectSystemID,
			String objectId) {
		// Validate object inputs
		if (objectSystemID == null || objectSystemID.trim().isEmpty()) {
			throw new InvalidInputException("objectSystemID cannot be null or empty.");
		}
		if (objectId == null || objectId.trim().isEmpty()) {
			throw new InvalidInputException("objectId cannot be null or empty.");
		}

		UserRole role = validateUserRoleAndDB(systemID, userEmail); // validate user role and check it exist in DB
		String combinedId = generateId(objectId, objectSystemID); // create the object id
		
        logger.info("Object retrieved: " + this.objectCrud.findById(combinedId).filter(entity -> filterByRole(entity, role)).map(this.converter::toBoundary)); // Log object retrieval
		return this.objectCrud.findById(combinedId) // find in DB, filter: if the object inactive and the role is
				// END_USER: exception
				.filter(entity -> filterByRole(entity, role)).map(this.converter::toBoundary);
	}

	// update object
	@Override
	@Transactional // (readOnly = false)
	public ObjectBoundary update(String userSystemID, String userEmail, String objectSystemID, String objectId,
			ObjectBoundary updatedObject) {
		// Validate object inputs
		if (objectSystemID == null || objectSystemID.trim().isEmpty()) {
			throw new InvalidInputException("objectSystemID cannot be null or empty.");
		}
		if (objectId == null || objectId.trim().isEmpty()) {
			throw new InvalidInputException("objectId cannot be null or empty.");
		}

		UserRole role = validateUserRoleAndDB(userSystemID, userEmail);
		if (role == UserRole.END_USER)
			throw new ForbiddenAccessException("END_USER users cannot update objects.");

		// Find object in database by id
		String combinedId = generateId(objectId, objectSystemID);
		ObjectEntity entity = objectCrud.findById(combinedId)
				.orElseThrow(() -> new NotFoundException("Object not found or invalid systemID"));

		// Update allowed fields - only if they contain non-default values
		if (updatedObject.getType() != null && !updatedObject.getType().trim().isEmpty()
				&& !updatedObject.getType().equals("string")) {
			entity.setType(updatedObject.getType().trim());
		}
		if (updatedObject.getAlias() != null && !updatedObject.getAlias().trim().isEmpty()
				&& !updatedObject.getAlias().equals("string")) {
			entity.setAlias(updatedObject.getAlias().trim());
		}
		if (updatedObject.getStatus() != null && !updatedObject.getStatus().trim().isEmpty()
				&& !updatedObject.getStatus().equals("string")) {
			entity.setStatus(updatedObject.getStatus().trim());
		}
		if (updatedObject.getLocation() != null
				&& (updatedObject.getLocation().getLat() != 0.1 || updatedObject.getLocation().getLng() != 0.1)) {
			Location loc = updatedObject.getLocation();
			entity.setLat(loc.getLat());
			entity.setLng(loc.getLng());
		}
		
		// Only update if explicitly changed
		if (updatedObject.isActive() != entity.isActive()) {
			entity.setActive(updatedObject.isActive());
		}

		// Add objectDetails update - only if it contains non-default values
		if (updatedObject.getObjectDetails() != null && !updatedObject.getObjectDetails().isEmpty()
				&& !isDefaultObjectDetails(updatedObject.getObjectDetails())) {
			entity.setObjectDetails(updatedObject.getObjectDetails());
		}

		// Save the updated object entity in the database
		this.objectCrud.save(entity);
		
        logger.info("Object updated: " + this.converter.toBoundary(entity)); // Log object update


		return this.converter.toBoundary(entity);
	}

	// get all objects
	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getAllObjects(String userSystemID, String userEmail, int page, int size) {
		UserRole role = validateUserRoleAndDB(systemID, userEmail); // validate user role and check it exist in DB
		Boolean activeFilter = (role == UserRole.END_USER) ? true : null;

		List<ObjectBoundary> objects = this.objectCrud
				.findAllByActive(activeFilter,
						PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
				.stream().map(this.converter::toBoundary) // Convert entities to boundary objects
				.toList();
		
        logger.info("Retrieved all objects, total count: " + objects.size()); // Log all objects retrieval

		
		return this.objectCrud
				.findAllByActive(activeFilter,
						PageRequest.of(page, size, Direction.DESC, "creationTimestamp", "objectId"))
				.stream().map(this.converter::toBoundary) // Convert entities to boundary objects
				.toList();
	}

	// Delete all objects
	@Override
	@Transactional // (readOnly = false)
	public void deleteAllObjects(String adminSystemID, String adminEmail) {
		// Validate field arn't empty or null
		if (adminSystemID == null || adminSystemID.trim().isEmpty()) {
			throw new InvalidInputException("UserSystemID cannot be null or empty.");
		}
		if (adminEmail == null || adminEmail.trim().isEmpty()) {
			throw new InvalidInputException("UserEmail cannot be null or empty.");
		}
		// check if the user exist in DB and get its role
		UserRole userRole = usersLogic.getUserRole(adminSystemID, adminEmail);
		if (userRole != UserRole.ADMIN)
			throw new ForbiddenAccessException("Only ADMIN users can delete all objects.");

		this.objectCrud.deleteAll();
		logger.warn("All objects deleted!"); // Log all objects deletion
	}

	// Search Objects by exact alias with pagination support
	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByAlias(String userSystemID, String userEmail, String alias, int page,
			int size) {
		if (alias == null || alias.trim().isEmpty()) {
			throw new InvalidInputException("alias cannot be null or empty.");
		}
		// Validate role and permissions
		UserRole role = validateUserRoleAndDB(userSystemID, userEmail);
		Boolean activeFilter = (role == UserRole.END_USER) ? true : null;

		return this.objectCrud
				.findByAlias(alias, activeFilter,
						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
				.stream().map(this.converter::toBoundary).collect(Collectors.toList());
	}

	// Search Objects by alias pattern with pagination support
	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByAliasPattern(String userSystemID, String userEmail, String pattern,
			int page, int size) {
		if (pattern == null || pattern.trim().isEmpty()) {
			throw new InvalidInputException("pattern cannot be null or empty.");
		}
		// Validate role and permissions
		UserRole role = validateUserRoleAndDB(userSystemID, userEmail);
		Boolean activeFilter = (role == UserRole.END_USER) ? true : null;

		return this.objectCrud
				.findByAliasLike("%" + pattern + "%", activeFilter,
						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
				.stream().map(this.converter::toBoundary).collect(Collectors.toList());
	}

	// Search Objects by type with pagination support
	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByType(String userSystemID, String userEmail, String type, int page,
			int size) {
		if (type == null || type.trim().isEmpty()) {
			throw new InvalidInputException("type cannot be null or empty.");
		}
		// Validate role and permissions
		UserRole role = validateUserRoleAndDB(userSystemID, userEmail);
		Boolean activeFilter = (role == UserRole.END_USER) ? true : null;

		return this.objectCrud
				.findByType(type, activeFilter,
						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
				.stream().map(this.converter::toBoundary).collect(Collectors.toList());
	}

	// Search Objects by type and status with pagination support
	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByTypeAndStatus(String userSystemID, String userEmail, String type,
			String status, int page, int size) {
		if (type == null || type.trim().isEmpty() || status == null || status.trim().isEmpty()) {
			throw new InvalidInputException("type / status cannot be null or empty.");
		}
		// Validate role and permissions
		UserRole role = validateUserRoleAndDB(userSystemID, userEmail);
		Boolean activeFilter = (role == UserRole.END_USER) ? true : null;

		return this.objectCrud
				.findByTypeAndStatus(type, status, activeFilter,
						PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
				.stream().map(this.converter::toBoundary).collect(Collectors.toList());

	}

	// Search Objects by location with pagination support -> basic implementation + circle implementation
	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByLocation(String userSystemID, String userEmail, double lat, double lng,
			double distance, DistanceUnits distanceUnits, boolean useCircle, int page, int size) {
		// VERIFY THE PARAMAETERS
		if (distance <= 0)
			throw new InvalidInputException("Distance must be positive!");
		if (lat < -90 || lat > 90)
			throw new InvalidInputException("Latitude must be between -90 and 90");
		if (lng < -180 || lng > 180)
			throw new InvalidInputException("Longtitude must be between -180 and 180");

		// Validate role and get active filter
		UserRole role = validateUserRoleAndDB(userSystemID, userEmail);
		Boolean activeFilter = (role == UserRole.END_USER) ? true : null;

		// Convert distance to kilometers if necessary
		double distanceInKm = convertToKilometers(distance, distanceUnits);

		if (useCircle) {
			// Use circle search (bonus implementation)
			return this.objectCrud.findObjectsInCircle(lat, lng, distanceInKm, // Pass the converted distance
					activeFilter, PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId")).stream()
					.map(this.converter::toBoundary).collect(Collectors.toList());
		} else {
			// Use box search (basic implementation)
			// Calculate the lat/lng deltas (approximate)
			double latDelta = distanceInKm / 111.0; // 1 degree latitude ≈ 111 km
			double lngDelta = distanceInKm / (111.0 * Math.cos(Math.toRadians(lat)));

			return this.objectCrud
					.findObjectsInBox(lat - latDelta, lat + latDelta, lng - lngDelta, lng + lngDelta, activeFilter,
							PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
					.stream().map(this.converter::toBoundary).collect(Collectors.toList());
		}
	}

	// Validate user: check if the user exist in DB and get its role
	private UserRole validateUserRoleAndDB(String userSystemID, String userEmail) {
		if (userSystemID == null || userSystemID.trim().isEmpty()) {
			throw new InvalidInputException("UserSystemID cannot be null or empty.");
		}
		if (userEmail == null || userEmail.trim().isEmpty()) {
			throw new InvalidInputException("UserEmail cannot be null or empty.");
		}
		// check if the user exist in DB and get its role
		UserRole userRole = usersLogic.getUserRole(userSystemID, userEmail);
		// ADMIN: cannot do anything on objects
		if (userRole == UserRole.ADMIN) {
			throw new ForbiddenAccessException("ADMIN users are not allowed to perform operations on OBJECT.");
		}

		return userRole;
	}

	// Filter by role: END_USER can only make operations when the object is active,OPERATOR can make operations on any object
	private boolean filterByRole(ObjectEntity entity, UserRole role) {
		if (role == UserRole.END_USER && !entity.isActive()) {
			throw new NotFoundException("Object not found.");
		}
		return true;
	}

	// Helper method to generate Id
	private String generateId(String id, String systemID) {
		return id + "@@" + systemID;
	}

	// Helper method to check if objectDetails contains default values
	private boolean isDefaultObjectDetails(Map<String, Object> objectDetails) {
		return objectDetails.entrySet().stream()
				.allMatch(entry -> entry.getKey().startsWith("additionalProp") && entry.getValue().equals("string"));
	}

	// Helper method to convert distances to kilometers
	private double convertToKilometers(double distance, DistanceUnits units) {
		if (units == null || units == DistanceUnits.NEUTRAL) {
			return distance; // Treat neutral units as kilometers
		}

		return switch (units) {
		case KILOMETERS -> distance;
		case MILES -> distance * 1.60934; // Convert miles to kilometers
		default -> distance;
		};
	}
}
