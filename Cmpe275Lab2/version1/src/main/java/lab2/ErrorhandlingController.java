package lab2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lab2.Services.customException;

@ControllerAdvice
public class ErrorhandlingController {
	@ExceptionHandler(customException.class)
	public ResponseEntity<BadRequest> customException(customException ex) throws Exception {
		BadRequest er = new BadRequest(ex);
		return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);

	}

}
