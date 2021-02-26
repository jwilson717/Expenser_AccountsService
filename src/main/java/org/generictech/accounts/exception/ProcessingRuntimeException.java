package org.generictech.accounts.exception;
/**
 * Exception Class for runtime exceptions with processing 
 * @author Jaden Wilson
 * @since 1.0
 */
@SuppressWarnings("serial")
public class ProcessingRuntimeException extends RuntimeException {

	public ProcessingRuntimeException() {
		super();
	}

	public ProcessingRuntimeException(String message) {
		super(message);
	}

	
}
