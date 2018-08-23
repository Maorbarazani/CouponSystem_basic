package x.exceptions;

public class CouponAlreadyExistException extends CouponSystemException {

	private static final long serialVersionUID = 1L;

	public CouponAlreadyExistException() {
		super();
	}

	public CouponAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouponAlreadyExistException(String message) {
		super(message);
	}

}
