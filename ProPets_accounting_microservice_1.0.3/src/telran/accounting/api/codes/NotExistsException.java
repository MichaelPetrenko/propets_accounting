package telran.accounting.api.codes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "NOT_EXISTS_EXCEPTION")
public class NotExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}
