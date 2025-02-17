package aii.dal;

import java.util.List;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aii.data.ObjectEntity;

public interface ObjectCrud extends JpaRepository<ObjectEntity, String> {

	// Find all objects with optional active filter
	@Query("SELECT o FROM ObjectEntity o WHERE (:active IS NULL OR o.active = :active)")
	public List<ObjectEntity> findAllByActive(
			@Param("active") Boolean active,
			Pageable pageable);

	// Find by alias with optional active filter
	@Query("SELECT o FROM ObjectEntity o WHERE o.alias = :alias AND (:active IS NULL OR o.active = :active)")
	public List<ObjectEntity> findByAlias(
			@Param("alias") String alias,
			@Param("active") Boolean active, 
			Pageable pageable);

	// Find by alias pattern with optional active filter
	@Query("SELECT o FROM ObjectEntity o WHERE o.alias LIKE :pattern AND (:active IS NULL OR o.active = :active)")
	public List<ObjectEntity> findByAliasLike(
			@Param("pattern") String pattern,
			@Param("active") Boolean active,
			Pageable pageable);

	// Find by type with optional active filter
	@Query("SELECT o FROM ObjectEntity o WHERE o.type = :type AND (:active IS NULL OR o.active = :active)")
	public List<ObjectEntity> findByType(
			@Param("type") String type,
			@Param("active") Boolean active,
			Pageable pageable);

	// Find by type and status with optional active filter
	@Query("SELECT o FROM ObjectEntity o WHERE o.type = :type AND o.status = :status AND (:active IS NULL OR o.active = :active)")
	public List<ObjectEntity> findByTypeAndStatus(
			@Param("type") String type,
			@Param("status") String status,
			@Param("active") Boolean active,
			Pageable pageable);
	
	// Box search
	@Query("SELECT o FROM ObjectEntity o WHERE " +
	       "o.lat BETWEEN :minLat AND :maxLat AND " +
	       "o.lng BETWEEN :minLng AND :maxLng AND " +
	       "(:active IS NULL OR o.active = :active)")
	public List<ObjectEntity> findObjectsInBox(
	    @Param("minLat") double minLat,
	    @Param("maxLat") double maxLat,
	    @Param("minLng") double minLng,
	    @Param("maxLng") double maxLng,
	    @Param("active") Boolean active,
	    Pageable pageable);

	// Circle search using Haversine formula
	@Query("SELECT o FROM ObjectEntity o WHERE " +
		       "(6371.0 * 2 * ASIN(SQRT(" +
		       "POWER(SIN(RADIANS(:lat - o.lat) / 2), 2) + " +
		       "COS(RADIANS(:lat)) * COS(RADIANS(o.lat)) * " +
		       "POWER(SIN(RADIANS(:lng - o.lng) / 2), 2)" +
		       "))) <= :distanceInKm AND " +
		       "(:active IS NULL OR o.active = :active)")
		public List<ObjectEntity> findObjectsInCircle(
		    @Param("lat") double lat,
		    @Param("lng") double lng,
		    @Param("distanceInKm") double distanceInKm,  // Changed parameter name
		    @Param("active") Boolean active,
		    Pageable pageable);
}

