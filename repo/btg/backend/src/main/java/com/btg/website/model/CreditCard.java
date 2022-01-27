package com.btg.website.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CreditCard {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customer;
	private String type;
	
	private String number;
	private String exMon;
	private String exYr;
	private String cvv;
	
	public CreditCard() {}
	
	public CreditCard(Customer customer, String type, String number, String exMon, String exYr, String cvv) {
		this.customer = customer;
		this.type = type;
		this.number = number;
		this.exMon = exMon;
		this.exYr = exYr;
		this.cvv = cvv;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getExMon() {
		return exMon;
	}

	public void setExMon(String exMon) {
		this.exMon = exMon;
	}
	
	public String getExYr() {
		return exYr;
	}
	
	public void setExYr(String exYr) {
		this.exYr = exYr;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	

}
