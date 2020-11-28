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
import telran.accounting.domain.dao.AccountsRepository;
import telran.accounting.domain.entities.AccountEntity;
import telran.accounting.service.TokenService;

@Service
public class RemoveUserFilter implements Filter {
	
	@Autowired
	TokenService tokenService;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	AccountsRepository repo;
	
	@Autowired
	DataCheck dataCheck;
	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		//String GET_USER_DATA = "/account/en/v1/{login}?dataType=true";
		if (request.getServletPath().matches("/en/v1/[^/]+") && request.getMethod().equalsIgnoreCase("DELETE")) {
			
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
			
			String login = request.getServletPath().split("/")[3];
			
			if(!login.equals(credentials[0])) {
				response.sendError(400);
				return;
			}
			
			AccountEntity accountEntity = repo.findById(credentials[0]).orElse(null);
			if (accountEntity == null) {
				response.sendError(401, "User not exists");
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
