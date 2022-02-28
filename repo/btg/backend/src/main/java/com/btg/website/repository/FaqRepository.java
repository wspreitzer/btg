package com.btg.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.btg.website.model.Faq;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long>, JpaSpecificationExecutor<Faq> {
}
