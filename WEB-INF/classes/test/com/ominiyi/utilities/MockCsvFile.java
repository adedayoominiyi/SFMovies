package com.ominiyi.utilities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Adedayo Ominiyi
 */
public class MockCsvFile {

	public enum AvailableCsv {
		FILM_LOCATIONS_CSV, GEO_POINTS_CSV;

		private String filmLocationsCsv = "Title,Release Year,Locations,Fun Facts,Production Company,Distributor,Director,Writer,Actor 1,Actor 2,Actor 3\r\n"
				+ "180,2011,Epic Roasthouse (399 Embarcadero),,SPI Cinemas,,Jayendra,\"Umarji Anuradha, Jayendra, Aarthi Sriram, & Suba \",Siddarth,Nithya Menon,Priya Anand\r\n";
		private String geoPointsCsv = "Location,Latitude,Longitude\r\n"
				+ "epic roasthouse (399 embarcadero),37.7908379,-122.3893566\r\n";

		public String getCsvContent() {
			switch (this) {
				case FILM_LOCATIONS_CSV:
					return filmLocationsCsv;
	
				case GEO_POINTS_CSV:
					return geoPointsCsv;
			}
			
			return null;
		}
	}

	private MockCsvFile() {
	}

	public static InputStream makeInputStream(AvailableCsv availableCsv) {
		return new ByteArrayInputStream(availableCsv.getCsvContent().getBytes(StandardCharsets.UTF_8));
	}
}
