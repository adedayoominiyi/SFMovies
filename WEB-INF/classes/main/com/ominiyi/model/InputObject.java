package com.ominiyi.model;

/**
 * The InputObject class is an abstract class that is inherited by other model classes.
 *
 * @author  Adedayo Ominiyi
 */
public abstract class InputObject {

	protected InputObject() {
		super();
	}
	
	protected String cleanInput(String input) {
		if (input == null) {
			return null;
		}
		
		return input.trim();
	}
	
	protected Integer toInteger(String input) {
		String cleanedInput = cleanInput(input);
		return (cleanedInput != null && !cleanedInput.isEmpty() ? Integer.parseInt(cleanedInput) : null);
	}
	
	protected Double toDouble(String input) {
		String cleanedInput = cleanInput(input);
		return (cleanedInput != null && !cleanedInput.isEmpty() ? Double.parseDouble(cleanedInput) : null);
	}
}
