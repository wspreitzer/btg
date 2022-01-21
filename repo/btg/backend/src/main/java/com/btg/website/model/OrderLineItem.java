package com.btg.website.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class OrderLineItem {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="product_id", referencedColumnName = "id")
	private Product product;
	
	private int qty;
	private double lineTotal; 
	
	public OrderLineItem() {}
	
	public OrderLineItem(Order order, Product product, int qty, double lineTotal) {
		this.order = order;
		this.product = product;
		this.qty = qty;
		this.lineTotal = lineTotal;
	}
	
	public long getId() {
		return this.id;
	}
	
	public Order getOrder() {
		return this.order;
	}
	
	public void setOrder(Order order) {
		this.order = order;
	}
	
	public Product getProduct() {
		return this.product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
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