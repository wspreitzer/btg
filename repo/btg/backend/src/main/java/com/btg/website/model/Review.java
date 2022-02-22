package com.btg.website.model;

import java.sql.Date;

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
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
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
	
	/*
	 * @Override public int hashCode() {
	 * 
	 * int result = id != null ? id.hashCode() : 0; if(customer != null) { result =
	 * 31 * result + (customer.getFirstName() != null ?
	 * customer.getFirstName().hashCode() : 0); result = 31 * result +
	 * (customer.getLastName() != null ? customer.getLastName().hashCode() : 0); }
	 * result = 31 * result + (review != null ? review.hashCode() : 0 ); result = 31
	 * result + (reviewDate != null ? reviewDate.hashCode() : 0);
	 * 
	 * return super.hashCode(); }
	 * 
	 * @Override public boolean equals(Object obj) { boolean retVal; if ((obj !=
	 * null && (this == obj || obj instanceof Review))) { Review review = (Review)
	 * obj; retVal = true; if( id != null ? !id.equals(review.id) : review.id !=
	 * null) { retVal = false; } if( customer != null ? customer.equals(customer) :
	 * customer != null) { retVal = false; } if (review != null ?
	 * review.equals(review.review) : review.review != null) { retVal = false; } if
	 * (reviewDate != null ? reviewDate.equals(review.reviewDate) :
	 * review.reviewDate != null ) { retVal = false; } } else { retVal = false; }
	 * return super.equals(obj); }
	 * 
	 */	@Override
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