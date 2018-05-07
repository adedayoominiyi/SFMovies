package com.ominiyi.utilities;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ominiyi.model.GeoLocation;
import com.ominiyi.model.Movie;
import com.ominiyi.utilities.MockCsvFile.AvailableCsv;

/**
 * @author Adedayo Ominiyi
 */
public class FileLoaderTest {

	@Test
	public void testLoadMovie() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		Movie expectedMovie = new Movie("180", "Epic Roasthouse (399 Embarcadero)", 2011);
		List<Movie> moviesList = FileLoader.loadFile(MockCsvFile.makeInputStream(AvailableCsv.FILM_LOCATIONS_CSV), Movie.class);
		Assert.assertEquals(1, moviesList.size());
		Assert.assertEquals(expectedMovie, moviesList.get(0));
	}

	@Test
	public void testLoadGeoPoints() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		GeoLocation expectedGeoLocation = new GeoLocation("epic roasthouse (399 embarcadero)", 37.7908379, -122.3893566);

		List<GeoLocation> geoLocationsList = FileLoader.loadFile(MockCsvFile.makeInputStream(AvailableCsv.GEO_POINTS_CSV), GeoLocation.class);
		Assert.assertEquals(1, geoLocationsList.size());
		Assert.assertEquals(expectedGeoLocation, geoLocationsList.get(0));
	}
}
