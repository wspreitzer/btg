package com.btg.website.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CREDIT_CARD")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "fk_credit_card_customer_id"))
	private Customer customer;
	private String type;
	private String number;
	private String exMon;
	private String exYr;
	private String cvv;
	public CreditCard(Customer customer, String type, String number, String exMon, String exYr, String cvv) {
		this.customer = customer;
		this.type = type;
		this.number = number;
		this.exMon = exMon;
		this.exYr = exYr;
		this.cvv = cvv;
	}
}