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
@Table(name = "CART")
public class Cart {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="customer_id", referencedColumnName = "id")
	private Customer customer;
	
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items;
	
	private Date createdDate;
	
	public Cart() {}
	
	public Cart(Customer customer, List<CartItem> items, Date createdDate) {
		this.customer = customer;
		this.items = items;
		this.createdDate = createdDate;
	}
	
	public Long getId() {
		return id;
	}

	public Customer getCustomer() {
		return customer;
	}
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public List<CartItem> getItems() {
		return items;
	}
	
	public void setItems(List<CartItem> items) {
		this.items = items;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	
	public void addCartItem(CartItem item) {
		items.add(item);
	}
	
	public void removeCartItem(CartItem item) {
		items.remove(item);
		item.setCart(null);
	}
}