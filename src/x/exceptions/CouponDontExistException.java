package x.exceptions;

public class CouponDontExistException extends CouponSystemException {

	private static final long serialVersionUID = 1L;

	public CouponDontExistException() {
		super();
	}

	public CouponDontExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouponDontExistException(String message) {
		super(message);
	}

}
