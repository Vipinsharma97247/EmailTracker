package com.example.demo;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="user_table")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	String username;
	String password;
	@Column(name = "account_creation_date")

	LocalDate accounCreationDate;
	@Column(name = "account_type")
	@Value(value = "free")
	String accountType;
	@Column(name = "mail_limit")

	int mailLimit;
	@Column(name = "mail_count")
	@Value(value = "0")
	int mailCount;
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getId() {
		return id;
	}
	public LocalDate getAccounCreationDate() {
		return accounCreationDate;
	}
	public void setAccounCreationDate(LocalDate accounCreationDate) {
		this.accounCreationDate = accounCreationDate;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public int getMailLimit() {
		return mailLimit;
	}
	public void setMailLimit(int mailLimit) {
		this.mailLimit = mailLimit;
	}
	
	
	
}
