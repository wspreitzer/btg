package com.btg.website.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Service {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String serviceName;
	private String description;
	private double price;
	
	public Service(String serviceName, String description, double price) {
		this.serviceName = serviceName;
		this.description = description;
		this.price = price;
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (int) id;
		hash = 31 * hash +  (int) price;
		hash = 31 * hash + (serviceName == null ? 0 : serviceName.hashCode());
		hash = 31 * hash + (description == null ? 0 : description.hashCode());
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean retVal;
		if ((obj != null && getClass() == obj.getClass()) || this == obj) {
			Service other = (Service) obj;
			if(serviceName != other.serviceName 
					|| description != other.description 
					|| price != price ) {
				retVal = false;
			} else {
				retVal = true;
			}
		} else {
			retVal = false;
		}
		return retVal;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Service: ");
		sb.append(this.serviceName);
		sb.append(" is available for the price of: $");
		sb.append(this.price);
		sb.append(", and we will ");
		sb.append(this.description);
		return sb.toString();
	}
}