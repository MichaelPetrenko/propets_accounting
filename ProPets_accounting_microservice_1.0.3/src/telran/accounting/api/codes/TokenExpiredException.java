package telran.accounting.api.codes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Token expired!")
public class TokenExpiredException extends RuntimeException {
	private static final long serialVersionUID = -2502105220680767412L;	
}
