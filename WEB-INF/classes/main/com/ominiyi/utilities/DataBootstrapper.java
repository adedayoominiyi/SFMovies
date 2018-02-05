package com.ominiyi.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.ominiyi.model.GeoLocation;
import com.ominiyi.model.Movie;
import com.ominiyi.model.Setting;

public class DataBootstrapper {

	public static final String DATA_SETUP_KEY = "DATA_ALREADY_SET";
	
	private DataBootstrapper() {}
	
	public static void setupData(InputStream moviesInputStream, InputStream geoLocationsInputStream)
			throws InterruptedException, ExecutionException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		List<Movie> moviesList = FileLoader.loadFile(moviesInputStream, Movie.class);
		List<GeoLocation> geoLocationsList = FileLoader.loadFile(geoLocationsInputStream, GeoLocation.class);

		Set<Movie> moviesSet = removeBadOrDuplicates(moviesList);
		Set<Movie> moviesWithGeoPointsSet = fillGeoPointsForMovies(moviesSet, geoLocationsList);
		indexMoviesForSearching(moviesWithGeoPointsSet);
		saveMoviesToDataStore(moviesWithGeoPointsSet);
		saveDataSetupSetting();
	}
	
	private static void indexMoviesForSearching(Set<Movie> moviesWithGeoPointsSet)
			throws InterruptedException, ExecutionException {
		LogHelper.log(DataBootstrapper.class, Level.INFO, "Deleting Existing Documents");
		FullTextSearch.deleteAllDocuments();
		LogHelper.log(DataBootstrapper.class, Level.INFO, "Deleted Existing Documents");

		LogHelper.log(DataBootstrapper.class, Level.INFO, "Indexing New Documents");
		FullTextSearch.indexAllMovies(moviesWithGeoPointsSet);
		LogHelper.log(DataBootstrapper.class, Level.INFO, "Indexed New Documents");
	}
	
	private static Set<Movie> fillGeoPointsForMovies(Set<Movie> moviesSet, List<GeoLocation> geoLocationsList) {
		Set<Movie> moviesWithGeoPointsSet = new HashSet<>();

		Map<String, Double[]> locationToGeoPointsMap = convertToMap(geoLocationsList);

		for (Movie movie : moviesSet) {
			Double[] latLong = locationToGeoPointsMap.get(movie.getLocation().toLowerCase());
			if (latLong != null) {
				movie.setLatitude(latLong[0]);
				movie.setLongitude(latLong[1]);

				moviesWithGeoPointsSet.add(movie);
			}
		}

		return moviesWithGeoPointsSet;
	}

	private static Map<String, Double[]> convertToMap(List<GeoLocation> geoLocationsList) {
		Map<String, Double[]> locationToGeoPointsMap = new HashMap<>();

		for (GeoLocation geoLocation : geoLocationsList) {
			Double latitude = geoLocation.getLatitude();
			Double longitude = geoLocation.getLongitude();

			if (Double.compare(latitude, 0.0) != 0 || Double.compare(longitude, 0.0) != 0) {
				locationToGeoPointsMap.put(geoLocation.getLocation(), new Double[] { latitude, longitude });
			}
		}

		return locationToGeoPointsMap;
	}

	private static Set<Movie> removeBadOrDuplicates(List<Movie> moviesList) {
		Set<Movie> moviesSet = new HashSet<>();

		for (Movie movie : moviesList) {
			if (movie.isValid()) {
				moviesSet.add(movie);
			}
		}
		return moviesSet;
	}
	
	@SuppressWarnings("unused")
	private static void saveMoviesToDataStore(final Set<Movie> moviesWithGeoPointsSet) {
		// Hack: Objectify needs a filter context to run normally. However, servlet
		// listeners run outside the scope of a filter so this is the workaround.
		// ObjectifyService.register(Movie.class);
		ObjectifyService.run(new VoidWork() {

			@Override
			public void vrun() {
				LogHelper.log(DataBootstrapper.class, Level.INFO, "Deleting Existing Database Records");
				Movie.deleteAllMovies();
				LogHelper.log(DataBootstrapper.class, Level.INFO, "Deleted Existing Database Records");

				LogHelper.log(DataBootstrapper.class, Level.INFO, "Adding New Database Records");
				Movie.saveMovies(moviesWithGeoPointsSet);
				LogHelper.log(DataBootstrapper.class, Level.INFO, "Added New Database Records");
			}
		});
	}
	
	private static void saveDataSetupSetting() {
		// Hack: Objectify needs a filter context to run normally. However, servlet
		// listeners run outside the scope of a filter so this is the workaround.
		// ObjectifyService.register(Movie.class);
		ObjectifyService.run(new VoidWork() {

			@Override
			public void vrun() {
				Setting setting = new Setting(DATA_SETUP_KEY, "true");
				setting.save();
			}
		});
	}
}
