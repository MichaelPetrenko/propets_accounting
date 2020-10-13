package telran.accounting.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import telran.accounting.api.codes.NotExistsException;
import telran.accounting.domain.dao.AccountsRepository;
import telran.accounting.domain.entities.AccountEntity;
import telran.accounting.domain.entities.AccountingRoles;

//@Service
public class TokenServiceImplDeprecated implements TokenService {
	
	@Autowired
	AccountsRepository repo;

	/**
	 * This method will create x-token like this:	
	 * login-password-2628937427:USER,ADMIN
	 */
	@Override
	public String createToken(AccountEntity accountEntity) {
		return createToken(
				accountEntity.getEmail(), 
				accountEntity.getPass(), 
				accountEntity.getRoles());
	}
	
	@Override
	public String createToken(String email, String pass, HashSet<AccountingRoles> hashSet) {
		Instant time = Instant.now();
		String token = email + "-" + pass + "-" 
				+ time.plus(30, ChronoUnit.DAYS).toEpochMilli() + ":"+ hashSet;
		token = Base64.getUrlEncoder().encodeToString(token.getBytes());
		return token;
	}
	
	@Override
	public String[] decompileToken(String token) {
		if(token==null || token=="" || token==" ") {
			return null;
		}
		token = token.split(" ")[1];
		String[] parts = new String(Base64.getDecoder().decode(token)).split(":");
		String[] credentials = parts[0].split("-");
		return credentials; // [login,pass,8746598634]
	}
	
//	@Override
//	public String[] decompileRolesFromToken(String token) {
//		if(token==null || token=="" || token==" ") {
//			return null;
//		}
//		token = token.split(" ")[1];
//		String[] parts = new String(Base64.getDecoder().decode(token)).split(":");
//		String res = parts[1].substring(1, parts[1].length()-1);
//		String[] roles = res.split(", ");
//		
//		return roles; // [USER,ADMIN,SPECIALIST]
//	}

	/**
	 * Here we need to make timestamp checking.
	 * If expired- throw ExpiredExc.
	 * TODO
	 */
	@Override
	public String validateToken(String token) {
		token = token.split(" ")[1];
		String[] parts = new String(Base64.getDecoder().decode(token)).split(":");	
		String[] userInfo = parts[0].split("-"); // login-password-2628937427 => [login, pass, 874656]
		String login = userInfo[0];
		AccountEntity user = repo.findById(login).orElse(null);
		if(user == null) {
			throw new NotExistsException();
		}
		String newToken = createToken(user);
		return newToken;
	}
}
