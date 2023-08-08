package com.btg.website.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "billing_address", referencedColumnName = "id")
	private Address billingAddress;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="shipping_address", referencedColumnName = "id")
	private Address shippingAddress;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "company_name", referencedColumnName = "id")
	private Company company;
	
	private String email;
	
	@Column(name="phone_number")
	private String phoneNumber;
	private String username;
	private String password;

	@Column(name="sign_up_date")
	private Date signupDate;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "wish_list", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_customer_wish_list_id"))
	private WishList wishList;
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CreditCard> creditCards;
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Review> reviews = new ArrayList<>();


	public Customer(String firstName, String lastName, Address billingAddress, Company company, String email, String phoneNumber,
			String username, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.billingAddress = billingAddress;
		this.company = company;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.username = username;
		this.password = password;
	}
	
	public Customer(String firstName, String lastName, Address billingAddress, String email, String phoneNumber,
			String username, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.billingAddress = billingAddress;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.username = username;
		this.password = password;
	}
	
	public Customer(String firstName, String lastName, 
			String email, String phoneNumber, String username, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.username = username;
		this.password = password;
	}
}