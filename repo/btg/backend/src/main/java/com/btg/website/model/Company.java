package com.btg.website.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Company {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String name;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="billing_address", referencedColumnName="id")
	private Address billingAddress;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="shipping_address", referencedColumnName="id")
	private Address shippingAddress;
	private double discount;
	private String phoneNumber;

	public Company(String name,	Address billingAddress, Address shippingAddress, double discount, String phoneNumber) {
		this.name = name;
		this.billingAddress = billingAddress;
		this.shippingAddress = shippingAddress;
		this.discount = discount;
		this.phoneNumber = phoneNumber;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getBillingAddress() {
		return this.billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public Address getShippingAddress() {
		return this.shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
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
		sb.append(this.billingAddress.toString());
		if(shippingAddress != null) {
			sb.append(" ");
			sb.append(shippingAddress);
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