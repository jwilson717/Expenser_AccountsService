package org.generictech.accounts.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.generictech.accounts.exception.BadValueException;
import org.generictech.accounts.exception.ProcessingException;
import org.generictech.accounts.exception.TypeNotFoundException;
import org.generictech.accounts.model.AccountType;
import org.generictech.accounts.repo.AccountTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class to handle business logic pertaining the the account_type table
 * @author Jaden Wilson
 * @since 1.0
 *
 */
@Service
public class AccountTypeService {

	@Autowired
	private AccountTypeRepo accountTypeRepo;
	
	/**
	 * Method to handle retrieving all type records from the repo.
	 * @return List<AccountType>
	 */
	public List<AccountType> findAll() {
		return accountTypeRepo.findAll();
	}
	
	/**
	 * Method to handle finding a single {@link AccountType} from the repo based on ID. 
	 * @param id
	 * @return AccountType
	 * @throws TypeNotFoundException
	 */
	public AccountType findById(int id) throws TypeNotFoundException {
		Optional<AccountType> type = accountTypeRepo.findById(id);
		if (type.isPresent()) {
			return type.get();
		} else {
			throw new TypeNotFoundException();
		}
	}
	
	/**
	 * Method to handle persisting a new {@link AccountType} to the repo.
	 * @param type
	 * @return {@link AccountType}
	 * @throws NoSuchElementException
	 * @throws ProcessingException 
	 * @throws BadValueException 
	 */
	public AccountType save(AccountType type) throws NoSuchElementException, BadValueException {
		if (type.getType() == null || type.getType().isEmpty()) {
			throw new BadValueException("Non-empty type value required");
		}
		return accountTypeRepo.save(type);
	}
	
	/**
	 * Method to handle updating a persistent {@link AccountType} in the repo. 
	 * @param typeData
	 * @return {@link AccountType}
	 * @throws TypeNotFoundException
	 */
	public AccountType update(AccountType typeData) throws TypeNotFoundException {
		Optional<AccountType> type = accountTypeRepo.findById(typeData.getId());
		if (type.isPresent()) {
			if (typeData.getType() != null) {
				type.get().setType(typeData.getType());
			}
			return accountTypeRepo.save(type.get());
		} else {
			throw new TypeNotFoundException();
		}
	}
	
	/**
	 * Method to handle deleting a persistent {@link AccountType} in the repo
	 * @param id
	 * @return {@link AccountType}
	 * @throws NoSuchElementException
	 * @throws TypeNotFoundException
	 */
	public boolean delete(int id) throws NoSuchElementException, TypeNotFoundException {
		Optional<AccountType> type = accountTypeRepo.findById(id); 
		if (type.isPresent()) {
			accountTypeRepo.delete(type.get());
			return true;			
		} else {
			throw new TypeNotFoundException();
		}
	}
}
