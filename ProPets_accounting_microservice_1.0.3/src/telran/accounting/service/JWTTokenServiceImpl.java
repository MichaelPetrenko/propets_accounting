package telran.accounting.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import telran.accounting.api.codes.NotExistsException;
import telran.accounting.api.codes.TokenExpiredException;
import telran.accounting.domain.dao.AccountsRepository;
import telran.accounting.domain.entities.AccountEntity;
import telran.accounting.domain.entities.AccountingRoles;

@Service
public class JWTTokenServiceImpl implements TokenService {
	
	@Autowired
	AccountsRepository repo;

	String secret = "ProPetsEvgeniiMichael19911995";

	@Override
	public String createToken(AccountEntity accountEntity) {
		return createToken(accountEntity.getEmail(), accountEntity.getPass(), accountEntity.getRoles());
	}

	@Override
	public String createToken(String email, String pass, HashSet<AccountingRoles> roles) {
		Instant time = Instant.now();
		return Jwts.builder().claim("login", email).claim("password", pass)
				.claim("timestamp", time.plus(30, ChronoUnit.DAYS).toEpochMilli())
				.claim("role", roles).signWith(SignatureAlgorithm.HS256, secret).compact();
		
	}

	@Override
	public String validateToken(String token) {
		Jws<Claims> jws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
		Claims claims = jws.getBody();
		
		String login = claims.get("login").toString();
		AccountEntity user = repo.findById(login).orElse(null);
		
		if(user == null) {
			throw new NotExistsException();
		}
		
		Instant time = Instant.ofEpochMilli(Long.parseLong("" + claims.get("timestamp")));
		
		if (time.isBefore(Instant.now())) {
			throw new TokenExpiredException();
		}
		
		claims.put("timestamp", Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());
		token = Jwts.builder().setClaims(claims).compact();
		
		return token;
	}

	@Override
	public String[] decompileToken(String token) {
		Jws<Claims> jws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
		Claims claims = jws.getBody();
		String[] res = new String[4];
		res[0] = claims.get("login").toString();
		res[1] = claims.get("password").toString();
		res[2] = claims.get("timestamp").toString();
		String roles = claims.get("role").toString();
		res[3] = roles.substring(1, roles.length()-1);
		//[login, pass, 3463457, "USER, ADMIN"]
		return res;
	}

}
