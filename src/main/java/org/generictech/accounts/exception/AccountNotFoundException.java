package org.generictech.accounts.exception;

/**
 * Exception for when no Account is found based upon any criteria. 
 * @author Jaden Wilson
 * @since 1.0
 */
@SuppressWarnings("serial")
public class AccountNotFoundException extends Exception {

	public AccountNotFoundException() {
		super("Account not found.");
	}

	public AccountNotFoundException(String message) {
		super(message);
	}

	
}
