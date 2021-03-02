package org.generictech.accounts.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.generictech.accounts.dto.SystemUserDTO;
import org.generictech.accounts.exception.AccountNotFoundException;
import org.generictech.accounts.exception.ProcessingException;
import org.generictech.accounts.exception.ProcessingRuntimeException;
import org.generictech.accounts.exception.TypeNotFoundException;
import org.generictech.accounts.exception.UnauthorizedAccessException;
import org.generictech.accounts.exception.UserNotFoundException;
import org.generictech.accounts.model.AccountType;
import org.generictech.accounts.model.Accounts;
import org.generictech.accounts.repo.AccountTypeRepo;
import org.generictech.accounts.repo.AccountsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * This class contains methods and business logic pertaining to the accounts table of the database. 
 * @author Jaden Wilson
 * @since 1.0
 *
 */
@Service
@Slf4j
public class AccountsService {
	
	@Autowired
	private AccountsRepo accountsRepo;
	@Autowired
	private AccountTypeRepo accountTypeRepo;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ObjectMapper om;
	@Autowired
	private DiscoveryClient discoveryClient;

	/**
	 * Method to find all accounts from the repo.
	 * @return List<Accounts>
	 */
	public List<Accounts> findAll() {
		return accountsRepo.findAll();
	}
	
	/**
	 * Method to find all accounts that have the same UserID.
	 * @param userId int id value to search by 
	 * @return List<Accounts>
	 * @throws AccountNotFoundException
	 */
	public List<Accounts> findByUserId(int userId) throws AccountNotFoundException {
		Optional<List<Accounts>> accounts = accountsRepo.findByUserId(userId);
		if (accounts.isPresent()) {
			return accounts.get();
		} else {
			throw new AccountNotFoundException("No accounts found");
		}
	}
	
	/**
	 * Method to find an account by id value. 
	 * @param id 
	 * @param user
	 * @return Accounts
	 * @throws AccountNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	public Accounts findById(int id, SystemUserDTO user) throws AccountNotFoundException, UnauthorizedAccessException {
		Optional<Accounts> account = accountsRepo.findById(id);
		if (account.isPresent()) {
			if (account.get().getUserId() != user.getId()) {
				throw new UnauthorizedAccessException();
			}
			return account.get();
		} else {
			throw new AccountNotFoundException();
		}
	}
	
	/**
	 * Method to save a new account to the repo. 
	 * @param account
	 * @return Accounts
	 * @throws TypeNotFoundException
	 */
	public Accounts save(Accounts account) throws TypeNotFoundException {
		Optional<AccountType> type = accountTypeRepo.findById(account.getType().getId());
		if (type.isPresent()) {
			account.setType(type.get());
			return accountsRepo.save(account);			
		} else {
			throw new TypeNotFoundException("Type Does Not Exist");
		}
	}
	
	/**
	 * Method to update an account in the repo. 
	 * @param accountData
	 * @param user
	 * @return Accounts
	 * @throws NoSuchElementException
	 * @throws AccountNotFoundException
	 * @throws TypeNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	public Accounts update(Accounts accountData, SystemUserDTO user) throws NoSuchElementException, AccountNotFoundException, TypeNotFoundException, UnauthorizedAccessException {
		Optional<Accounts> account = accountsRepo.findById(accountData.getId());
		if (account.isPresent()) {
			if (account.get().getUserId() != user.getId()) {
				throw new UnauthorizedAccessException();
			}
			if (accountData.getType() != null) {
				Optional<AccountType> type = accountTypeRepo.findById(accountData.getType().getId());
				
				if (type.isPresent()) {
					account.get().setType(type.get());
				} else {
					throw new TypeNotFoundException();
				}
			}
			if (accountData.getDescription() != null) {
				account.get().setDescription(accountData.getDescription());
			}
			if (accountData.getBalance() != 0.0) {
				account.get().setBalance(accountData.getBalance());
			}
			
			return accountsRepo.save(account.get());			
		} else {
			throw new AccountNotFoundException();
		}
	}
	
	/**
	 * Method to handle deleting an account from the repo.
	 * @param id
	 * @param user
	 * @return boolean
	 * @throws AccountNotFoundException
	 * @throws UnauthorizedAccessException
	 */
	public boolean delete(int id, SystemUserDTO user) throws AccountNotFoundException, UnauthorizedAccessException {
		Optional<Accounts> account = accountsRepo.findById(id);
		if (account.isPresent()) {
			if (account.get().getUserId() != user.getId()) {
				throw new UnauthorizedAccessException();
			}
			accountsRepo.delete(account.get());
			return true;			
		} else {
			throw new AccountNotFoundException();
		}
		
	}
	
	/**
	 * Method to get a user from the User-Auth-Service.
	 * @param token
	 * @return SystemUserDTO
	 * @throws ProcessingException
	 * @throws ProcessingRuntimeException
	 * @throws UserNotFoundException
	 */
	public SystemUserDTO getUser(String token) throws ProcessingException, ProcessingRuntimeException, UserNotFoundException {
		HttpEntity<String> request;
		SystemUserDTO user = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			request = new HttpEntity<String>(om.writeValueAsString(token), headers);
			String url = discoveryClient.getInstances("user-auth-service").get(0).getUri().toString();
			user = restTemplate.postForObject(url + "/validate", request, SystemUserDTO.class);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			throw new ProcessingException("Unable to send token validate request");
		}
		return user;
	}
}
