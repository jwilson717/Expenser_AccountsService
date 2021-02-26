package org.generictech.accounts.exception;
/**
 * Exception class to handle exceptions that occur during processing.
 * @author Jaden Wilson
 * @since 1.0
 */
@SuppressWarnings("serial")
public class ProcessingException extends Exception {

	public ProcessingException() {
		super();
	}

	public ProcessingException(String message) {
		super(message);
	}

}
