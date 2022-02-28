package com.btg.website.model;

import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;

@Entity
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customer;
	
	private String review;
	
	@Column(name="review_date")
	private Date reviewDate;

	public Review(Customer customer, String review, Date reviewDate) {
		this.customer = customer;
		this.review = review;
		this.reviewDate = reviewDate;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public String getReview() {
		return review;
	}
	
	public void setReview(String review) {
		this.review = review;
	}
	
	public Date getReviewDate() {
		return reviewDate;
	}
	
	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Review )) return false;
		return id != null && id.equals(((Review) o).getId());
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(customer.getFirstName());
		sb.append(", ");
		sb.append(customer.getLastName());
		sb.append(" wrote this ");
		sb.append(review);
		sb.append(" on: ");
		sb.append(reviewDate);
		return sb.toString();
	}
}