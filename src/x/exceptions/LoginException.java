package x.exceptions;

public class LoginException extends CouponSystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginException() {
		super();
	}

	public LoginException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoginException(String message) {
		super(message);
	}

}
