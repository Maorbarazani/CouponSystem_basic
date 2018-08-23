package x.exceptions;

public class CustomerAlreadyExistException extends CouponSystemException {

	private static final long serialVersionUID = 1L;

	public CustomerAlreadyExistException() {
		super();
	}

	public CustomerAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public CustomerAlreadyExistException(String message) {
		super(message);
	}

}
