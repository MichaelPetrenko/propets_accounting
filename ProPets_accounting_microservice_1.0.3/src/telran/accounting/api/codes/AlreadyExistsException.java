package telran.accounting.api.codes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "ALREADY_EXISTS!")
public class AlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = -4944549262480884350L;
}
