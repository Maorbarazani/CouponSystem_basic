package x.exceptions;

public class CustomerDontExistException extends CouponSystemException {

	private static final long serialVersionUID = 1L;

	public CustomerDontExistException() {
		super();
	}

	public CustomerDontExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public CustomerDontExistException(String message) {
		super(message);
	}

}
