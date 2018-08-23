package x.exceptions;

public class FacadeException extends CouponSystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FacadeException() {
		super();
	}

	public FacadeException(String message, Throwable cause) {
		super(message, cause);
	}

	public FacadeException(String message) {
		super(message);
	}

}
