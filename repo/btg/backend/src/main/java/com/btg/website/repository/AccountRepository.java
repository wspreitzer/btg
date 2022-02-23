package com.btg.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.btg.website.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{
}
