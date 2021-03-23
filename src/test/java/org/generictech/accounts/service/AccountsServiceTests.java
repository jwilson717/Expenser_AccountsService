package org.generictech.accounts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.generictech.accounts.dto.SystemUserDTO;
import org.generictech.accounts.exception.AccountNotFoundException;
import org.generictech.accounts.exception.TypeNotFoundException;
import org.generictech.accounts.exception.UnauthorizedAccessException;
import org.generictech.accounts.model.AccountType;
import org.generictech.accounts.model.Accounts;
import org.generictech.accounts.repo.AccountTypeRepo;
import org.generictech.accounts.repo.AccountsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class to test the {@link AccountsService} class
 * @author Jaden Wilson
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class AccountsServiceTests {
	
	@Mock
	private AccountsRepo accountsRepo;
	
	@Mock
	private AccountTypeRepo accountTypeRepo;
	
	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private ObjectMapper om;
	
	@Mock
	private DiscoveryClient discoveryClient;
	
	@Mock
	private ServiceInstance service;
	
//	@Mock
//	private URI uri;
	
	@InjectMocks
	private AccountsService accountsService;
	
	private AccountType type;
	private Accounts account;
	private List<Accounts> accounts = new ArrayList<>();
	private SystemUserDTO user;
	private List<ServiceInstance> list = new ArrayList<>();
	
	@BeforeEach
	public void setUp() {
		user = new SystemUserDTO(1, "TestUser", "t@t.com");
		type = new AccountType(1, "Test Type");
		account = new Accounts(1, type, "Test Account", 100.00, 1);
		accounts.add(account);
		accounts.add(new Accounts(2, type, "Test Account 2", 2.00, 1));
		accounts.add(new Accounts(3, type, "Test Account 3", 3.00, 1));
		accounts.add(new Accounts(4, type, "Test Account 4", 4.00, 1));
		list.add(service);
	}
	
	/**
	 * Method to test the findAll method. 
	 */
	@Test
	void findAllTest() {
		doReturn(accounts).when(accountsRepo).findAll();
		
		List<Accounts> returnList = accountsService.findAll();
		
		assertEquals(accounts, returnList);
	}
	
	/**
	 * Method to test the findByUserId method.
	 * @throws AccountNotFoundException
	 */
	@Test 
	void findByUserIdTest() throws AccountNotFoundException {
		doReturn(Optional.of(accounts)).when(accountsRepo).findByUserId(user.getId());
		List<Accounts> resultList = accountsService.findByUserId(user.getId());
		
		assertEquals(accounts, resultList);
	}
	
	/**
	 * Method to test the proper error thrown when no account is found in the repo. 
	 */
	@Test 
	void findByUserIdAccountNotFoundTest() {
		doReturn(Optional.ofNullable(null)).when(accountsRepo).findByUserId(user.getId());
		
		assertThrows(AccountNotFoundException.class, () -> {
			accountsService.findByUserId(user.getId());
		});
		
	}
	
	/**
	 * Method to test the findById method. 
	 * @throws AccountNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	@Test
	void findByIdTest() throws AccountNotFoundException, UnauthorizedAccessException {
		doReturn(Optional.of(account)).when(accountsRepo).findById(1);
		Accounts returnAccount = accountsService.findById(1, user);
		
		assertEquals(account, returnAccount);
	}
	
	/**
	 * Method to test the return of an AccountNotFoundException if there is no account found. 
	 */
	@Test
	void findByIdTestAccountNotFoundTest() {
		doReturn(Optional.ofNullable(null)).when(accountsRepo).findById(5);
		assertThrows(AccountNotFoundException.class, () -> {
			accountsService.findById(5, user);
		});
	}
	
	/**
	 * Method to test the return of an UnauthorizedAccessException when a user is not authorized
	 * to access the account found. 
	 */
	@Test 
	void findByIdUnauthorizedAccessTest() {
		user.setId(3);
		doReturn(Optional.of(account)).when(accountsRepo).findById(1);
		assertThrows(UnauthorizedAccessException.class, () -> {
			accountsService.findById(1, user);
		});
	}
	
	/**
	 * Method to test the successful save method
	 * @throws TypeNotFoundException
	 */
	@Test
	void saveSuccessTest() throws TypeNotFoundException {
		doReturn(Optional.of(type)).when(accountTypeRepo).findById(1);
		doReturn(account).when(accountsRepo).save(any(Accounts.class));
		Accounts resultAccount = accountsService.save(account);
		
		assertEquals(account, resultAccount);
	}
	
	/**
	 * Method to test the proper return of a type not found exception when no 
	 * valid type is found for the provided id. 
	 * @throws TypeNotFoundException
	 */
	@Test
	void saveTypeNotFoundTest() throws TypeNotFoundException {
		account.getType().setId(6);
		doReturn(Optional.ofNullable(null)).when(accountTypeRepo).findById(6);
		
		assertThrows(TypeNotFoundException.class, () -> {
			accountsService.save(account);
		});
	}
	
	/**
	 * Method to test the successful alteration of an Account with the update method.
	 * @throws NoSuchElementException
	 * @throws AccountNotFoundException
	 * @throws TypeNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	@Test 
	void updateSuccessTest() throws NoSuchElementException, AccountNotFoundException, TypeNotFoundException, UnauthorizedAccessException {
		AccountType type2= new AccountType(2, "Test Type 2");
		Accounts accountData = new Accounts();
		accountData.setId(1);
		accountData.setType(type2);
		accountData.setDescription("Updated Description");
		accountData.setBalance(1001.00);
		
		doReturn(Optional.of(account)).when(accountsRepo).findById(1);
		doReturn(Optional.of(type2)).when(accountTypeRepo).findById(2);
		doReturn(accountData).when(accountsRepo).save(any(Accounts.class));
		
		Accounts resultAccount = accountsService.update(accountData, user);
		
		assertEquals(accountData, resultAccount);
		
	}
	
	/**
	 * Method to test that only provided values are changed. 
	 * @throws NoSuchElementException
	 * @throws AccountNotFoundException
	 * @throws TypeNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	@Test 
	void updateSuccessOnlySomeValuesTest() throws NoSuchElementException, AccountNotFoundException, TypeNotFoundException, UnauthorizedAccessException {
		Accounts accountData = new Accounts();
		accountData.setId(1);
		accountData.setDescription("Updated Description");
		
		doReturn(Optional.of(account)).when(accountsRepo).findById(1);
		doReturn(account).when(accountsRepo).save(any(Accounts.class));
		
		Accounts resultAccount = accountsService.update(accountData, user);
		assertEquals(account, resultAccount);
		assertEquals(accountData.getDescription(), resultAccount.getDescription());
		assertEquals(type, resultAccount.getType());
		assertEquals(100.00, resultAccount.getBalance());
		
	}
	
	/**
	 * Method to verify that unchangeable values (userId in this case) are unchanged even
	 * when incorrect values are provided. 
	 * @throws NoSuchElementException
	 * @throws AccountNotFoundException
	 * @throws TypeNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	@Test 
	void updateSuccessUnchangeableValuesNotChangedTest() throws NoSuchElementException, AccountNotFoundException, TypeNotFoundException, UnauthorizedAccessException {
		Accounts accountData = new Accounts();
		accountData.setId(1);
		accountData.setUserId(3);
		accountData.setDescription("Updated Description");
		
		doReturn(Optional.of(account)).when(accountsRepo).findById(1);
		doReturn(account).when(accountsRepo).save(any(Accounts.class));
		
		Accounts resultAccount = accountsService.update(accountData, user);
		assertEquals(account, resultAccount);
		assertEquals(accountData.getDescription(), resultAccount.getDescription());
		assertEquals(type, resultAccount.getType());
		assertEquals(100.00, resultAccount.getBalance());
		assertNotEquals(accountData.getUserId(), resultAccount.getUserId());
		
	}
	
	/**
	 * Method to test proper return of AccountNotFoundException if the desired account does 
	 * not exist. 
	 * @throws NoSuchElementException
	 * @throws AccountNotFoundException
	 * @throws TypeNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	@Test 
	void updateAccountNotFoundTest() throws NoSuchElementException, AccountNotFoundException, TypeNotFoundException, UnauthorizedAccessException {
		Accounts accountData = new Accounts();
		accountData.setId(5);
		doReturn(Optional.ofNullable(null)).when(accountsRepo).findById(5);
		assertThrows(AccountNotFoundException.class, () -> {
			accountsService.update(accountData, user);			
		});
	}
	
	/**
	 * Method to test the proper return of a TypeNotFoundException when the provided
	 * type does not exist. 
	 * @throws NoSuchElementException
	 * @throws AccountNotFoundException
	 * @throws TypeNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	@Test 
	void updateTypeNotFoundTest() throws NoSuchElementException, AccountNotFoundException, TypeNotFoundException, UnauthorizedAccessException {
		Accounts accountData = new Accounts();
		accountData.setId(1);
		accountData.setType(new AccountType());
		accountData.getType().setId(2);
		doReturn(Optional.of(account)).when(accountsRepo).findById(1);
		doReturn(Optional.ofNullable(null)).when(accountTypeRepo).findById(2);
		assertThrows(TypeNotFoundException.class, () -> {
			accountsService.update(accountData, user);			
		});
	}
	
	/**
	 * Method to test proper return of Unauthorized access exception when a user tries
	 * to access another users account. 
	 * @throws NoSuchElementException
	 * @throws AccountNotFoundException
	 * @throws TypeNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	@Test 
	void updateUnauthorizedAccessTest() throws NoSuchElementException, AccountNotFoundException, TypeNotFoundException, UnauthorizedAccessException {
		Accounts accountData = new Accounts();
		accountData.setId(1);
		accountData.setDescription("Updated Description");
		user.setId(3);
		doReturn(Optional.of(account)).when(accountsRepo).findById(1);
		assertThrows(UnauthorizedAccessException.class, () -> {
			accountsService.update(accountData, user);			
		});
	}	
	
	/**
	 * Method to test the successful delete method. 
	 * @throws AccountNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	@Test
	void deleteSuccessTest() throws AccountNotFoundException, UnauthorizedAccessException {
		doReturn(Optional.of(account)).when(accountsRepo).findById(1);
		doNothing().when(accountsRepo).delete(account);
		assertTrue(accountsService.delete(1, user));
	}
	
	/**
	 * Method to test the proper return of an AccountNotFoundException when the account
	 * requested does not exist. 
	 * @throws AccountNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	@Test
	void deleteAccountNotFoundTest() throws AccountNotFoundException, UnauthorizedAccessException {
		doReturn(Optional.ofNullable(null)).when(accountsRepo).findById(5);
		assertThrows(AccountNotFoundException.class, () -> {
			accountsService.delete(5, user);
		});
	}
	
	/**
	 * Method to test the proper return of an UnauthorizedAccessException when a user
	 * dries to delete another users account. 
	 * @throws AccountNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	@Test
	void deleteUnauthorizedAccessTest() throws AccountNotFoundException, UnauthorizedAccessException {
		doReturn(Optional.of(account)).when(accountsRepo).findById(1);
		user.setId(3);
		assertThrows(UnauthorizedAccessException.class, () -> {
			accountsService.delete(1, user);
		});
	}
	
	// unable to get following tests to work consistently due to MockMaker extension
	// and final classes. 
	
//	/**
//	 * Method to test successful getUser
//	 */
//	@SuppressWarnings("unchecked")
//	@Test
//	public void getUserSuccessTest() {
//		doReturn(list).when(discoveryClient).getInstances(anyString());
//		doReturn(uri).when(list.get(0)).getUri();
//		doReturn("testUri").when(uri).toString();
//		doReturn(user).when(restTemplate).postForObject(anyString(), any(HttpEntity.class), (Class<SystemUserDTO>) any());
//		
//		SystemUserDTO resultUser = null;
//		try {
//			resultUser = accountsService.getUser("testToken");
//			
//		} catch (Exception e) {
//			fail();
//		}
//		
//		assertEquals(user, resultUser);
//	}
//	
//	/**
//	 * Method to test the proper return of a UserNotFoundException when no user
//	 * is returned from the user-auth-service. 
//	 */
//	@SuppressWarnings("unchecked")
//	@Test
//	public void getUserNotFoundTest() {
//		doReturn(list).when(discoveryClient).getInstances(anyString());
//		doReturn(uri).when(list.get(0)).getUri();
//		doReturn("testUri").when(uri).toString();
//		doThrow(UserNotFoundException.class).when(restTemplate).postForObject(anyString(), any(HttpEntity.class), (Class<SystemUserDTO>) any());
//		
//		assertThrows(UserNotFoundException.class, ()-> {
//			accountsService.getUser("testToken");
//		});
//	}
	
}