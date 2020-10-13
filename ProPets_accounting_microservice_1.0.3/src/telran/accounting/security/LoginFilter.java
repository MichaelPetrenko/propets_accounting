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

import telran.accounting.domain.dao.AccountsRepository;
import telran.accounting.domain.entities.AccountEntity;
import telran.accounting.service.TokenService;

@Service
public class LoginFilter implements Filter {

	@Autowired
	AccountsRepository repo;

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

		if (request.getServletPath().startsWith("/account/en/v1/login")) {

			// Getting auth
			String auth = dataCheck.gettingToken(request, "Authorization");

			if (auth == null) {
				response.sendError(401);
				return;
			} else {

				// Getting credentials
				String[] credentials = dataCheck.gettingAuthCredentials(request, auth);
				if (credentials == null) {
					response.sendError(401);
					return;
				}

				// Getting user
				AccountEntity accountEntity = repo.findById(credentials[0]).orElse(null);
				if (accountEntity == null) {
					response.sendError(401, "User not exists");
					return;
				}

				if (!encoder.matches(credentials[1], accountEntity.getPass())) {
					response.sendError(400, "Wrong password");
					return;
				}

				String tokenHeader = tokenService.createToken(accountEntity);
				response.setHeader("X-Token", tokenHeader);
			}
		}
		chain.doFilter(request, response);
	}
}
