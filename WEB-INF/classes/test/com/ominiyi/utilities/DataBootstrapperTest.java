package com.ominiyi.utilities;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.ominiyi.model.GeoLocation;
import com.ominiyi.model.Movie;
import com.ominiyi.utilities.MockCsvFile.AvailableCsv;

/**
 * @author Adedayo Ominiyi
 */
@PrepareForTest( { DataBootstrapper.class, FullTextSearch.class })
@RunWith(PowerMockRunner.class)
public class DataBootstrapperTest {

	@Test
	public void testSetupData() throws Exception {
		PowerMockito.spy(DataBootstrapper.class);
		
		PowerMockito.doNothing().when(DataBootstrapper.class, "indexMoviesForSearching", Mockito.anySet());
		PowerMockito.doNothing().when(DataBootstrapper.class, "saveMoviesToDataStore", Mockito.anySet());
		PowerMockito.doNothing().when(DataBootstrapper.class, "saveDataSetupSetting");
		
		DataBootstrapper.setupData(MockCsvFile.makeInputStream(AvailableCsv.FILM_LOCATIONS_CSV), MockCsvFile.makeInputStream(AvailableCsv.GEO_POINTS_CSV));
		
		PowerMockito.verifyPrivate(DataBootstrapper.class).invoke("removeBadOrDuplicates", Mockito.anyList());
		PowerMockito.verifyPrivate(DataBootstrapper.class).invoke("fillGeoPointsForMovies", Mockito.anySet(), Mockito.anyList());
		PowerMockito.verifyPrivate(DataBootstrapper.class).invoke("indexMoviesForSearching", Mockito.anySet());
		PowerMockito.verifyPrivate(DataBootstrapper.class).invoke("saveMoviesToDataStore", Mockito.anySet());
		PowerMockito.verifyPrivate(DataBootstrapper.class).invoke("saveDataSetupSetting");
	}
	
	@Test
	public void testIndexMoviesForSearching() throws Exception {
		PowerMockito.spy(DataBootstrapper.class);
		PowerMockito.spy(FullTextSearch.class);
		
		PowerMockito.doNothing().when(FullTextSearch.class, "deleteAllDocuments");
		PowerMockito.doNothing().when(FullTextSearch.class, "indexAllMovies", Mockito.anySet());
		
		Whitebox.invokeMethod(DataBootstrapper.class, "indexMoviesForSearching", Collections.emptySet());
		
		PowerMockito.verifyPrivate(FullTextSearch.class).invoke("deleteAllDocuments");
		PowerMockito.verifyPrivate(FullTextSearch.class).invoke("indexAllMovies", Mockito.anySet());
	}
	
	@Test
	public void testRemoveBadOrDuplicates() throws Exception {
		PowerMockito.spy(DataBootstrapper.class);
		
		Movie expectedMovie = new Movie("180", "Epic Roasthouse (399 Embarcadero)", 2011);
		List<Movie> moviesList = FileLoader.loadFile(MockCsvFile.makeInputStream(AvailableCsv.FILM_LOCATIONS_CSV), Movie.class);
		
		Assert.assertEquals(1, moviesList.size());
		Assert.assertEquals(expectedMovie, moviesList.get(0));
		
		moviesList.add(new Movie(expectedMovie.getTitle(), expectedMovie.getLocation(), expectedMovie.getReleaseYear()));
		Assert.assertEquals(2, moviesList.size());
		
		Set<Movie> moviesSet = Whitebox.invokeMethod(DataBootstrapper.class, "removeBadOrDuplicates", moviesList);
		
		Assert.assertEquals(1, moviesSet.size());
		Assert.assertTrue(moviesSet.contains(expectedMovie));
	}
	
	@Test
	public void testFillGeoPointsForMovies() throws Exception {
		PowerMockito.spy(DataBootstrapper.class);
		
		List<Movie> moviesList = FileLoader.loadFile(MockCsvFile.makeInputStream(AvailableCsv.FILM_LOCATIONS_CSV), Movie.class);
		List<GeoLocation> geoLocationsList = FileLoader.loadFile(MockCsvFile.makeInputStream(AvailableCsv.GEO_POINTS_CSV), GeoLocation.class);
		
		Set<Movie> moviesSet = Whitebox.invokeMethod(DataBootstrapper.class, "removeBadOrDuplicates", moviesList);
		Set<Movie> moviesWithGeoPointsSet = Whitebox.invokeMethod(DataBootstrapper.class, "fillGeoPointsForMovies", moviesSet, geoLocationsList);
		
		Movie expectedMovie = new Movie("180", "Epic Roasthouse (399 Embarcadero)", 2011);
		Assert.assertEquals(1, moviesWithGeoPointsSet.size());
		Assert.assertTrue(moviesWithGeoPointsSet.contains(expectedMovie));
		
		Movie moviesWithGeoPoint = moviesWithGeoPointsSet.iterator().next();
		Assert.assertEquals(37.7908379, moviesWithGeoPoint.getLatitude().doubleValue(), 0);
		Assert.assertEquals(-122.3893566, moviesWithGeoPoint.getLongitude().doubleValue(), 0);
	}
	
	@Test
	public void testConvertToMap() throws Exception {
		PowerMockito.spy(DataBootstrapper.class);
		
		GeoLocation expectedGeoLocation = new GeoLocation("epic roasthouse (399 embarcadero)", 37.7908379, -122.3893566);
		List<GeoLocation> geoLocationsList = FileLoader.loadFile(MockCsvFile.makeInputStream(AvailableCsv.GEO_POINTS_CSV), GeoLocation.class);
		
		Assert.assertEquals(1, geoLocationsList.size());
		Assert.assertEquals(expectedGeoLocation, geoLocationsList.get(0));
		
		Map<String, Double[]> locationToGeoPointsMap = Whitebox.invokeMethod(DataBootstrapper.class, "convertToMap", geoLocationsList);
		
		Assert.assertEquals(1, locationToGeoPointsMap.size());
		Assert.assertTrue(locationToGeoPointsMap.containsKey(expectedGeoLocation.getLocation()));
		
		Double[] latLong = locationToGeoPointsMap.get(expectedGeoLocation.getLocation());
		
		Assert.assertEquals(37.7908379, latLong[0], 0);
		Assert.assertEquals(-122.3893566, latLong[1], 0);
	}
}
