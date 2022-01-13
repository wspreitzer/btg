package com.btg.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.btg.website.model.State;

public interface StateRepository extends JpaRepository<State, Long>, JpaSpecificationExecutor<State> {
}
