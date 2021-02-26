package org.generictech.accounts.repo;

import org.generictech.accounts.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface is for database interactions with the account_type table.
 * It extends the JpaRepository interface to implement this database interaction. 
 * @author Jaden Wilson
 * @since 1.0
 *
 */
@Repository
public interface AccountTypeRepo extends JpaRepository<AccountType, Integer>{

}
