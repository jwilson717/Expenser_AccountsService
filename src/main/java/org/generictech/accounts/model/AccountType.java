package org.generictech.accounts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * This class is to model the Account_types table of the database.
 * @author Jaden Wilson
 * @since 1.0
 *
 */
@Entity
@Table(name="account_type")
public class AccountType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="account_type_id")
	int id;
	@Column(unique = true)
	@NotNull
	String type;
	
	public AccountType() {
		super();
	}

	public AccountType(int id, @NotNull String type) {
		super();
		this.id = id;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "AccountType [id=" + id + ", type=" + type + "]";
	}
	
}
