package com.btg.website.model;

import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Order {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String orderNumber;
	private Date orderDate;
	private long customerId;
	private List<OrderLineItem> lineItems;
	private double orderSubTotal;
	private double orderTax;
	private double orderShipping;
	private double orderTotal;
	private String orderStatus;
	
	public Order() {}
	
	public Order(String orderNumber, Date orderDate, long customerId, 
			List<OrderLineItem> lineItems, double orderSubTotal, double orderTax, 
			double orderShipping, double orderTotal, String orderStatus) {
		this.orderNumber = orderNumber;
		this.orderDate = orderDate;
		this.customerId = customerId;
		this.lineItems = lineItems;
		this.orderSubTotal = orderSubTotal;
		this.orderTax = orderTax;
		this.orderShipping = orderShipping;
		this.orderTotal = orderTotal;
		this.orderStatus = orderStatus;
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
	
	public long getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	
	public List<OrderLineItem> getLineItems() {
		return lineItems;
	}
	
	public void setLineItems(List<OrderLineItem> lineItems) {
		this.lineItems = lineItems;
	}
	
	public String getOrderStatus() {
		return orderStatus;
	}
	
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public long getId() {
		return id;
	}
}