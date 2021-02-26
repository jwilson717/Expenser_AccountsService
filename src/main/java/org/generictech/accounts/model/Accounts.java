package org.generictech.accounts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;


/**
 * This class is to model the Accounts table in the database. 
 * @author Jaden Wilson
 * @since 1.0
 */
@Entity
public class Accounts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	int id;
	@ManyToOne
	@JoinColumn(name="acount_type_id")
	@NotNull
	AccountType type;
	String description;
	@NotNull
	double balance;
	@Column(name="system_user_id")
	@NotNull
	int userId;
	
	public Accounts() {
		super();
	}

	public Accounts(int id, @NotNull AccountType type, String description, @NotNull double balance, @NotNull int userId) {
		super();
		this.id = id;
		this.type = type;
		this.description = description;
		this.balance = balance;
		this.userId = userId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public double getBalance() {
		return this.balance;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "Accounts [id=" + id + ", type=" + type + ", description=" + description + ", userId=" + userId + "]";
	}
		
}
