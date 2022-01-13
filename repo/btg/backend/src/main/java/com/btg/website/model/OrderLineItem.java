package com.btg.website.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class OrderLineItem {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private long orderId;
	private long productId;
	private int qty;
	private double lineTotal; 
	
	public OrderLineItem() {}
	
	public OrderLineItem(long orderId, long productId, int qty, double lineTotal) {
		this.orderId = orderId;
		this.productId = productId;
		this.qty = qty;
		this.lineTotal = lineTotal;
	}
	
	public long getId() {
		return this.id;
	}
	
	public long getOrderId() {
		return this.orderId;
	}
	
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	
	public long getProductId() {
		return this.productId;
	}
	
	public void setProductId(long productId) {
		this.productId = productId;
	}
	
	public int getQty() {
		return this.qty;
	}
	
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public double getLineTotal() {
		return this.lineTotal;
	}
	
	public void setLineTotal(double lineTotal) {
		this.lineTotal = lineTotal;
	}
}