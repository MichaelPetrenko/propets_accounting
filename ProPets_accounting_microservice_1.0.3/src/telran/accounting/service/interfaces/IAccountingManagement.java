package telran.accounting.service.interfaces;

import telran.accounting.api.RegistrationDto;
import telran.accounting.api.ResponseDto;
import telran.accounting.domain.entities.Activ;

import java.net.URISyntaxException;

import telran.accounting.api.EditUserDto;

public interface IAccountingManagement {
	
	ResponseDto registerUser(RegistrationDto registrationDto);							//DONE TESTED FINAL ret
	ResponseDto loginUser(String login) throws URISyntaxException; 												//DONE TESTED FINAL ret & Auth
	ResponseDto getUserInformation(String email);										//DONE TESTED FINAL get ret - NOW ONLY USER GETS INFO
	ResponseDto editUserProfile(String email, EditUserDto editUserDto);					//DONE TESTED FINAL get ret
	ResponseDto removeUser(String email);												//DONE TESTED FINAL ret
	boolean revokeAccount(String email, boolean status);								//DONE TESTED FINAL ret
	Object[] addRole(String email, String role);										//DONE TESTED FINAL get ret
	Object[] removeRole(String email, String role);										//DONE TESTED FINAL get ret
	void addUserFavorite(String email, String postID, String xServiceName); 			//DONE TESTED FINAL get ret
	void addUserActivity(String email, String postID, String xServiceName); 			//DONE TESTED FINAL get ret
	void removeUserFavorite(String email, String postID, String xServiceName);			//DONE TESTED FINAL get ret
	void removeUserActivity(String email, String postID, String xServiceName);			//DONE TESTED FINAL get ret
	Activ getUserData(String email, boolean dataType); 									//DONE TESTED FINAL get ret
	String tokenValidation(String oldToken); 											//DONE TESTED FINAL get ret
}
