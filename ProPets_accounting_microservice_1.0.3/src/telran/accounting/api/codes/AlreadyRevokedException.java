package telran.accounting.api.codes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "ALREADY_REVOKED!")
public class AlreadyRevokedException extends RuntimeException {
	private static final long serialVersionUID = -7612273450899607358L;
}
