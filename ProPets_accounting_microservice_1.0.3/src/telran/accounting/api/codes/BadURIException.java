package telran.accounting.api.codes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.URI_TOO_LONG, reason = "URI may not contains whitespacing characters")
public class BadURIException extends RuntimeException {

	private static final long serialVersionUID = -3097699679878014716L;
	String customMessage = "URI may not contains whitespacing characters";
	
	public BadURIException() {
		super();
	}
	
	
	
}
