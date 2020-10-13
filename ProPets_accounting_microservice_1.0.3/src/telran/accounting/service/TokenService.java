package telran.accounting.service;

import java.util.Base64;
import java.util.HashSet;

import telran.accounting.domain.entities.AccountEntity;
import telran.accounting.domain.entities.AccountingRoles;

public interface TokenService {
	
	String createToken(AccountEntity accountEntity);
	String createToken(String email, String pass, HashSet<AccountingRoles> roles);
	String validateToken(String token);
	String[] decompileToken(String token);

	default String[] validateAuth(String token) {
		token = token.split(" ")[1];
		String[] credentials = new String(Base64.getDecoder().decode(token)).split(":");
		return credentials;
	}
}
