package org.generictech.accounts.exception;

/**
 * Exception to be thrown when a spcified type ID is not found in the database. 
 * @author Jaden Wilson
 *
 */
@SuppressWarnings("serial")
public class TypeNotFoundException extends Exception {
	
	public TypeNotFoundException() {
		super("Type not found");
	}

	public TypeNotFoundException(String message) {
		super(message);
	}	
}
