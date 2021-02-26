package org.generictech.accounts.exception;
/**
 * Exception class to handle attempts to access other users accounts
 * @author Jaden Wilson
 * @since 1.0
 */
@SuppressWarnings("serial")
public class UnauthorizedAccessException extends Exception {

	public UnauthorizedAccessException() {
		super("Unauthorized");
	}

	public UnauthorizedAccessException(String message) {
		super(message);
	}

}
