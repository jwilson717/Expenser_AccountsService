package org.generictech.accounts.repo;

import java.util.List;
import java.util.Optional;

import org.generictech.accounts.model.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface is for database interactions with the accounts table. 
 * It extends the JpaRepository interface to allow these database interactions 
 * @author Jaden Wilson
 * @since 1.0
 *
 */
@Repository
public interface AccountsRepo extends JpaRepository<Accounts, Integer>{

	public Optional<List<Accounts>> findByUserId(int id);
}
