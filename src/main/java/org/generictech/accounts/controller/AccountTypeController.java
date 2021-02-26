package org.generictech.accounts.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.generictech.accounts.exception.TypeNotFoundException;
import org.generictech.accounts.model.AccountType;
import org.generictech.accounts.service.AccountTypeService;
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
 * Class to handle incoming HTTP requests pertaining to account_types
 * @author Jaden Wilson
 * @since 1.0
 *
 */
@RestController
@RequestMapping("/account/type")
public class AccountTypeController {

	@Autowired
	private AccountTypeService accountTypeService;
	
	/**
	 * Method to handle GET requests to /account/type. This method returns all types in the database. 
	 * @return ResponseEntity<List<AccountType>>
	 */
	@GetMapping("")
	public ResponseEntity<List<AccountType>> findAll() {
		return new ResponseEntity<List<AccountType>>(accountTypeService.findAll(), HttpStatus.OK);
	}
	
	/**
	 * Method to handle GET requests to /account/type/id/{id}. This method will return the account record with the specified ID value. 
	 * @param id
	 * @return
	 * @throws TypeNotFoundException
	 */
	@GetMapping("/id/{id}")
	public ResponseEntity<AccountType> findById(@PathVariable int id) throws TypeNotFoundException {
		return new ResponseEntity<AccountType>(accountTypeService.findById(id), HttpStatus.OK);
	}
	
	/**
	 * Method to handle POST requests to /account/type. This method will add a new type record to the database. 
	 * @param type AccountType
	 * @return ResponseEntity<AccountType>
	 */
	@PostMapping("")
	public ResponseEntity<AccountType> save(@RequestBody AccountType type) {
		return new ResponseEntity<AccountType>(accountTypeService.save(type), HttpStatus.CREATED);
	}
	
	/**
	 * Method to handle PUT requests to /account/type/{id}. This method will update a type record in the database. 
	 * @param id
	 * @param type
	 * @return ResponseEntity<AccountType>
	 * @throws TypeNotFoundException
	 */
	@PutMapping("/{id}")
	public ResponseEntity<AccountType> update(@PathVariable int id, @RequestBody AccountType type) throws TypeNotFoundException {
		type.setId(id);
		return new ResponseEntity<AccountType>(accountTypeService.update(type), HttpStatus.OK);
	}
	
	/**
	 * Method to handle DELETE requests to /account/type/{id}. This method will delete a type record with the specified ID value.
	 * @param id
	 * @return ResponseEntity<Object>
	 * @throws NoSuchElementException
	 * @throws TypeNotFoundException
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable int id) throws NoSuchElementException, TypeNotFoundException {
		if (accountTypeService.delete(id)) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
	
}
