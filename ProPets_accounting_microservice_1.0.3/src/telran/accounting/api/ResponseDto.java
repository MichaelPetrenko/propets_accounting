package telran.accounting.api;

import java.util.HashSet;

import telran.accounting.domain.entities.AccountingRoles;

public class ResponseDto {
	public String email;
	public String name;
	public String avatar;
	public String phone;
	public HashSet<AccountingRoles> roles;
	
	public ResponseDto(String email, String name, String avatar, String phone, HashSet<AccountingRoles> roles) {
		super();
		this.email = email;
		this.name = name;
		this.avatar = avatar;
		this.phone = phone;
		this.roles = roles;
	}
	
	public ResponseDto() {}
	
	
}

