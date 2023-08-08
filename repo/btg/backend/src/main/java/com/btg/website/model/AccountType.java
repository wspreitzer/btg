package com.btg.website.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ACCOUNT_TYPE", uniqueConstraints = {@UniqueConstraint(name = "unique_account", columnNames = "accountType")})
public class AccountType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "ACCOUNT_TYPE")
	private String accountType;
}
