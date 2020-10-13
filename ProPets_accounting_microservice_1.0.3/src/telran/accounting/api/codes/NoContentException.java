package telran.accounting.api.codes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "No one or more fields")
public class NoContentException extends RuntimeException {
	private static final long serialVersionUID = 4586423466553151700L;
	String customMessage = "";
	
	public NoContentException(String customMessage) {
		super();
		this.customMessage = customMessage;
	}
	public NoContentException() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
