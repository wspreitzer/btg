package com.btg.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.btg.website.model.OrderLineItem;

public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Long>, JpaSpecificationExecutor<OrderLineItem> {
}
