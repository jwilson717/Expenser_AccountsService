package org.generictech.accounts.controller;

import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.generictech.accounts.dto.SystemUserDTO;
import org.generictech.accounts.exception.AccountNotFoundException;
import org.generictech.accounts.exception.ProcessingException;
import org.generictech.accounts.exception.ProcessingRuntimeException;
import org.generictech.accounts.exception.TypeNotFoundException;
import org.generictech.accounts.exception.UnauthorizedAccessException;
import org.generictech.accounts.exception.UserNotFoundException;
import org.generictech.accounts.model.Accounts;
import org.generictech.accounts.service.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class to handle incoming HTTP requests dealing with accounts
 * @author Jaden Wilson
 * @since 1.0
 *
 */
@RestController
@RequestMapping("/account")
public class AccountsController {

	@Autowired
	private AccountsService accountsService;
	@Autowired
	private HttpServletRequest req;
	
	/**
	 * Method to handle GET requests to /account endpoint.
	 * @return List<Accounts> 
	 * @throws ProcessingException 
	 * @throws UserNotFoundException 
	 * @throws ProcessingRuntimeException 
	 * @throws AccountNotFoundException 
	 */
	@GetMapping("")
	public ResponseEntity<List<Accounts>> getAll() throws ProcessingRuntimeException, UserNotFoundException, ProcessingException, AccountNotFoundException {
		SystemUserDTO user = accountsService.getUser(req.getHeader("tokenId"));
		return new ResponseEntity<List<Accounts>>(accountsService.findByUserId(user.getId()), HttpStatus.OK);
	}
	
	/**
	 * Method to handle GET requests to /account/id/{id} 
	 * @param id
	 * @return Accounts object
	 * @throws AccountNotFoundException 
	 * @throws ProcessingException 
	 * @throws UserNotFoundException 
	 * @throws ProcessingRuntimeException 
	 * @throws UnauthorizedAccessException 
	 */
	@GetMapping("/id/{id}")
	public ResponseEntity<Accounts> getById(@PathVariable int id) throws AccountNotFoundException, ProcessingRuntimeException, UserNotFoundException, ProcessingException, UnauthorizedAccessException {
		SystemUserDTO user = accountsService.getUser(req.getHeader("tokenId"));
		return new ResponseEntity<Accounts>(accountsService.findById(id, user), HttpStatus.OK);			
	}
	
	/**
	 * Method to handle GET request to /account/user/{userid}
	 * @param userId
	 * @return ResponseEntity<List<Accounts>>
	 * @throws AccountNotFoundException 
	 */
//	This method is commented out because the same functionality is accomplished with the no PathVariable GET request above 
//	@GetMapping("/user/{userId}")
//	public ResponseEntity<List<Accounts>> getByuserId(@PathVariable int userId) throws AccountNotFoundException {
//		return new ResponseEntity<List<Accounts>>(accountsService.findByUserId(userId), HttpStatus.OK);			
//	}
	
	/**
	 * Method to handle POST request to /account
	 * @param account
	 * @return ResponseEntity<Accounts>
	 * @throws TypeNotFoundException 
	 * @throws ProcessingException 
	 * @throws UserNotFoundException 
	 * @throws ProcessingRuntimeException 
	 */
	@PostMapping("")
	public ResponseEntity<Accounts> save(@RequestBody Accounts account) throws TypeNotFoundException, ProcessingRuntimeException
		, UserNotFoundException, ProcessingException {
		SystemUserDTO user = accountsService.getUser(req.getHeader("tokenId"));
		account.setUserId(user.getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(accountsService.save(account));			
	}
	
	/**
	 * Method to handle PUT requests to /account/{id}
	 * @param id
	 * @param accountData
	 * @return ResponseEntity<Accounts>
	 * @throws AccountNotFoundException 
	 * @throws NoSuchElementException 
	 * @throws TypeNotFoundException 
	 * @throws ProcessingException 
	 * @throws UserNotFoundException 
	 * @throws ProcessingRuntimeException 
	 * @throws UnauthorizedAccessException 
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Accounts> update(@PathVariable int id, @RequestBody Accounts accountData) throws NoSuchElementException, AccountNotFoundException
		, TypeNotFoundException, ProcessingRuntimeException, UserNotFoundException, ProcessingException, UnauthorizedAccessException {
		accountData.setId(id);
		SystemUserDTO user = accountsService.getUser(req.getHeader("tokenId"));
		return new ResponseEntity<Accounts>(accountsService.update(accountData, user), HttpStatus.OK);			
	}
	
	/**
	 * Method to handle DELETE requests to /account/{id}
	 * @param id
	 * @return ResponseEntity<Object>
	 * @throws AccountNotFoundException 
	 * @throws ProcessingException 
	 * @throws UserNotFoundException 
	 * @throws ProcessingRuntimeException 
	 * @throws UnauthorizedAccessException 
	 */
	@DeleteMapping("/{id}") 
	public ResponseEntity<Object> delete(@PathVariable int id) throws AccountNotFoundException, ProcessingRuntimeException, UserNotFoundException
		, ProcessingException, UnauthorizedAccessException {
		SystemUserDTO user = accountsService.getUser(req.getHeader("tokenId"));
		accountsService.delete(id, user);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}
}
