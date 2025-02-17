package aii.boundary;

// class to handle location structure
public class Location {

	private double lat; // Latitude
	private double lng; // Longitude

	// Default Constructor
	public Location() {}

	// Full Constructor
	public Location(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	// Getters and Setters
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

	@Override
	public String toString() {
		return "Location{" +
				"lat=" + lat +
				", lng=" + lng +
				'}';
	}  
}
