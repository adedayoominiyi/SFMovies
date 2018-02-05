package com.ominiyi.utilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class FileLoader {

	private FileLoader() {}
	
	public static <T> List<T> loadFile(InputStream inputStream, Class<T> clazz)
			throws IOException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {

			return loadFile(bufferedReader, clazz);
		}
	}

	public static <T> List<T> loadFile(BufferedReader reader, Class<T> clazz)
			throws IOException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<T> list = new ArrayList<>();

		try (CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				Constructor<T> ctor = clazz.getConstructor(CSVRecord.class);
				T object = ctor.newInstance(new Object[] { csvRecord });
				list.add(object);
			}
		}

		return list;
	}
}
