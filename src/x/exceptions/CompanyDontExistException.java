package x.exceptions;

public class CompanyDontExistException extends CouponSystemException {

	private static final long serialVersionUID = 1L;

	public CompanyDontExistException() {
		super();
	}

	public CompanyDontExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompanyDontExistException(String message) {
		super(message);
	}

}
