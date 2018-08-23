package f.system;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

import d.DAO.CouponDAO;
import d.DAO.CouponDbDAO;
import x.exceptions.ConnectionPoolException;
import x.exceptions.DAOException;

public class DailyCouponExpirationTask implements Runnable {

	private CouponDAO couponDao;
	private boolean quit = false;

	/**
	 * CTOR that also initializes the DAO.
	 * 
	 * @throws ConnectionPoolException
	 */
	public DailyCouponExpirationTask() throws ConnectionPoolException {
		super();
		this.couponDao = new CouponDbDAO();
	}

	/**
	 * this method describes the thread actions: on every 24hour iteration the
	 * 'today' date is being updated, and then used in conjunction with 3 different
	 * DAO methods to find and remove expired coupons from wherever they appear.
	 */
	@Override
	public void run() {
		while (!quit) {
			try {
				// establishing the current date in SQL format for easy DB implementation
				Date today = Date.valueOf(LocalDate.now());
				System.out.println("\n### The *daily coupon expiration task* has started running on: " + today + ", "
						+ LocalTime.now() + " ###");

				// removing expired coupons from the company_coupon JOIN table:
				try {
					couponDao.removeExpiredFromCompanyCoupon(today);
				} catch (ConnectionPoolException | DAOException e) {
					System.err.println(e.getMessage());
				}

				// removing expired coupons from the customer_coupon JOIN table:
				try {
					couponDao.removeExpiredFromCustomerCoupon(today);
				} catch (ConnectionPoolException | DAOException e) {
					System.err.println(e.getMessage());
				}

				// removing expired coupons from the coupon table:
				try {
					couponDao.removeExpiredFromCoupon(today);
				} catch (ConnectionPoolException | DAOException e2) {
					System.err.println(e2.getMessage());
				}

				Thread.sleep(86400000);
			} catch (InterruptedException e) {
				System.err.println("The DailyCouponExpirationTask 'wait timer' was interrupted;");
				break;
			}
		}
		System.out.println("\n## the DailyCouponExpirationTask has been terminated; ##");
	}

	/**
	 * this method acts as a stop mechanism for the daily task run method by
	 * changing the value of the expression in the while loop condition to
	 * quit=true;
	 */
	public void stopTask() {
		if (this.quit = false) {
			this.quit = true;
		}
	}

}
