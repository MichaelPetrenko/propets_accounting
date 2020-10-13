package telran.accounting.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.accounting.service.TokenService;

@Service
public class DataCheck {

	@Autowired
	TokenService tokenService;

	public String[] gettingCredentials(String token) {

		String[] credentials;
		try {
			credentials = tokenService.decompileToken(token);
		} catch (Exception e) {
			return null;
		}
		if (credentials.length != 4) {
			return null;
		}
		if (credentials[0] == "" || credentials[0] == " " || credentials[1] == "" || credentials[1] == " ") {
			return null;
		}
		return credentials;
	}
	
	public String gettingToken(HttpServletRequest request, String headerType) throws IOException {
		String token;
		try {
			token = request.getHeader(headerType);
		} catch (Exception e) {
			return null;
		}

		if (token == null || token == "" || token == " ") {
			return null;
		}
		return token;
	}
	
	public String[] gettingAuthCredentials(HttpServletRequest request, String auth)
			throws IOException {

		String[] credentials;
		try {
			credentials = tokenService.validateAuth(auth);
		} catch (Exception e1) {
			return null;
		}
		if (credentials.length != 2) {
			return null;
		}
		if (credentials[0] == "" || credentials[0] == " " || credentials[1] == "" || credentials[1] == " ") {
			return null;
		}
		return credentials;
		
	}

}
