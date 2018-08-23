package x.exceptions;

public class ConnectionPoolException extends CouponSystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionPoolException() {
		super();
	}

	public ConnectionPoolException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionPoolException(String message) {
		super(message);
	}

}
