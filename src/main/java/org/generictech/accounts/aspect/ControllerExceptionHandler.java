package org.generictech.accounts.aspect;

import java.util.Date;
import java.util.NoSuchElementException;

import org.generictech.accounts.exception.AccountNotFoundException;
import org.generictech.accounts.exception.BadValueException;
import org.generictech.accounts.exception.ExceptionResponse;
import org.generictech.accounts.exception.ProcessingException;
import org.generictech.accounts.exception.ProcessingRuntimeException;
import org.generictech.accounts.exception.TypeNotFoundException;
import org.generictech.accounts.exception.UnauthorizedAccessException;
import org.generictech.accounts.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Class to handle exceptions in the controller methods.
 * @author Jaden Wilson
 * @since 1.0
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

	/**
	 * Method to handle Not found exceptions including AccountNotFoundException, TypeNotFoundException, and NoSuchElementException
	 * @param web WebRequest
	 * @param e Exception
	 * @return ResponseEntity<ExceptionResponse>
	 */
	@ExceptionHandler({AccountNotFoundException.class, TypeNotFoundException.class
		, NoSuchElementException.class, UserNotFoundException.class})
	public ResponseEntity<ExceptionResponse> notFoundExceptions(WebRequest web, Exception e) {
		log.error(e.getMessage());
		return new ResponseEntity<>(new ExceptionResponse(new Date(), 404, e.getClass().getSimpleName()
				, e.getMessage()), HttpStatus.NOT_FOUND);
	}
	
	/**
	 * Method to handle Processing exceptions.
	 * @param web WebRequest
	 * @param e Exception
	 * @return ResponseEntity<ExceptionResponse>
	 */
	@ExceptionHandler({ProcessingException.class, ProcessingRuntimeException.class})
	public ResponseEntity<ExceptionResponse> processingExceptions(WebRequest web, Exception e) {
		log.error(e.getMessage());
		return new ResponseEntity<>(new ExceptionResponse(new Date(), 500, e.getClass().getSimpleName()
				, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * Method to handle Unauthorized Access
	 * @param web WebRequest
	 * @param e Exception
	 * @return ResponseEntity<ExceptionResponse>
	 */
	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<ExceptionResponse> unauthorizedAccessExceptions(WebRequest web, Exception e) {
		log.error(e.getMessage());
		return new ResponseEntity<>(new ExceptionResponse(new Date(), 401, e.getClass().getSimpleName()
				, e.getMessage()), HttpStatus.UNAUTHORIZED);
	}
	
	/**
	 * Method to handle badValue exceptions
	 * @param web
	 * @param e
	 * @return ResponseEntity<ExceptionResponse>
	 */
	@ExceptionHandler(BadValueException.class)
	public ResponseEntity<ExceptionResponse> badValueExceptions(WebRequest web, Exception e) {
		log.error(e.getMessage());
		return new ResponseEntity<>(new ExceptionResponse(new Date(), 400, e.getClass().getSimpleName()
				, e.getMessage()), HttpStatus.BAD_REQUEST);
	}
}
