package com.btg.website.model;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="order_table")
public class Order {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String orderNumber;
	private Date orderDate;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderLineItem> lineItems;
	
	private double orderSubTotal;
	private double orderTax;
	private double orderShipping;
	private double orderTotal;
	private Status orderStatus;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "card_id", referencedColumnName = "id")
	private CreditCard card;
	
	public Order() {}
	
	public Order(String orderNumber, Customer customer, 
			List<OrderLineItem> lineItems, double orderSubTotal, double orderTax, 
			double orderShipping, double orderTotal, CreditCard card) {
		this.orderNumber = orderNumber;
		this.customer = customer;
		this.lineItems = lineItems;
		this.orderSubTotal = orderSubTotal;
		this.orderTax = orderTax;
		this.orderShipping = orderShipping;
		this.orderTotal = orderTotal;
		this.card = card;
	}
	
	public String getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public Date getOrderDate() {
		return orderDate;
	}
	
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	public double getOrderSubTotal() {
		return orderSubTotal;
	}
	
	public void setOrderSubTotal(double orderSubTotal) {
		this.orderSubTotal = orderSubTotal;
	}
	
	public double getOrderTax() {
		return orderTax;
	}
	
	public void setOrderTax(double orderTax) {
		this.orderTax = orderTax;
	}
	
	public double getOrderShipping() {
		return orderShipping;
	}
	
	public void setOrderShipping(double orderShipping) {
		this.orderShipping = orderShipping;
	}
	
	public double getOrderTotal() {
		return orderTotal;
	}
	
	public void setOrderTotal(double orderTotal) {
		this.orderTotal = orderTotal;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public List<OrderLineItem> getLineItems() {
		return lineItems;
	}
	
	public void setLineItems(List<OrderLineItem> lineItems) {
		this.lineItems = lineItems;
	}
	
	public CreditCard getCard() {
		return card;
	}
	
	public void setCard(CreditCard card) {
		this.card = card; 
	}
	
	public Status getOrderStatus() {
		return orderStatus;
	}
	
	public void setOrderStatus(Status orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public long getId() {
		return id;
	}

	public void setOrderId(Long id) {
		this.id = id;
	}
}