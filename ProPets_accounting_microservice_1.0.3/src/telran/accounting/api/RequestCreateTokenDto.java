package telran.accounting.api;

import java.util.HashSet;

import telran.accounting.domain.entities.AccountingRoles;

public class RequestCreateTokenDto {
	
	public String email;
	public String pass;
	public HashSet<AccountingRoles> roles;
	
	public RequestCreateTokenDto(String email, String pass, HashSet<AccountingRoles> roles) {
		super();
		this.email = email;
		this.pass = pass;
		this.roles = roles;
	}

	public RequestCreateTokenDto() {
		super();
	}

	
	
}
