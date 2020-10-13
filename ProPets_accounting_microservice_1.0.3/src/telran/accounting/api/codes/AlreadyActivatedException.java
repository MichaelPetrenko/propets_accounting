package telran.accounting.api.codes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "ALREADY_ACTIVATED!")
public class AlreadyActivatedException extends RuntimeException {
	private static final long serialVersionUID = 6711144674191253755L;
}
