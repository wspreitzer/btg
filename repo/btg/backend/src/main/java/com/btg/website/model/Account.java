package com.btg.website.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ACCOUNT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@OneToOne
	@JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "fk_account_customer_id"))
	private Customer customer;

	@Column(name = "ACCOUNT_ID")
	private Long accountId;
	@JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "fk_account_account_id"))
	private AccountType accountType;
	private double balance;
	
	
	public Account(Customer customer, long accountId, AccountType accountType, double balance) {
		this.customer = customer;
		this.accountId = accountId;
		this.accountType = accountType;
		this.balance = balance;
	}
}