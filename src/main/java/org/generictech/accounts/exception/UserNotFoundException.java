package org.generictech.accounts.exception;
/**
 * Exception class to handle user not found senarios
 * @author Jaden Wilson
 * @since 1.0
 */
@SuppressWarnings("serial")
public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException() {
		super("User not found");
	}

	public UserNotFoundException(String message) {
		super(message);
	}

}
