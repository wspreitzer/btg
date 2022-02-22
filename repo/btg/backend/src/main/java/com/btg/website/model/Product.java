package com.btg.website.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Product {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String name;
	private String sku;
	private String description;
	private int qty;
	private double price;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private WishList wishList;
	
	protected Product() {}
	
	public Product (String name, String sku, String description, int qty, double price) {
		this.name = name;
		this.sku = sku;
		this.description = description;
		this.qty = qty;
		this.price = price;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSku() {
		return sku;
	}
	
	public void setSku(String sku) {
		this.sku = sku;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getQty() {
		return qty;
	}
	
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public double getPrice() {
		return price;
	}
	
	public WishList getWishList() {
		return wishList;
	}

	public void setWishList(WishList wishList) {
		this.wishList = wishList;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj ) return true;
		if (!(obj instanceof Product )) return false;
		return id != null && id.equals(((Product) obj).getId());
	}

	@Override
	public String toString() {
		return super.toString();
	}
}