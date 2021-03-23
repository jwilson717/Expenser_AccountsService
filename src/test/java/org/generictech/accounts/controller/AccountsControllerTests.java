package org.generictech.accounts.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.generictech.accounts.dto.SystemUserDTO;
import org.generictech.accounts.exception.AccountNotFoundException;
import org.generictech.accounts.exception.TypeNotFoundException;
import org.generictech.accounts.exception.UnauthorizedAccessException;
import org.generictech.accounts.exception.UserNotFoundException;
import org.generictech.accounts.model.AccountType;
import org.generictech.accounts.model.Accounts;
import org.generictech.accounts.service.AccountsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class to test the AccountsController 
 * @author Jaden Wilson
 * @since 1.0
 *
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AccountsController.class)
class AccountsControllerTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper om;
	@MockBean
	private AccountsService accountsService;
	
	private SystemUserDTO user;
	private Accounts account;
	private List<Accounts> accounts = new ArrayList<>();
	private AccountType type;
	
	@BeforeEach
	public void setUp() {
		type = new AccountType(1, "Test");
		user = new SystemUserDTO(1, "TTester", "test@test.com");
		account = new Accounts(1, type, "Test Account", 100.00, 1);
		accounts.add(account);
		accounts.add(new Accounts(2, type, "Test Account 2", 100.00, 1));
		accounts.add(new Accounts(3, type, "Test Account 3", 100.00, 1));
		accounts.add(new Accounts(4, type, "Test Account 4", 100.00, 1));
	}
	
	/**
	 * Method to test a successful GET request to /account
	 * @throws Exception
	 */
	@Test
	void getAccountsSuccessTest() throws Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		doReturn(accounts).when(accountsService).findByUserId(user.getId());
		mockMvc.perform(get("/account")
				.header("tokenId", "TestToken"))
			.andExpect(status().is(200))
			.andExpect(MockMvcResultMatchers.content().string(om.writeValueAsString(accounts)));
	}
	
	/**
	 * Method to test a GET request with a user that does not exist
	 * @throws Exception
	 */
	@Test
	void getAccountsNoUserTest() throws Exception {
		doThrow(UserNotFoundException.class).when(accountsService).getUser(anyString());
		mockMvc.perform(get("/account")
				.header("tokenId", "TestToken"))
			.andExpect(status().is(404))
			.andExpect(MockMvcResultMatchers.content().string(containsString("UserNotFoundException")));
	}
	
	/**
	 * Method to test a GET request when no accounts exist.
	 * @throws Exception
	 */
	@Test
	void getAccountsNoAccountTest() throws Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		doThrow(AccountNotFoundException.class).when(accountsService).findByUserId(user.getId());
		mockMvc.perform(get("/account")
				.header("tokenId", "TestToken"))
			.andExpect(status().is(404))
			.andExpect(MockMvcResultMatchers.content().string(containsString("AccountNotFoundException")));
	}
	
	/**
	 * Method to test a GET request to /account/id/{id}
	 * @throws Exception
	 */
	@Test
	void getAccountSuccessTest() throws Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		doReturn(account).when(accountsService).findById(1, user);
		mockMvc.perform(get("/account/id/1")
				.header("tokenId", "TestToken"))
			.andExpect(status().is(200))
			.andExpect(MockMvcResultMatchers.content().string(om.writeValueAsString(account)));
	}
	
	/**
	 * Method to test a GET request to /account/id/{id} where user does not exist
	 * @throws Exception
	 */
	@Test
	void getAccountNoUserTest() throws Exception {
		doThrow(UserNotFoundException.class).when(accountsService).getUser(anyString());
		mockMvc.perform(get("/account/id/1")
				.header("tokenId", "TestToken"))
			.andExpect(status().is(404))
			.andExpect(MockMvcResultMatchers.content().string(containsString("UserNotFoundException")));
	}
	
	/**
	 * Method to test a GET request to /account/id/{id} where the account does not exist
	 * @throws Exception
	 */
	@Test
	void getAccountNoAccountTest() throws Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		doThrow(AccountNotFoundException.class).when(accountsService).findById(6, user);
		mockMvc.perform(get("/account/id/6")
				.header("tokenId", "TestToken"))
			.andExpect(status().is(404))
			.andExpect(MockMvcResultMatchers.content().string(containsString("AccountNotFoundException")));
	}
	
	/**
	 * Method to test a GET request to /account/id/{id} when a user tried to access another users account
	 * @throws Exception
	 */
	@Test
	void getAccountBadAccessTest() throws Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		doThrow(UnauthorizedAccessException.class).when(accountsService).findById(5, user);
		mockMvc.perform(get("/account/id/5")
				.header("tokenId", "TestToken"))
			.andExpect(status().is(401));
	}
	
	/**
	 * Method to test a POST request to /account that results in a successful POST. 
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void postAccountSuccessTest() throws JsonProcessingException, Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		doReturn(account).when(accountsService).save(any(Accounts.class));
		mockMvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(account))
				.accept(MediaType.APPLICATION_JSON)
				.header("tokenId", "testToken"))
			.andExpect(status().isCreated())
			.andExpect(MockMvcResultMatchers.content().string(om.writeValueAsString(account)));
	}
	
	/**
	 * Method to test a POST request to /account that results in a Type not found exception.
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void postAccountNoTypeFoundExceptionTest() throws JsonProcessingException, Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		doThrow(TypeNotFoundException.class).when(accountsService).save(any(Accounts.class));
		mockMvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(account))
				.accept(MediaType.APPLICATION_JSON)
				.header("tokenId", "testToken"))
			.andExpect(status().isNotFound())
			.andExpect(MockMvcResultMatchers.content().string(containsString("TypeNotFoundException")));
			
	}
	
	/**
	 * Method to test a POST request to /account that results in a UserNotFoundException
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void postAccountUserNotFoundExceptionTest() throws JsonProcessingException, Exception {
		doThrow(UserNotFoundException.class).when(accountsService).getUser(anyString());
		mockMvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(account))
				.accept(MediaType.APPLICATION_JSON)
				.header("tokenId", "testToken"))
			.andExpect(status().isNotFound())
			.andExpect(MockMvcResultMatchers.content().string(containsString("UserNotFoundException")));
			
	}
	
	/**
	 * Method to test a successful PUT request to /account/{id}
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void putAccountSuccessTest() throws JsonProcessingException, Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		Accounts altered = new Accounts(account.getId(), account.getType(), "Updated description", 102.00, account.getUserId());
		doReturn(altered).when(accountsService).update(any(Accounts.class), any(SystemUserDTO.class));
		mockMvc.perform(put("/account/1").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(altered))
				.accept(MediaType.APPLICATION_JSON)
				.header("tokenId", "testToken"))
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.content().string(containsString("Updated description")));
	}
	
	/**
	 * Method to test an unsuccessful PUT request to /account/{id} in which the provided id does not
	 * match an account in the DB. 
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void putAccountNotFoundExceptionTest() throws JsonProcessingException, Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		Accounts altered = new Accounts(account.getId(), account.getType(), "Updated description", 100.00, account.getUserId());
		doThrow(AccountNotFoundException.class).when(accountsService).update(any(Accounts.class), any(SystemUserDTO.class));
		mockMvc.perform(put("/account/7").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(altered))
				.accept(MediaType.APPLICATION_JSON)
				.header("tokenId", "testToken"))
			.andExpect(status().isNotFound())
			.andExpect(MockMvcResultMatchers.content().string(containsString("AccountNotFoundException")));
	}
	
	/**
	 * Method to test an unsuccessful request to /account/{id} in which the altered type
	 * does not exist in the database. 
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void putAccountTypeNotFoundExceptionTest() throws JsonProcessingException, Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		Accounts altered = new Accounts(account.getId(), new AccountType(), "Updated description", 1000.00, account.getUserId());
		doThrow(TypeNotFoundException.class).when(accountsService).update(any(Accounts.class), any(SystemUserDTO.class));
		mockMvc.perform(put("/account/1").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(altered))
				.accept(MediaType.APPLICATION_JSON)
				.header("tokenId", "testToken"))
			.andExpect(status().isNotFound())
			.andExpect(MockMvcResultMatchers.content().string(containsString("TypeNotFoundException")));
	}
	
	/**
	 * Method to test proper return of PUT request to /account/{id} in which the user trying 
	 * to access a specific account does not have the permissions to access said account. 
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void putAccountUnauthorizedExceptionTest() throws JsonProcessingException, Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		Accounts altered = new Accounts(account.getId(), new AccountType(), "Updated description", 100.00, account.getUserId());
		doThrow(UnauthorizedAccessException.class).when(accountsService).update(any(Accounts.class), any(SystemUserDTO.class));
		mockMvc.perform(put("/account/6").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(altered))
				.accept(MediaType.APPLICATION_JSON)
				.header("tokenId", "testToken"))
			.andExpect(status().isUnauthorized())
			.andExpect(MockMvcResultMatchers.content().string(containsString("UnauthorizedAccessException")));
	}
	
	/** 
	 * Method to test a successful delete request to /account/{id}
	 * @throws Exception
	 */
	@Test
	void deleteAccountSuccessTest() throws Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		doReturn(true).when(accountsService).delete(1, user);
		mockMvc.perform(delete("/account/1").header("tokenId", "testToken"))
			.andExpect(status().isNoContent());
	}
	
	/**
	 * Method to test an unsuccessful DELETE request in which the account requested does not exist.
	 * @throws Exception
	 */
	@Test
	void deleteAccountNotFoundTest() throws Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		doThrow(AccountNotFoundException.class).when(accountsService).delete(7, user);
		mockMvc.perform(delete("/account/7").header("tokenId", "testToken"))
			.andExpect(status().isNotFound())
			.andExpect(MockMvcResultMatchers.content().string(containsString("AccountNotFoundException")));
	}
	
	/**
	 * Method to test the proper response to an unsuccessful DELETE request in which the user trying to 
	 * delete an account does not have access to that account. 
	 * @throws Exception
	 */
	@Test
	void deleteAccountUnauthorizedAccessTest() throws Exception {
		doReturn(user).when(accountsService).getUser(anyString());
		doThrow(UnauthorizedAccessException.class).when(accountsService).delete(6, user);
		mockMvc.perform(delete("/account/6").header("tokenId", "testToken"))
			.andExpect(status().isUnauthorized())
			.andExpect(MockMvcResultMatchers.content().string(containsString("UnauthorizedAccessException")));
	}
}
