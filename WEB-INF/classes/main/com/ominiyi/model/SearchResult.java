package com.ominiyi.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The SearchResult class is used to hold search results.
 *
 * @author  Adedayo Ominiyi
 */
public class SearchResult {
	
	private String location;
	private double latitude;
	private double longitude;
	private Set<MovieForLocation> moviesForLocation = new TreeSet<>();
	
	public SearchResult() {
		super();
	}
	
	public SearchResult(String location, double latitude, double longitude) {
		super();
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String getLocation() {
		return location;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public Set<MovieForLocation> getMoviesForLocation() {
		return moviesForLocation;
	}

	public void addMovie(Movie movieToAdd) {
		moviesForLocation.add(new MovieForLocation(movieToAdd.getTitle(), movieToAdd.getReleaseYear()));
	}
	
	public static Collection<SearchResult> make(Set<Movie> moviesSet) {
		Map<String, SearchResult> mappings = new HashMap<>();
		
		for (Movie movie : moviesSet) {
			SearchResult searchResult;
			String locationLower = movie.getLocation().toLowerCase();
			if (!mappings.containsKey(locationLower)) {
				searchResult = new SearchResult(movie.getLocation(), movie.getLatitude(), movie.getLongitude());
				mappings.put(locationLower, searchResult);
			} else {
				searchResult = mappings.get(locationLower);
			}
			searchResult.addMovie(movie);
		}
		
		return mappings.values();
	}
	
	static class MovieForLocation implements Comparable<MovieForLocation> {
		private String title;
		private int releaseYear;
		
		public MovieForLocation() {
			super();
		}
		
		public MovieForLocation(String title, Integer releaseYear) {
			super();
			this.title = title;
			this.releaseYear = releaseYear == null ? 0 : releaseYear.intValue();
		}

		public String getTitle() {
			return title;
		}

		public Integer getReleaseYear() {
			return releaseYear;
		}

		@Override
		public int compareTo(MovieForLocation other) {
			if (other == null || (other.releaseYear == 0 && this.releaseYear != 0)) {
				return 1;
			}
			
			int releaseYearCompare = Integer.compare(this.releaseYear, other.releaseYear);
			return releaseYearCompare != 0 ? releaseYearCompare : this.title.compareToIgnoreCase(other.title);
		}
	}
}