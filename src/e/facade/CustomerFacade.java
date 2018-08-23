package e.facade;

import java.util.Collection;
import java.util.Date;

import c.javaBeans.Coupon;
import c.javaBeans.CouponType;
import c.javaBeans.Customer;
import d.DAO.CompanyDAO;
import d.DAO.CompanyDbDAO;
import d.DAO.CouponDAO;
import d.DAO.CouponDbDAO;
import d.DAO.CustomerDAO;
import d.DAO.CustomerDbDAO;
import x.exceptions.ConnectionPoolException;
import x.exceptions.DAOException;
import x.exceptions.FacadeException;

public class CustomerFacade implements CouponClientFacade {

	private CustomerDAO custDao = new CustomerDbDAO();
	private CouponDAO couponDao = new CouponDbDAO();
	private CompanyDAO compDao = new CompanyDbDAO();
	private Customer currentCust = null;

	/**
	 * CTOR
	 * 
	 * @param custName
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public CustomerFacade(String custName) throws ConnectionPoolException, DAOException {
		super();
		this.currentCust = custDao.getCustomerByName(custName);
	}

	/**
	 * this is a utility method used to retrieve a Customer object from the DB with
	 * all of it's attributes, after a successful login and facade acquisition.
	 * 
	 * @return {@link Customer}
	 * @throws FacadeException
	 */
	public Customer getCurrentCustomer() throws FacadeException {
		if (currentCust == null) {
			throw new FacadeException(
					"Failed to get current customer details: not yet initialized a new Customer object (on successful login). value is null;");
		}

		// returning Company
		return currentCust;
	}

	// Coupon management:

	/**
	 * this method is used to purchase a coupon. gets a Coupon object as parameter,
	 * check it exists on the DB, and this customer didn't already purchase it in
	 * the past. also makes sure there is at least 1 coupon left for purchase, and
	 * it is not expired. if so- adds this coupon to this customer, and updates both
	 * it's amount and the income for the company who sold it.
	 * 
	 * @param coupon
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public void purchaseCoupon(Coupon coupon) throws ConnectionPoolException, DAOException, FacadeException {
		// get full coupon details from the DB:
		coupon = couponDao.getCoupon(coupon.getId());

		if (coupon == null) {
			throw new FacadeException("The coupon you are trying to purchase does not exist");
		}

		// check this customer doesn't already have this coupon:
		if (custDao.getOnePurchasedCoupon(currentCust, coupon) != null) {
			throw new FacadeException("Failed to purchase coupon: Customer *" + currentCust.getCustName()
					+ "* already purchased *" + coupon.getTitle() + "* coupon;");
		}
		// check there are coupons left for purchase:
		if (coupon.getAmount() == 0) {
			throw new FacadeException("Failed to purchase coupon: *" + coupon.getTitle() + "* coupon amount is 0;");
		}
		// just making sure the coupon has not expired and for some reason WAS NOT
		// removed by the daily expired task:
		if (coupon.getEndDate().before(new Date())) {
			throw new FacadeException("Failed to purchase coupon: *" + coupon.getTitle() + "* coupon expired;");
		}

		// updating coupon amount on the DB:
		coupon.setAmount(coupon.getAmount() - 1);
		couponDao.updateCoupon(coupon);

		// purchase = update the customer_coupon JOIN table:
		couponDao.populateCustomerCoupon(currentCust.getId(), coupon.getId());

		// update company_income with purchased coupon:
		compDao.updateCompanyIncome(coupon);

	}

	/**
	 * this method is used to get all existing coupons purchased by this client, or
	 * an exception if no coupons exist.
	 * 
	 * @return {@link Collection}
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public Collection<Coupon> getAllCustomerCoupon() throws ConnectionPoolException, DAOException, FacadeException {
		Collection<Coupon> list = custDao.getCoupons(currentCust);
		if (!list.isEmpty()) {
			return list;
		} else {
			throw new FacadeException("Failed while trying to get Coupons: no Coupons exist for customer *"
					+ currentCust.getCustName() + "*;");
		}

	}

	/**
	 * this method is used to get all existing coupons of certain type purchased by
	 * this client, or an exception if no coupons exist.
	 * 
	 * @param type
	 * @return {@link Collection}
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public Collection<Coupon> getCouponsByType(CouponType type)
			throws ConnectionPoolException, DAOException, FacadeException {
		Collection<Coupon> list = custDao.getCouponsByType(currentCust, type);
		if (!list.isEmpty()) {
			return list;
		} else {
			throw new FacadeException("Failed while trying to get Coupons: no Coupons of type *" + type.toString()
					+ "* exist for customer *" + currentCust.getCustName() + "*;");
		}

	}

	/**
	 * this method is used to get all existing coupons up to a certain price
	 * purchased by this client, or an exception if no coupons exist.
	 * 
	 * @param price
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public Collection<Coupon> getCouponsByPrice(double price)
			throws ConnectionPoolException, DAOException, FacadeException {
		Collection<Coupon> list = custDao.getCouponsByPrice(currentCust, price);
		if (!list.isEmpty()) {
			return list;
		} else {
			throw new FacadeException("Failed while trying to get Coupons: no Coupons up to price " + price
					+ " exist for customer *" + currentCust.getCustName() + "*;");
		}

	}

	public Collection<Coupon> getAllCoupons() throws ConnectionPoolException, DAOException, FacadeException {
		Collection<Coupon> list = couponDao.getAllCoupons();
		if (!list.isEmpty()) {
			return list;
		} else {
			throw new FacadeException("Failed while trying to get Coupons: no Coupons seem to exist in the system");
		}

	}

}
