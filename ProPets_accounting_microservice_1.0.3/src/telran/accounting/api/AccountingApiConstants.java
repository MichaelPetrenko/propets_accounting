package telran.accounting.api;

public interface AccountingApiConstants {

	String REGISTER = 				"/account/en/v1/registration";
	String LOGIN = 					"/account/en/v1/login";
	String USER_INFO = 				"/account/en/v1/{login}/info";
	String EDIT_USER= 				"/account/en/v1/1{login}";
	String REMOVE_USER = 			"/account/en/v1/{login}";
	String ADD_USER_ROLE = 			"/account/en/v1/{userLogin}/role/{role}";
	String DELETE_USER_ROLE = 		"/account/en/v1/{login}/role/{role}";
	String BLOCK_USER_ACC = 		"/account/en/v1/{userLogin}/block/{status}";
	String ADD_USER_FAVORITE = 		"/account/en/v1/{login}/favorite/{id}";
	String ADD_USER_ACTIVITY = 		"/account/en/v1/{login}/activity/{postId}";
	String REMOVE_USER_FAVORITE = 	"/account/en/v1/{login}/favorite/{id}";
	String REMOVE_USER_ACTIVITY = 	"/account/en/v1/{login}/activity/{id}";
	String GET_USER_DATA = 			"/account/en/v1/{login}";
	//String GET_USER_DATA = "/account/en/v1/{login}?dataType=true";
	String TOKEN_VALIDATION = 		"/account/en/v1/token/validation";

}
