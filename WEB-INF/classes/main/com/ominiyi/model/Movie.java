package com.ominiyi.model;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVRecord;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.GeoPoint;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * The Movie class is a model class that represents the details of a single movie.
 *
 * @author  Adedayo Ominiyi
 */
@Entity
public class Movie extends InputObject {
	
	@Id private Long id;
	private String title;
	private Integer releaseYear;
	private String location;
	private Double latitude;
	private Double longitude;
	
	static {
		ObjectifyService.register(Movie.class);
	}
	
	public Movie() {
		super();
	}
	
	public Movie(String title, String location, Integer releaseYear) {
		super();
		this.title = cleanInput(title);
		this.location = cleanInput(location);
		this.releaseYear = releaseYear;
	}
	
	public Movie(CSVRecord csvRecord) {
		super();
		this.title = cleanInput(csvRecord.get("Title"));
		this.location = cleanInput(csvRecord.get("Locations"));
		this.releaseYear = toInteger(csvRecord.get("Release Year"));
	}
	
	public String getTitle() {
		return title;
	}

	public String getLocation() {
		return location;
	}
	
	public Integer getReleaseYear() {
		return releaseYear;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Movie) {
			Movie other = (Movie) obj;
			
			boolean areTitlesEqual = (this.title == null && other.title == null) 
					|| (this.title.equalsIgnoreCase(other.title));
			
			boolean areLocationsEqual = (this.location == null && other.location == null) 
					|| (this.location.equalsIgnoreCase(other.location));
			
			boolean areReleaseYearsEqual = (this.releaseYear == null && other.releaseYear == null) 
					|| (this.releaseYear != null && other.releaseYear != null && Integer.compare(this.releaseYear, other.releaseYear) == 0);
			
			return (areTitlesEqual && areLocationsEqual && areReleaseYearsEqual);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((title == null) ? 0 : title.toLowerCase().hashCode());
		result = prime * result + ((location == null) ? 0 : location.toLowerCase().hashCode());
		result = prime * result + ((releaseYear == null) ? 0 : releaseYear.hashCode());
		
		return result;
	}
	
	public boolean isValid() {
		return (location != null) && !location.trim().isEmpty() && (title != null) && !title.trim().isEmpty();
	}
	
	public Document makeDocument() {
		Document doc =
		        Document.newBuilder()
		        	.addField(Field.newBuilder().setName("originalTitle").setText(title))
		        	.addField(Field.newBuilder().setName("originalLocation").setText(location))
		            .addField(Field.newBuilder().setName("title").setText(title.toLowerCase()))
		            .addField(Field.newBuilder().setName("location").setText(location.toLowerCase()))
		            .addField(Field.newBuilder().setName("releaseYear").setNumber(releaseYear == null ? 0 : releaseYear))
		            .addField(Field.newBuilder().setName("latitude").setNumber(latitude))
		            .addField(Field.newBuilder().setName("longitude").setNumber(longitude))
		            .addField(Field.newBuilder().setName("geoPoint").setGeoPoint(new GeoPoint(latitude, longitude)))
		            .build();
		
		return doc;
	}
	
	public static void saveMovies(Set<Movie> moviesSet) {
		if (moviesSet == null || moviesSet.isEmpty()) {
			return;
		}
		ofy().save().entities(moviesSet).now(); 
	}
	
	public static Set<Movie> fetchUniqueMovies() {
		List<Movie> allMovies =  fetchAllMovies();
		Set<Movie> uniqueMovies = new HashSet<>(allMovies);
		return uniqueMovies;
	}
	
	private static List<Movie> fetchAllMovies() {
		List<Movie> allMovies = ofy().load().type(Movie.class).list();
		
		return allMovies;
	}
	
	public static void deleteAllMovies() {
		List<Key<Movie>> keys = ofy().load().type(Movie.class).keys().list();
		ofy().delete().keys(keys).now();
	}
	
	public void printValues() {
		System.out.print("title: " + title);
		System.out.print(", location: " + location);
		System.out.print(", releaseYear: " + releaseYear);
		System.out.print(", latitude: " + latitude);
		System.out.println(", longitude: " + longitude);
	}
}
