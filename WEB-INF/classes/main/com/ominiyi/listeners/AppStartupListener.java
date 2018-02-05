package com.ominiyi.listeners;

import java.io.InputStream;
import java.util.logging.Level;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.validation.constraints.NotNull;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.ominiyi.model.Setting;
import com.ominiyi.utilities.DataBootstrapper;
import com.ominiyi.utilities.LogHelper;

@WebListener
public class AppStartupListener implements ServletContextListener {

	private final String ORIGINAL_CSV_FILE_PATH = "/WEB-INF/files/Film_Locations_in_San_Francisco.csv";
	private final String GEOCODED_CSV_FILE_PATH = "/WEB-INF/files/Locations_GeoPoints.csv";

	@Override
	public void contextInitialized(@NotNull ServletContextEvent sce) {
		LogHelper.log(AppStartupListener.class, Level.INFO, "AppStartupListener contextInitialized");
		beginDataSetup(sce);
	}

	private void beginDataSetup(@NotNull final ServletContextEvent sce) {
		// Hack: Objectify needs a filter context to run normally. However, servlet
		// listeners run outside the scope of a filter so this is the workaround.
		// ObjectifyService.register(Movie.class);
		ObjectifyService.run(new VoidWork() {

			@Override
			public void vrun() {
				Setting setting = Setting.findSetting(DataBootstrapper.DATA_SETUP_KEY);
				if (setting == null) {
					LogHelper.log(AppStartupListener.class, Level.INFO, "Data Setup Started");
				
					try (InputStream moviesInputStream = sce.getServletContext()
							.getResourceAsStream(ORIGINAL_CSV_FILE_PATH);
							InputStream geoLocationsInputStream = sce.getServletContext()
									.getResourceAsStream(GEOCODED_CSV_FILE_PATH);) {
						
						DataBootstrapper.setupData(moviesInputStream, geoLocationsInputStream);
					} catch (Exception ex) {
						LogHelper.log(AppStartupListener.class, Level.SEVERE, ex);
					}

					LogHelper.log(AppStartupListener.class, Level.INFO, "Data Setup Completed");
				}
			}
		});
	}

	@Override
	public void contextDestroyed(@NotNull ServletContextEvent sce) {
		LogHelper.log(AppStartupListener.class, Level.INFO, "AppStartupListener contextDestroyed");
	}
}