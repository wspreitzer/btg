package com.btg.website.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.model.Account;
import com.btg.website.repository.AccountService;

@RestController
public class AccountController {

	private AccountService accountService;
	
	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}
	
	@PostMapping(value = "/api/account")
	public Account createAccount(@RequestBody Account account, HttpServletResponse response, HttpServletRequest request) {
		Long accountId = accountService.save(account).getAccountId();
		account.setAccountId(accountId);
		response.setStatus(HttpStatus.CREATED.value());
		response.setHeader("Location", String.format("%s/api/account/%s", request.getContextPath(), accountId, null));
		return account;
	}
	
	@GetMapping(value = "/api/account/{accountId}")
	public Account getAccount(@PathVariable("accountId") Long accountId) throws Exception {
		if(accountId < 9999) {
			throw new InvalidAccountRequestException();
		}
		Account account = accountService.getById(accountId);
		if(null == account) {
			throw new AccountNotFoundException();
		}
		return account;
	}
}
