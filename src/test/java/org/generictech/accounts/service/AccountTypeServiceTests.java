package org.generictech.accounts.service;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.generictech.accounts.exception.BadValueException;
import org.generictech.accounts.exception.ProcessingException;
import org.generictech.accounts.exception.TypeNotFoundException;
import org.generictech.accounts.model.AccountType;
import org.generictech.accounts.repo.AccountTypeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


/**
 * Test class to test the {@link AccountTypeService} class
 * @author Jaden Wilson
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class AccountTypeServiceTests {

	@Mock
	private AccountTypeRepo accountTypeRepo;
	
	@InjectMocks
	private AccountTypeService accountTypeService;
	
	private AccountType type;
	private List<AccountType> types = new ArrayList<>();
	
	@BeforeEach
	public void setUp() {
		type = new AccountType(1, "Test Type");
		types.add(type);
		types.add(new AccountType(2, "Test Type 2"));
		types.add(new AccountType(3, "Test Type 3"));
	}
	
	/**
	 * Method to test the proper return of the findAll method. 
	 */
	@Test
	void findAllSuccessTest() {
		doReturn(types).when(accountTypeRepo).findAll();
		
		assertEquals(types, accountTypeService.findAll());
	}
	
	/**
	 * Method to test the proper return of the findById method. 
	 * @throws TypeNotFoundException
	 */
	@Test 
	void findByIdSuccessTest() throws TypeNotFoundException {
		doReturn(Optional.of(type)).when(accountTypeRepo).findById(1);
		
		assertEquals(type, accountTypeService.findById(1));
	}
	
	/**
	 * Method to test the return of a TypeNotFoundException when no type is returned
	 * from the repo. 
	 * @throws TypeNotFoundException
	 */
	@Test 
	void findByIdNotFoundTest() throws TypeNotFoundException {
		doReturn(Optional.ofNullable(null)).when(accountTypeRepo).findById(4);
		
		assertThrows(TypeNotFoundException.class, ()->{
			accountTypeService.findById(4);
		});
	}
	
	/**
	 * Method to test proper return of save method. 
	 * @throws NoSuchElementException 
	 * @throws BadValueException 
	 */
	@Test 
	void saveSuccessTest() throws NoSuchElementException, BadValueException {
		doReturn(type).when(accountTypeRepo).save(any(AccountType.class));
		
		assertEquals(type, accountTypeService.save(type));
	}
	
	/**
	 * Method to test the proper return if no type content is provided. 
	 * @throws ProcessingException 
	 * @throws NoSuchElementException 
	 */
	@Test 
	void saveFailedTest() throws NoSuchElementException {
		assertThrows(BadValueException.class, ()->{
			accountTypeService.save(new AccountType());
		});
	}
	
	/**
	 * Method to test proper return of ProcessingException if empty string is provided. 
	 * @throws NoSuchElementException
	 * @throws ProcessingException
	 */
	@Test 
	void saveEmptyFailedTest() throws NoSuchElementException {
		AccountType newType = new AccountType();
		newType.setType("");
		assertThrows(BadValueException.class, ()->{
			accountTypeService.save(newType);
		});
	}
	
	/**
	 * Method to test the proper return of the update method. 
	 * @throws TypeNotFoundException
	 */
	@Test
	void updateSuccessTest() throws TypeNotFoundException {
		AccountType typeData = new AccountType();
		typeData.setId(1);
		typeData.setType("Updated Test Type");
		doReturn(Optional.of(type)).when(accountTypeRepo).findById(1);
		doReturn(type).when(accountTypeRepo).save(type);
		AccountType resultType = accountTypeService.update(typeData);
		assertEquals(typeData.getId(), resultType.getId());
		assertEquals(typeData.getType(), resultType.getType());
	}
	
	/**
	 * Method to test proper return of TypeNotFoundException when the requested
	 * type to update  is not found in the repo. 
	 */
	@Test
	void updateNotFoundTest() {
		AccountType typeData = new AccountType();
		typeData.setId(4);
		typeData.setType("Updated Test Type");
		doReturn(Optional.ofNullable(null)).when(accountTypeRepo).findById(4);
		assertThrows(TypeNotFoundException.class, ()->{
			accountTypeService.update(typeData);
		});
	}
	
	/**
	 * Method to test the proper return of the delete method. 
	 * @throws NoSuchElementException
	 * @throws TypeNotFoundException
	 */
	@Test
	void deleteSuccessTest() throws NoSuchElementException, TypeNotFoundException {
		doReturn(Optional.of(type)).when(accountTypeRepo).findById(1);
		doNothing().when(accountTypeRepo).delete(type);
		assertTrue(accountTypeService.delete(1));
	}
	
	/**
	 * method to test the proper return of the delete method when the requested 
	 * type does not exist in the repo. 
	 */
	@Test 
	void deleteNotFoundTest() {
		doReturn(Optional.ofNullable(null)).when(accountTypeRepo).findById(4);
		assertThrows(TypeNotFoundException.class, ()->{
			accountTypeService.delete(4);
		});
	}
	
}
