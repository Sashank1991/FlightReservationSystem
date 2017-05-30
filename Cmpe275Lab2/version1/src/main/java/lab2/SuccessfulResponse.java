package lab2;

import org.springframework.stereotype.Component;

import lab2.Services.Success;

@Component
public class SuccessfulResponse {

	public SuccessResponse Response;

	public SuccessfulResponse(Success succ) {
		Response = new SuccessResponse();
		this.Response.setCode(succ.getCode());
		this.Response.setMessage(succ.getMessage());
	}

}

class SuccessResponse {
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
