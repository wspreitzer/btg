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
import javax.persistence.Table;

@Entity
@Table(name = "CART_ITEM")
public class CartItem {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Cart cart;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	private Product product;

	private int qty;
	private double lineTotal;

	public long getId() {
		return id;
	}
	
	public Cart getCart() {
		return cart;
	}
	
	public void setCart(Cart cart) {
		this.cart = cart;
	}
	
	public Product getProduct() {
		return product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public int getQty() {
		return qty;
	}
	
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public double getLineTotal() {
		return lineTotal;
	}
	
	public void setLineTotal(double lineTotal) {
		this.lineTotal = lineTotal;
	}
	
	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj ) return true;
		if (!(obj instanceof CartItem )) return false;
		return id != null && id.equals(((CartItem) obj).getId());
	}
}