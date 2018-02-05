package com.ominiyi.utilities;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHelper {

	private LogHelper() {}
	
	public static <T> void log(Class<T> clazz, Level logLevel, String message){
		Logger logger = Logger.getLogger(clazz.getName());
		logger.log(logLevel, message);
	}
	
	public static <T> void log(Class<T> clazz, Level logLevel, Exception exception){
		Logger logger = Logger.getLogger(clazz.getName());
		logger.log(logLevel, exception.toString(), exception);
	}
}
