package telran.accounting.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import telran.accounting.api.RegistrationDto;
import telran.accounting.api.ResponseDto;
import telran.accounting.api.codes.AlreadyActivatedException;
import telran.accounting.api.codes.AlreadyExistsException;
import telran.accounting.api.codes.AlreadyRevokedException;
import telran.accounting.api.codes.ForbiddenException;
import telran.accounting.api.codes.NoContentException;
import telran.accounting.api.codes.NotExistsException;
import telran.accounting.api.EditUserDto;
import telran.accounting.domain.dao.AccountsRepository;
import telran.accounting.domain.entities.AccountEntity;
import telran.accounting.domain.entities.AccountingRoles;
import telran.accounting.domain.entities.Activ;
import telran.accounting.service.TokenService;
import telran.accounting.service.interfaces.IAccountingManagement;

@Service
public class AccountingMongo implements IAccountingManagement {

	@Autowired
	AccountsRepository repository;

	@Autowired
	TokenService tokenService;

	@Autowired
	PasswordEncoder encoder; // Создается в классе SecurityConfiguration
	
	@Autowired
	RestTemplate restTemplate;

	@Override
	public ResponseDto registerUser(RegistrationDto registrationDto) {
		if(!checkRegistrationDto(registrationDto)) {
			throw new NoContentException();
		}

		if(repository.existsById(registrationDto.email)) {
			throw new AlreadyExistsException();
		}
		
		String pass = encoder.encode(registrationDto.password);
		AccountEntity newUser = new AccountEntity(registrationDto.email, registrationDto.name, pass);

		repository.save(newUser);
		ResponseDto responseDto = new ResponseDto(newUser.getEmail(), newUser.getName(), newUser.getAvatar(),
				newUser.getPhone(), newUser.getRoles());

		return responseDto;
	}
	
	private boolean checkRegistrationDto(RegistrationDto regDto) {
		if(regDto.email==null) {
			return false;
		}
		if(!regDto.email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
			return false;
		}	
		if(regDto.password == null || regDto.password == "") {
			return false;
		}
		if(!regDto.password.matches("\\S{5,}")) {
			return false;
		}
		if(regDto.name == null || regDto.name == "") {
			return false;
		}
		if(!regDto.name.matches("[A-Za-z0-9]+ [A-Za-z0-9]+")) {
			return false;
		}
		return true;
	}

	@Override
	public ResponseDto loginUser(String login) throws URISyntaxException {
		if (login == null) {
			throw new NoContentException();
		}
		AccountEntity user = repository.findById(login).orElse(null);
		if (user == null) {
			throw new NotExistsException();
		}
		return new ResponseDto(user.getEmail(), user.getName(), user.getAvatar(), user.getPhone(), user.getRoles());
	}

	@Override
	public ResponseDto getUserInformation(String email) {
		AccountEntity user = repository.findById(email).orElse(null);
		if (user != null) {
			ResponseDto responseDto = new ResponseDto(user.getEmail(), user.getName(), user.getAvatar(),
					user.getPhone(), user.getRoles());
			return responseDto;
		}
		throw new NotExistsException();
	}

	@Override
	public ResponseDto editUserProfile(String email, EditUserDto editUserDto) {
		if (email == null || email == "") {
			throw new NotExistsException();
		}
		AccountEntity user = repository.findById(email).orElse(null);
		if (user == null) {
			throw new NotExistsException();
		}
		
		if(!checkEditUserDto(editUserDto)) {
			throw new NoContentException();
		}

		if (user.getAvatar() != editUserDto.avatar) {
			user.setAvatar(editUserDto.avatar);
		}

		if (user.getName() != editUserDto.name) {
			user.setName(editUserDto.name);
		}

		if (user.getPhone() != editUserDto.phone) {
			user.setPhone(editUserDto.phone);
		}

		repository.save(user);
		ResponseDto responseDto = new ResponseDto(user.getEmail(), user.getName(), user.getAvatar(), user.getPhone(),
				user.getRoles());
		return responseDto;
	}
	
	private boolean checkEditUserDto(EditUserDto editUserDto) {
		if(!editUserDto.name.matches("[A-Za-z0-9]+ [A-Za-z0-9]+")) {
			return false;
		}
		if(!editUserDto.phone.matches("\\+?[0-9]+")) {
			return false;
		}
		if(editUserDto.avatar.length()==0) {
			editUserDto.avatar = "http://gravatar.com/avatar/0?d=mp";
		}		
		return true;
	}

	@Override
	public ResponseDto removeUser(String email) {
		AccountEntity user = repository.findById(email).orElse(null);
		if (user == null) {
			throw new NotExistsException();
		}
		repository.deleteById(email);
		ResponseDto responseDto = new ResponseDto(user.getEmail(), user.getName(), user.getAvatar(), user.getPhone(),
				user.getRoles());
		//TODO Need to remove all of posts of this removed user.
		return responseDto;
	}

	@Override
	public boolean revokeAccount(String email, boolean status) {
		AccountEntity user = repository.findById(email).orElse(null);
		if (user == null) {
			throw new NotExistsException();
		}
		if (user.isRevoked() == true && status == true) {
			throw new AlreadyRevokedException();
		}
		if (user.isRevoked() == false && status == false) {
			throw new AlreadyActivatedException();
		}
		user.setRevoked(status);
		repository.save(user);
		return status;
	}

