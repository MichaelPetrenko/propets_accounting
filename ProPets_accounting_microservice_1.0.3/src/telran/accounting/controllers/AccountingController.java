package telran.accounting.controllers;

import java.net.URISyntaxException;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import telran.accounting.api.RegistrationDto;
import telran.accounting.api.ResponseDto;
import telran.accounting.domain.dao.AccountsRepository;
import telran.accounting.domain.entities.AccountingRoles;
import telran.accounting.domain.entities.Activ;
import telran.accounting.api.AccountingApiConstants;
import telran.accounting.api.EditUserDto;
import telran.accounting.service.TokenService;
import telran.accounting.service.interfaces.IAccountingManagement;

@RestController
public class AccountingController {

	@Autowired
	TokenService tokenService;

	@Autowired
	IAccountingManagement accounting;
	
	@Autowired
	PasswordEncoder encoder;

	@Autowired
	AccountsRepository repository;

	// "/account/en/v1/registration"
	@PostMapping(value = AccountingApiConstants.REGISTER)
	ResponseDto registerAccount(@RequestBody RegistrationDto accountDto, HttpServletResponse responce) {
		ResponseDto resp = accounting.registerUser(accountDto);
		
		String email = accountDto.email;
		String pass = encoder.encode(accountDto.password);
		HashSet<AccountingRoles> roles = new HashSet<AccountingRoles>();
		roles.add(AccountingRoles.USER);
		
		String token = tokenService.createToken(email, pass, roles);
		if (token != null) {
			responce.addHeader("X-Token", token);
		}	
		return resp;
	}

	// "/account/en/v1/login"
	@PostMapping(value = AccountingApiConstants.LOGIN)
	ResponseDto login(HttpServletRequest request) throws URISyntaxException {
		String auth = request.getHeader("Authorization");
		String[] credentials = tokenService.validateAuth(auth);
		return accounting.loginUser(credentials[0]);
	}

	// "/account/en/v1/{login}/info"
	@GetMapping(value = AccountingApiConstants.USER_INFO)
	ResponseDto getUserInfo(@PathVariable("login") String login) {
		return accounting.getUserInformation(login);
	}

	// "/account/en/v1/1{login}"
	@PutMapping(value = AccountingApiConstants.EDIT_USER)
	ResponseDto editUserProfile(@RequestBody EditUserDto editUserDto, @PathVariable("login") String login) {
		return accounting.editUserProfile(login, editUserDto);
	}

	// "/account/en/v1/{login}"
	@DeleteMapping(value = AccountingApiConstants.REMOVE_USER)
	ResponseDto removeUser(@PathVariable("login") String login, HttpServletRequest request) {
		String xToken = request.getHeader("X-Token");
		return accounting.removeUser(login, xToken);
	}

	// "/account/en/v1/{userLogin}/role/{role}"
	@PutMapping(value = AccountingApiConstants.ADD_USER_ROLE)
	Object[] addUserRole(@PathVariable("userLogin") String userLogin, @PathVariable("role") String role) {
		return accounting.addRole(userLogin, role);
	}

	// "/account/en/v1/{login}/role/{role}"
	@DeleteMapping(value = AccountingApiConstants.DELETE_USER_ROLE)
	Object[] deleteUserRole(@PathVariable("login") String login, @PathVariable("role") String role) {
		return accounting.removeRole(login, role);
	}

	// "/account/en/v1/{userLogin}/block/{status}
	@PutMapping(value = AccountingApiConstants.BLOCK_USER_ACC)
	boolean blockUserAccount(@PathVariable("userLogin") String userLogin, @PathVariable("status") boolean status) {
		return accounting.revokeAccount(userLogin, status);
	}

	// "/account/en/v1/{login}/favorite/{id}"
	@PutMapping(value = AccountingApiConstants.ADD_USER_FAVORITE)
	void addUserFavorite(@PathVariable("login") String login, @PathVariable("id") String id,
			HttpServletRequest request) {
		String xServiceName = request.getHeader("X-ServiceName");
		accounting.addUserFavorite(login, id, xServiceName);
		return;
	}

	// "/account/en/v1/{login}/activity/{postId}"
	@PutMapping(value = AccountingApiConstants.ADD_USER_ACTIVITY)
	void addUserActivity(@PathVariable("login") String login, @PathVariable("postId") String postId,
			HttpServletRequest request) {
		String xServiceName = request.getHeader("X-ServiceName");
		accounting.addUserActivity(login, postId, xServiceName);
		return;
	}

	// "/account/en/v1/{login}/favorite/{id}"
	@DeleteMapping(value = AccountingApiConstants.REMOVE_USER_FAVORITE)
	void removeUserFavorite(@PathVariable("login") String login, @PathVariable("id") String id,
			HttpServletRequest request) {
		String xServiceName = request.getHeader("X-ServiceName");
		accounting.removeUserFavorite(login, id, xServiceName);
		return;
	}

	// "/account/en/v1/{login}/activity/{id}"
	@DeleteMapping(value = AccountingApiConstants.REMOVE_USER_ACTIVITY)
	void removeUserActivity(@PathVariable("login") String login, @PathVariable("id") String id,
			HttpServletRequest request) {
		String xServiceName = request.getHeader("X-ServiceName");
		accounting.removeUserActivity(login, id, xServiceName);
		return;
	}

	// "/account/en/v1/{login}"
	@GetMapping(value = AccountingApiConstants.GET_USER_DATA)
	Activ getUserData(@PathVariable("login") String login, @RequestParam boolean dataType) {
		return accounting.getUserData(login, dataType);
	}

	// "/account/en/v1/token/validation"
	@GetMapping(value = AccountingApiConstants.TOKEN_VALIDATION)
	void tokenValidation(@RequestHeader("X-Token") String token, HttpServletResponse resp) {
		String newToken = accounting.tokenValidation(token);
		resp.addHeader("X-Token", newToken );
		return;
	}

}
