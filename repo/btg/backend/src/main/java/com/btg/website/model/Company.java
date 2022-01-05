package com.btg.website.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Company {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String name;
	private String contactFirstName;
	private String contactLastName;
	private Address address;
	private Address address2;
	private double discount;
	private String phoneNumber;

	public Company(String name, String contactFirstName, String contactLastName, 
			Address address, Address address2, double discount, String phoneNumber) {
		this.name = name;
		this.contactFirstName = contactFirstName;
		this.contactLastName = contactLastName;
		this.address = address;
		this.address2 = address2;
		this.discount = discount;
		this.phoneNumber = phoneNumber;
	}
	
	public String getName() {
		return this.name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getContactFirstName() {
		return this.contactFirstName;
	}



	public void setContactFirstName(String contactFirstName) {
		this.contactFirstName = contactFirstName;
	}



	public String getContactLastName() {
		return this.contactLastName;
	}



	public void setContactLastName(String contactLastName) {
		this.contactLastName = contactLastName;
	}



	public Address getAddress() {
		return this.address;
	}



	public void setAddress(Address address) {
		this.address = address;
	}



	public Address getAddress2() {
		return this.address2;
	}



	public void setAddress2(Address address2) {
		this.address2 = address2;
	}



	public double getDiscount() {
		return this.discount;
	}



	public void setDiscount(double discount) {
		this.discount = discount;
	}



	public String getPhoneNumber() {
		return this.phoneNumber;
	}



	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}



	public long getId() {
		return this.id;
	}



	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.name);
		sb.append(" ");
		sb.append(this.contactLastName);
		sb.append(",");
		sb.append(this.contactFirstName);
		sb.append(" ");
		sb.append(this.address.toString());
		if(address2 != null) {
			sb.append(" ");
			sb.append(address2);
			sb.append(" ");
		} else {
			sb.append(" ");
		}
		sb.append(this.discount);
		sb.append(" ");
		sb.append(this.phoneNumber);
		return sb.toString();
	}
}