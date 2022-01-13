package com.btg.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.btg.website.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
}
