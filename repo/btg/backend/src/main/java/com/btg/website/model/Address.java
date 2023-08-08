package com.btg.website.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ADDRESS_TABLE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String street;
	private String city;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="state_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_state_id"))
	private State state;
	
	@Column(name="zip_code")
	private String zipCode;	
	
	public Address(String street, String city, State state, String zipCode) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
	}
}