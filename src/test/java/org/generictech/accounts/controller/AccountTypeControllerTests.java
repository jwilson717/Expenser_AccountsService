package org.generictech.accounts.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.generictech.accounts.exception.BadValueException;
import org.generictech.accounts.exception.TypeNotFoundException;
import org.generictech.accounts.model.AccountType;
import org.generictech.accounts.service.AccountTypeService;
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
 * Test class for testing the AccountTypeController
 * @author Jaden Wilson
 * @since 1.0
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AccountTypeController.class)
class AccountTypeControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper om;
	
	@MockBean
	private AccountTypeService accountTypeService;
	
	private AccountType type;
	private List<AccountType> types = new ArrayList<>();
	
	@BeforeEach
	public void setUp() {
		type = new AccountType(1, "Checking");
		types.add(type);
		types.add(new AccountType(2, "Savings"));
		types.add(new AccountType(3, "Test Type"));
	}
	
	/**
	 * Method to test a successful GET request to get all account types. 
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void getAllSuccessTest() throws JsonProcessingException, Exception {
		doReturn(types).when(accountTypeService).findAll();
		mockMvc.perform(get("/account/type"))
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.content().string(om.writeValueAsString(types)));
	}
	
	/**
	 * Method to test a successful get request to get by id.
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test 
	void getByIdSuccessTest() throws JsonProcessingException, Exception {
		doReturn(type).when(accountTypeService).findById(1);
		mockMvc.perform(get("/account/type/id/1"))
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.content().string(om.writeValueAsString(type)));
	}
	
	/**
	 * Method to test a failed get request to get a type that does not exist. Verifies
	 * the return of a TypeNotFoundException.
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test 
	void getByIdNotFoundTest() throws JsonProcessingException, Exception {
		doThrow(TypeNotFoundException.class).when(accountTypeService).findById(4);
		mockMvc.perform(get("/account/type/id/4"))
			.andExpect(status().isNotFound())
			.andExpect(MockMvcResultMatchers.content().string(containsString("TypeNotFoundException")));
	}
	
	/**
	 * Method to test a successful post request. 
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void postAccountTypeSuccessTest() throws JsonProcessingException, Exception {
		doReturn(type).when(accountTypeService).save(any(AccountType.class));
		mockMvc.perform(post("/account/type").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(type))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(MockMvcResultMatchers.content().string(om.writeValueAsString(type)));
	}
	
	/**
	 * Method to test a failed post with no body content
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void postAccountTypeNoDataTest() throws JsonProcessingException, Exception {
		doReturn(type).when(accountTypeService).save(any(AccountType.class));
		mockMvc.perform(post("/account/type"))
			.andExpect(status().isBadRequest());
	}
	
	/**
	 * Method to test proper return for a badValueException.
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void postAccountEmptyTypeTest() throws JsonProcessingException, Exception {
		AccountType newType = new AccountType();
		newType.setType("");
		doThrow(BadValueException.class).when(accountTypeService).save(any(AccountType.class));
		mockMvc.perform(post("/account/type").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(newType))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(MockMvcResultMatchers.content().string(containsString("BadValueException")));
	}
	
	/**
	 * Method to test a successful put request.
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void putAccountTypeSuccessTest() throws JsonProcessingException, Exception {
		type.setType("Updated Type");
		doReturn(type).when(accountTypeService).update(any(AccountType.class));
		mockMvc.perform(put("/account/type/1").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(type))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.content().string(om.writeValueAsString(type)));
	}
	
	/**
	 * Method to test a failed put request where the type does not exist.
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	@Test
	void putAccountTypeNotFoundTest() throws JsonProcessingException, Exception {
		type.setType("Updated Type");
		doThrow(TypeNotFoundException.class).when(accountTypeService).update(any(AccountType.class));
		mockMvc.perform(put("/account/type/4").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(type))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(MockMvcResultMatchers.content().string(containsString("TypeNotFoundException")));
	}
	
	/**
	 * Method to test a successful delete request.
	 * @throws Exception
	 */
	@Test
	void deleteAccountTypeSuccessTest() throws Exception {
		doReturn(true).when(accountTypeService).delete(1);
		mockMvc.perform(delete("/account/type/1"))
			.andExpect(status().isNoContent());
	}
	
	/**
	 * Method to test a failed delete request
	 * @throws Exception
	 */
	@Test
	void deleteAccountTypeNotFoundTest() throws Exception {
		doThrow(TypeNotFoundException.class).when(accountTypeService).delete(4);
		mockMvc.perform(delete("/account/type/4"))
			.andExpect(status().isNotFound())
			.andExpect(MockMvcResultMatchers.content().string(containsString("TypeNotFoundException")));
	}
	
}
