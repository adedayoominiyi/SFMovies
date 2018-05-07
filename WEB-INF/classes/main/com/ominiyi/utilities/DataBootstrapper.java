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
import java.util.logging.Logger;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.ominiyi.model.GeoLocation;
import com.ominiyi.model.Movie;
import com.ominiyi.model.Setting;

/**
 * The DataBootstrapper class is responsible for loading data into the AppEngine datastore.
 *
 * @author Adedayo Ominiyi
 */
public class DataBootstrapper {

	private final static Logger LOGGER = Logger.getLogger(DataBootstrapper.class.getName());
	public final static String DATA_SETUP_KEY = "DATA_ALREADY_SET";

	private DataBootstrapper() {
	}

	public static void setupData(InputStream moviesInputStream, InputStream geoLocationsInputStream)
			throws InterruptedException, ExecutionException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			IOException {
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
		LOGGER.info("Deleting Existing Documents");
		FullTextSearch.deleteAllDocuments();
		LOGGER.info("Deleted Existing Documents");

		LOGGER.info("Indexing New Documents");
		FullTextSearch.indexAllMovies(moviesWithGeoPointsSet);
		LOGGER.info("Indexed New Documents");
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
		ObjectifyService.run(new VoidWork() {

			@Override
			public void vrun() {
				LOGGER.info("Deleting Existing Database Records");
				Movie.deleteAllMovies();
				LOGGER.info("Deleted Existing Database Records");

				LOGGER.info("Adding New Database Records");
				Movie.saveMovies(moviesWithGeoPointsSet);
				LOGGER.info("Added New Database Records");
			}
		});
	}

	private static void saveDataSetupSetting() {
		// Hack: Objectify needs a filter context to run normally. However, servlet
		// listeners run outside the scope of a filter so this is the workaround.
		ObjectifyService.run(new VoidWork() {

			@Override
			public void vrun() {
				Setting setting = new Setting(DATA_SETUP_KEY, "true");
				setting.save();
			}
		});
	}
}
