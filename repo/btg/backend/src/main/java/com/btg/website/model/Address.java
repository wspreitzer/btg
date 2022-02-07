package com.btg.website.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="ADDRESS_TABLE")
public class Address {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String street;
	private String city;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="state_id", referencedColumnName = "id")
	private State state;
	
	@Column(name="zip_code")
	private String zipCode;
	
	public Address(String street, String city, State state, String zipCode) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
	}

	public long getId() {
		return this.id;
	}
	
	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public State getState() {
		return this.state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getZipCode() {
		return this.zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
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
		sb.append(street);
		sb.append(" ");
		sb.append(city);
		sb.append(", ");
		sb.append(state.getAbv());
		sb.append(" ");
		sb.append(zipCode);
		return super.toString();
	}	
}