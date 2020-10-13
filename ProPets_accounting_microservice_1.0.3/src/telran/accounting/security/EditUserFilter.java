package telran.accounting.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import telran.accounting.api.codes.TokenExpiredException;
import telran.accounting.service.TokenService;

@Service
public class EditUserFilter implements Filter {
	
	@Autowired
	TokenService tokenService;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	DataCheck dataCheck;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		if (request.getServletPath().matches("/account/en/v1/1[^/]+")) {
			
			String token = dataCheck.gettingToken(request, "X-Token");
			if (token == null) {
				response.sendError(401);
				return;
			}
			
			String[] credentials = dataCheck.gettingCredentials(token);

			if (credentials == null) {
				response.sendError(401);
				return;
			}
			
			String login1 = request.getServletPath().split("/")[4];
			String login = login1.substring(1);
			
			if(!login.equals(credentials[0])) {
				response.sendError(400);
				return;
			}
			
			String tokenHeader;
			try {
				tokenHeader = tokenService.validateToken(token);
				response.setHeader("X-Token", tokenHeader);
			} catch (TokenExpiredException e) {
				response.sendError(403);
				return;
			}	
		} 	
		chain.doFilter(request, response);
	}
}
