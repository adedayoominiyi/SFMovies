package com.ominiyi.model;
import org.apache.commons.csv.CSVRecord;

/**
 * The GeoLocation is a model class that represents a single geopoint for a single location.
 *
 * @author  Adedayo Ominiyi
 */
public class GeoLocation extends InputObject {

	private String location;
	private Double latitude;
	private Double longitude;
	
	public GeoLocation(String location, Double latitude, Double longitude) {
		super();
		this.location = cleanInput(location);
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public GeoLocation(CSVRecord csvRecord) {
		super();
		this.location = cleanInput(csvRecord.get("Location"));
		this.latitude = toDouble(csvRecord.get("Latitude"));
		this.longitude = toDouble(csvRecord.get("Longitude"));
	}
	
	public String getLocation() {
		return location;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof GeoLocation) {
			GeoLocation other = (GeoLocation) obj;
			
			boolean areLocationsEqual = (this.location == null && other.location == null) 
					|| (this.location.equalsIgnoreCase(other.location));
			
			return areLocationsEqual;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}
}
