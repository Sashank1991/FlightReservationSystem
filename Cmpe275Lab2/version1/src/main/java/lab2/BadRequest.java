package lab2;

import org.springframework.stereotype.Component;

import lab2.Services.customException;

@Component
public class BadRequest {
	public Response BadRequest;

	public BadRequest(customException ex) {
		BadRequest = new Response();
		this.BadRequest.setCode(ex.getCode());
		this.BadRequest.setMessage(ex.getMessage());
	}
}

class Response {
	private int code;
	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
