package com.btg.website.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WishList {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private long productId;
	
	private long customerId;
	
	private Date addedDate;
	
	public WishList() {}

	public WishList(long customerId, long productId, Date addedDate) {
		this.customerId = customerId;
		this.productId = productId;
		this.addedDate = addedDate;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public long getId() {
		return id;
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
