package telran.accounting.service;

import java.net.URI;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import telran.accounting.domain.dao.AccountsRepository;
import telran.accounting.domain.entities.AccountEntity;
import telran.accounting.domain.entities.AccountingRoles;
import telran.accounting.api.RequestCreateTokenDto;
import telran.accounting.api.codes.NoContentException;

@Service
public class TokenServiceConnector implements TokenService {

	@Autowired
	AccountsRepository repo;

	@Override
	public String createToken(AccountEntity accountEntity) {

		RestTemplate restTemplate = new RestTemplate();
		String endPoint = "http://propets-token.herokuapp.com/validation/en/v1/create/entity";
//		String endPoint = "http://localhost:8082/validation/en/v1/create/entity";

		URI uri;
		try {
			uri = new URI(endPoint);
		} catch (Exception e) {
			throw new NoContentException();
		}

		RequestEntity<AccountEntity> requestToCreateToken = RequestEntity.post(uri).accept(MediaType.APPLICATION_JSON)
				.body(accountEntity);

		ResponseEntity<String> responceFromCreateToken = restTemplate.exchange(uri, HttpMethod.POST,
				requestToCreateToken, String.class);

		return responceFromCreateToken.getBody().toString();
	}

	@Override
	public String createToken(String email, String pass, HashSet<AccountingRoles> roles) {

		String endPoint = "http://propets-token.herokuapp.com/validation/en/v1/create/email";
		RestTemplate restTemplate = new RestTemplate();

		URI uri;
		try {
			uri = new URI(endPoint);
		} catch (Exception e) {
			throw new NoContentException();
		}
		RequestCreateTokenDto dto = new RequestCreateTokenDto(email, pass, roles);

		RequestEntity<RequestCreateTokenDto> requestToCreateTokenByEmail = RequestEntity.post(uri)
				.accept(MediaType.APPLICATION_JSON).body(dto);

		ResponseEntity<String> responceFromCreateToken = restTemplate.exchange(uri, HttpMethod.POST,
				requestToCreateTokenByEmail, String.class);

		return responceFromCreateToken.getBody().toString();
	}

	@Override
	public String validateToken(String token) {
		String endPoint = "http://propets-token.herokuapp.com/validation/en/v1/validate";
		RestTemplate restTemplate = new RestTemplate();

		URI uri;
		try {
			uri = new URI(endPoint);
		} catch (Exception e) {
			throw new NoContentException();
		}

		RequestEntity<String> requestToValidateToken = RequestEntity.post(uri).accept(MediaType.APPLICATION_JSON)
				.body(token);

		ResponseEntity<String> responceFromValidateToken = restTemplate.exchange(uri, HttpMethod.POST,
				requestToValidateToken, String.class);

		return responceFromValidateToken.getBody().toString();
	}

	@Override
	public String[] decompileToken(String token) {
		String endPoint = "http://propets-token.herokuapp.com/validation/en/v1/decompile";
		RestTemplate restTemplate = new RestTemplate();

		URI uri;
		try {
			uri = new URI(endPoint);
		} catch (Exception e) {
			throw new NoContentException();
		}

		RequestEntity<String> requestToDecompile = RequestEntity.post(uri).accept(MediaType.APPLICATION_JSON)
				.body(token);

		ResponseEntity<String[]> responceFromDecompile = restTemplate.exchange(uri, HttpMethod.POST, 
				requestToDecompile, String[].class);
		System.out.println("= = = resp getBody" + responceFromDecompile.getBody());
		return responceFromDecompile.getBody();
	}

	@Override
	public String[] validateAuth(String token) {
		String endPoint = "http://propets-token.herokuapp.com/validation/en/v1/auth";
		RestTemplate restTemplate = new RestTemplate();

		URI uri;
		try {
			uri = new URI(endPoint);
		} catch (Exception e) {
			throw new NoContentException();
		}

		RequestEntity<String> requestToValidateAuth = RequestEntity.post(uri).accept(MediaType.APPLICATION_JSON)
				.body(token);
		ResponseEntity<String[]> responceFromDecompile = restTemplate.exchange(uri, HttpMethod.POST, 
				requestToValidateAuth, String[].class);
		System.out.println("= = = resp getBody" + responceFromDecompile.getBody());
		return responceFromDecompile.getBody();
	}

}