	@Override
	public Object[] addRole(String email, String role) {

		if (role == null || role == "") {
			throw new NoContentException();
		}

		boolean flag = false;
		for (AccountingRoles r : AccountingRoles.values()) {
			if (r.toString().equals(role)) {
				flag = true;
			}
		}

		if (flag == false) {
			throw new NotExistsException();
		}

		AccountEntity user = repository.findById(email).orElse(null);

		if (user == null) {
			throw new NotExistsException();
		}

		HashSet<AccountingRoles> roles = user.getRoles();

		boolean res = roles.add(AccountingRoles.valueOf(role));
		if (res) {
			repository.save(user);
			return roles.toArray();
		}

		throw new AlreadyExistsException();
	}

	@Override
	public Object[] removeRole(String email, String role) {

		if (role == null || role == "") {
			throw new NoContentException();
		}

		boolean flag = false;
		for (AccountingRoles r : AccountingRoles.values()) {
			if (r.toString().equals(role)) {
				flag = true;
			}
		}

		if (flag == false) {
			throw new NotExistsException();
		}

		if (role.equals(AccountingRoles.USER.toString())) {
			throw new ForbiddenException();
		}

		AccountEntity user = repository.findById(email).orElse(null);

		if (user == null) {
			throw new NotExistsException();
		}

		HashSet<AccountingRoles> roles = user.getRoles();
		boolean res = roles.remove(AccountingRoles.valueOf(role));
		if (res) {
			repository.save(user);
			return roles.toArray();
		}

		throw new NotExistsException();
	}

	@Override
	public void addUserFavorite(String email, String postID, String xServiceName) {

		if (postID == null) {
			throw new NotExistsException();
		}
		AccountEntity user = repository.findById(email).orElse(null);

		if (user == null) {
			throw new NotExistsException();
		}
		Activ favorites = user.getFavorites();

		boolean res = false;
		if (xServiceName.equalsIgnoreCase("message")) {
			res = favorites.getMessage().add(postID);
		} else if (xServiceName.equalsIgnoreCase("lostFound")) {
			res = favorites.getLostFound().add(postID);
		} else if (xServiceName.equalsIgnoreCase("hotels")) {
			res = favorites.getHotels().add(postID);
		} else {
			throw new NoContentException("Error form");
		}

		if (res) {
			repository.save(user);
			return;
		} else {
			throw new AlreadyExistsException();
		}
	}

	@Override
	public void addUserActivity(String email, String postID, String xServiceName) {

		if (postID == null) {
			throw new NotExistsException();
		}
		AccountEntity user = repository.findById(email).orElse(null);

		if (user == null) {
			throw new NotExistsException();
		}
		Activ activities = user.getActivities();

		boolean res = false;
		if (xServiceName.equalsIgnoreCase("message")) {
			res = activities.getMessage().add(postID);
		} else if (xServiceName.equalsIgnoreCase("lostFound")) {
			res = activities.getLostFound().add(postID);
		} else if (xServiceName.equalsIgnoreCase("hotels")) {
			res = activities.getHotels().add(postID);
		} else {
			throw new NoContentException();
		}

		if (res) {
			repository.save(user);
			return;
		} else {
			throw new AlreadyExistsException();
		}
	}

	@Override
	public void removeUserFavorite(String email, String postID, String xServiceName) {

		if (postID == null) {
			throw new NotExistsException();
		}
		AccountEntity user = repository.findById(email).orElse(null);

		if (user == null) {
			throw new NotExistsException();
		}
		Activ favorites = user.getFavorites();

		boolean res = false;
		if (xServiceName.equalsIgnoreCase("message")) {
			res = favorites.getMessage().remove(postID);
		} else if (xServiceName.equalsIgnoreCase("lostFound")) {
			res = favorites.getLostFound().remove(postID);
		} else if (xServiceName.equalsIgnoreCase("hotels")) {
			res = favorites.getHotels().remove(postID);
		} else {
			throw new NoContentException();
		}

		if (res) {
			repository.save(user);
			return;
		} else {
			throw new NotExistsException();
		}

	}

	@Override
	public void removeUserActivity(String email, String postID, String xServiceName) {

		if (postID == null) {
			throw new NotExistsException();
		}
		AccountEntity user = repository.findById(email).orElse(null);

		if (user == null) {
			throw new NotExistsException();
		}
		Activ activities = user.getActivities();

		boolean res = false;
		if (xServiceName.equalsIgnoreCase("message")) {
			res = activities.getMessage().remove(postID);
		} else if (xServiceName.equalsIgnoreCase("lostFound")) {
			res = activities.getLostFound().remove(postID);
		} else if (xServiceName.equalsIgnoreCase("hotels")) {
			res = activities.getHotels().remove(postID);
		} else {
			throw new NoContentException();
		}

		if (res) {
			repository.save(user);
			return;
		} else {
			throw new NotExistsException();
		}

	}

	@Override
	public Activ getUserData(String email, boolean dataType) {

		AccountEntity user = repository.findById(email).orElse(null);

		if (user == null) {
			throw new NotExistsException();
		}

		if (dataType != true && dataType != false) {
			throw new NoContentException();
		}

		if (dataType) {
			Activ activities = user.getActivities();
			return activities;
		} else {
			Activ favorites = user.getFavorites();
			return favorites;
		}

	}

	@Override
	public String tokenValidation(String oldToken) {
		String[] credentials = tokenService.decompileToken(oldToken);
		AccountEntity user = repository.findById(credentials[0]).orElse(null);
		if (user == null) {
			throw new NotExistsException();
		}
		if (!credentials[1].equals(user.getPass())) {
			throw new ForbiddenException();
		}

		return tokenService.createToken(user);
	}
	
}
