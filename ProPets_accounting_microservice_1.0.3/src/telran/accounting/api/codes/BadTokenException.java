package telran.accounting.api.codes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Bad token request!")
public class BadTokenException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4293337278414777495L;

}
