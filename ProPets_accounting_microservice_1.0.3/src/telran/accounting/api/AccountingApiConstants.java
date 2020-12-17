package telran.accounting.api;

public interface AccountingApiConstants {

	//for initial commit in new branch
	String REGISTER = 				"/en/v1/registration";
	String LOGIN = 					"/en/v1/login";
	String USER_INFO = 				"/en/v1/{login}/info";
	String EDIT_USER= 				"/en/v1/1{login}";
	String REMOVE_USER = 			"/en/v1/{login}";
	String ADD_USER_ROLE = 			"/en/v1/{userLogin}/role/{role}";
	String DELETE_USER_ROLE = 		"/en/v1/{login}/role/{role}";
	String BLOCK_USER_ACC = 		"/en/v1/{userLogin}/block/{status}";
	String ADD_USER_FAVORITE = 		"/en/v1/{login}/favorite/{id}";
	String ADD_USER_ACTIVITY = 		"/en/v1/{login}/activity/{postId}";
	String REMOVE_USER_FAVORITE = 	"/en/v1/{login}/favorite/{id}";
	String REMOVE_USER_ACTIVITY = 	"/en/v1/{login}/activity/{id}";
	String GET_USER_DATA = 			"/en/v1/{login}";
	//String GET_USER_DATA = "/account/en/v1/{login}?dataType=true";
	String TOKEN_VALIDATION = 		"/en/v1/token/validation";
	String WAKEUP = 				"/wakeup";

}
