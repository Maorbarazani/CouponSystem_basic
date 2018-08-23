package x.exceptions;

public class CompanyAlreadyExistException extends CouponSystemException {

	private static final long serialVersionUID = 1L;

	public CompanyAlreadyExistException() {
		super();
	}

	public CompanyAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompanyAlreadyExistException(String message) {
		super(message);
	}

}
