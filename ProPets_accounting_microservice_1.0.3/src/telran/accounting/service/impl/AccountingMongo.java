package telran.accounting.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import telran.accounting.api.RegistrationDto;
import telran.accounting.api.ResponceMessagingDto;
import telran.accounting.api.ResponseDto;
import telran.accounting.api.ResponseLostFoundPostDto;
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
import telran.accounting.api.codes.BadRequestException;
import telran.accounting.api.codes.BadTokenException;
import telran.accounting.api.codes.BadURIException;

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
		if (!checkRegistrationDto(registrationDto)) {
			throw new NoContentException();
		}

		if (repository.existsById(registrationDto.email)) {
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
		if (regDto.email == null) {
			return false;
		}
		if (!regDto.email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
			return false;
		}
		if (regDto.password == null || regDto.password == "") {
			return false;
		}
		if (!regDto.password.matches("\\S{5,}")) {
			return false;
		}
		if (regDto.name == null || regDto.name == "") {
			return false;
		}
		if (!regDto.name.matches("[A-Za-z0-9]+ [A-Za-z0-9]+")) {
			return false;
		}
		return true;
	}

	@Override
	public ResponseDto loginUser(String login) {
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

		if (!checkEditUserDto(editUserDto)) {
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
		if (!editUserDto.name.matches("[A-Za-z0-9]+ [A-Za-z0-9]+")) {
			return false;
		}
		if (!editUserDto.phone.matches("\\+?[0-9]+")) {
			return false;
		}
		if (editUserDto.avatar.length() == 0) {
			editUserDto.avatar = "http://gravatar.com/avatar/0?d=mp";
		}
		return true;
	}

	@Override
	public ResponseDto removeUser(String email, String xToken) {

		AccountEntity user = repository.findById(email).orElse(null);
		if (user == null) {
			throw new NotExistsException();
		}

		// =======================
		HashSet<String> messages = user.getActivities().getMessage();
		HashSet<String> lostfounds = user.getActivities().getLostFound();

		// Removing messages
		if (messages.size() > 0) {
			messages.forEach(m -> {
				try {
					deleteMessagesByUser(m.toString(), xToken);
				} catch (Exception e) {
					e.getStackTrace();
					if (e instanceof Forbidden) {
						throw new ForbiddenException();
					} else if (e instanceof Unauthorized) {
						throw new BadTokenException();
					} else if (e instanceof BadRequest) {
						throw new BadRequestException();
					} else
					throw new NotExistsException();
				}
			});
		}

		// Removing lostfounds
//		if (lostfounds.size() > 0) {
//			lostfounds.forEach(m -> {
//				
//				URI uri = null;
//				try {
//					uri = new URI("http://propets-lfs.herokuapp.com/en/v1/delete/" + m.toString());
//				} catch (URISyntaxException e) {
//					e.printStackTrace();
//				}
//				HttpHeaders headers = new HttpHeaders();
//				headers.setContentType(MediaType.APPLICATION_JSON);
//				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//				String xToken = "eyJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6InZhc3lhbkBnbWFpbC5jb20iLCJwYXNzd29yZCI6IiQyYSQxMCRKWUM2WW9tSzdzUzJLTUtJLzBQMS4uWGFhaGtJZDdnMEtsdEZmQUdoekc3ZW5BUkZHczhTVyIsInRpbWVzdGFtcCI6MTYxMDgyOTYzNzA0NCwicm9sZSI6WyJVU0VSIl19.FQpRqGByEUZBaEUveGsf3QD2CMmGBparhSFFgeWIAO4";
//				headers.set("X-Token", xToken);
//				headers.set("X-ServiceName", "lostFound");
//
//				HttpEntity<Void> request = new HttpEntity<>(headers);
//				restTemplate.exchange(uri, HttpMethod.DELETE, null, ResponseLostFoundPostDto.class);
//			});
//		}

		// =======================
		repository.deleteById(email);
		ResponseDto responseDto = new ResponseDto(user.getEmail(), user.getName(), user.getAvatar(), user.getPhone(),
				user.getRoles());
		return responseDto;
	}

	private void deleteMessagesByUser(String m, String xToken) {
		
		String endPointDeleteMessage = "http://propets-mes.herokuapp.com/en/v1/" + m;
		
		URI uri = null;
		try {
			uri = new URI(endPointDeleteMessage);
		} catch (Exception e) {
			System.out.println("Error URI");
			throw new BadURIException();
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//		String xToken = "eyJhbGciOiJIUzI1NiJ9."
//				+ "eyJsb2dpbiI6InZhc3lhbkBnbWFpbC5jb20iLCJwYXNzd29yZCI6IiQyYSQxMCRjOTVFLi52"
//				+ "cTg3VDNKbzl6dUpud21lSGJjUEswSEVqV3R2VndHbkdNU1RLMDU4b003MlJyUyIsInRpbWVz"
//				+ "dGFtcCI6MTYxMDg5NzkzOTg0MSwicm9sZSI6WyJVU0VSIl19.8co-xCzGDxqZ3oGgvDoVlrm7uzjNkeTgO-lYSFx4DD0";
		headers.set("X-Token", xToken);

		HttpEntity<Void> request = new HttpEntity<>(headers);
		@SuppressWarnings("unused")
		ResponseEntity<Void> responceFromDeletedPosts = restTemplate.exchange(uri, HttpMethod.DELETE, request,
				Void.class);

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
