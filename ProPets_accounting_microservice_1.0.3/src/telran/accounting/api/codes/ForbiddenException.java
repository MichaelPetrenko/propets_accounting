package telran.accounting.api.codes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "FORBIDDEN!")
public class ForbiddenException extends RuntimeException {
	private static final long serialVersionUID = -656908856202980740L;	
}
