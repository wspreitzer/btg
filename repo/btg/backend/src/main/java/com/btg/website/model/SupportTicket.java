package com.btg.website.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class SupportTicket {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String title;
	private String description;
	
	@Column(name="creation_date")
	private Date creationDate;
	
	private String status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customer;
	
	public SupportTicket() {}
	public SupportTicket(String title, String description, Date creationDate, String status, Customer customer) {
		this.title = title;
		this.description = description;
		this.creationDate = creationDate;
		this.status = status;
		this.customer = customer;
	}
	
	public long getId() {
		return this.id;
	}	

	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getCreationDate() {
		return this.creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
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
		return super.toString();
	}
}