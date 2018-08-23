package e.facade;

import java.sql.Date;
import java.util.Collection;

import c.javaBeans.Company;
import c.javaBeans.Coupon;
import c.javaBeans.CouponType;
import d.DAO.CompanyDAO;
import d.DAO.CompanyDbDAO;
import d.DAO.CouponDAO;
import d.DAO.CouponDbDAO;
import x.exceptions.ConnectionPoolException;
import x.exceptions.CouponAlreadyExistException;
import x.exceptions.CouponDontExistException;
import x.exceptions.CouponSystemException;
import x.exceptions.DAOException;
import x.exceptions.FacadeException;

public class CompanyFacade implements CouponClientFacade {

	private CompanyDAO compDao = new CompanyDbDAO();
	private CouponDAO couponDao = new CouponDbDAO();
	private Company currentComp = null;

	/**
	 * CTOR
	 * 
	 * @param compName
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public CompanyFacade(String compName) throws ConnectionPoolException, DAOException {
		super();
		this.currentComp = compDao.getCompanyByName(compName);
	}

	// Coupon management:

	/**
	 * this method receives a Coupon object, checks the DB to see if that name is
	 * available and if certain coupon attributes are valid, and if so- creates this
	 * new coupon on the DB.
	 *
	 * @param coupon
	 * @throws FacadeException
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws CouponAlreadyExistException
	 */
	public void createCoupon(Coupon coupon)
			throws FacadeException, ConnectionPoolException, DAOException, CouponAlreadyExistException {
		coupon.setImage(coupon.getImage().trim());
		coupon.setMessage(coupon.getMessage().trim());
		coupon.setTitle(coupon.getTitle().trim());
		// enforcing attribute restrictions
		if (coupon.getTitle() == null) {
			throw new FacadeException("Failed to create coupon: Coupon title cannot be null;");
		}
		if (coupon.getAmount() < 0 || coupon.getPrice() < 0) {
			throw new FacadeException("Failed to create coupon: cannot set a negative number for amount/price;");
		}
		if (coupon.getEndDate().before(coupon.getStartDate())) {
			throw new FacadeException("Failed to create coupon: end date cannot be before start date;");
		}
		// if coupon name is available, create it, and populate the company_coupon DB
		// with this data
		if (couponDao.getCouponByName(coupon.getTitle()) == null) {
			long couponId = couponDao.createCoupon(coupon);
			couponDao.populateCompanyCoupon(currentComp.getId(), couponId);
		} else {
			throw new CouponAlreadyExistException(
					"Failed to create coupon: a Coupon with the name *" + coupon.getTitle() + "* already exists;");
		}

	}

	/**
	 * this method receives a Coupon object, checks the DB to see if that coupon
	 * actually exists. if so, removes this coupon from all relevant DB tables.
	 * 
	 * @param coupon
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws CouponDontExistException
	 */
	public void removeCoupon(Coupon coupon) throws ConnectionPoolException, DAOException, CouponDontExistException {
		if (couponDao.getCoupon(coupon.getId()) != null) {

			// removing from the coupon DB:
			couponDao.removeCoupon(coupon);

			// removing from the company_coupon table:
			couponDao.removeFromCompanyCoupon(coupon.getId());

			// removing every coupon like this a customer already purchased:
			couponDao.removeFromCustomerCoupon(coupon.getId());
		} else {
			throw new CouponDontExistException(
					"Failed to remove Coupon. coupon with id " + coupon.getId() + " cannot be found;");
		}
	}

	/**
	 * this method receives a Coupon object, checks to ensure it's updateable
	 * attributes are valid, and then updates that coupon on the DB by retrieving
	 * the existing coupon, setting's its updatable attributes, and sending it to an
	 * update.
	 * 
	 * @param newCoupon
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public void updateCoupon(Coupon newCoupon) throws ConnectionPoolException, DAOException, FacadeException {
		// bringing the existing coupon from the DB to compare:
		Coupon oldCoupon = couponDao.getCoupon(newCoupon.getId());

		// checking the received coupon doesn't have illegal values:
		if (newCoupon.getEndDate().before(oldCoupon.getStartDate())) {
			throw new FacadeException("Failed to update coupon: Coupon end date cannot be before start date;");
		}
		if (newCoupon.getPrice() < 0) {
			throw new FacadeException("Failed to update coupon: cannot set a negative number for price;");
		}

		// updating existing coupon java bean attributes according to the coupon
		// received.
		oldCoupon.setPrice(newCoupon.getPrice());
		oldCoupon.setEndDate(newCoupon.getEndDate());

		// executing update with an updated price and end-date only.
		couponDao.updateCoupon(oldCoupon);
	}

	/**
	 * this method is used to retrieve one Coupon from the DB, that belongs to this
	 * company facade.
	 * 
	 * @param couponId
	 * @return {@link Coupon}
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws CouponDontExistException
	 */
	public Coupon getSpecificCoupon(long couponId)
			throws ConnectionPoolException, DAOException, CouponDontExistException {
		Coupon c = couponDao.getOneCompanyCoupon(currentComp.getId(), couponId);
		if (c != null) {
			return c;
		} else {
			throw new CouponDontExistException("Failed to get coupon: The coupon on id " + couponId
					+ " either does not exist, or does not belong to company *" + currentComp.getCompName() + "*;");
		}

	}

	/**
	 * this is a utility method used to retrieve a Company object from the DB with
	 * all of it's attributes, after a successful login and facade acquisition.
	 * 
	 * @return {@link Company}
	 * @throws FacadeException
	 */
	public Company getCurrentCompany() throws FacadeException {
		if (currentComp == null) {
			throw new FacadeException(
					"Failed to get current company details: not yet initialized a new Company object (on successful login). value is null;");
		}

		// returning Company
		return currentComp;
	}

	/**
	 * this method is used to get this company's income from the company_income
	 * table. throws an exception if no listing exists, and income <=0.
	 * 
	 * @return {@link Double}
	 * @throws CouponSystemException
	 */
	public double getCompanyIncome() throws CouponSystemException {
		double income = compDao.getCompanyIncome(currentComp);
		if (income > 0) {
			return income;
		} else {
			throw new CouponSystemException("No available income for company *" + currentComp.getCompName() + "*;");
		}
	}

	/**
	 * this method is used to retrieve from the DB all the coupons of this company,
	 * exception if none exist.
	 * 
	 * @return {@link Collection}
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public Collection<Coupon> getAllCompanyCoupons() throws ConnectionPoolException, DAOException, FacadeException {
		Collection<Coupon> list = compDao.getCoupons(currentComp);
		if (!list.isEmpty()) {
			return list;
		} else {
			throw new FacadeException("Failed while trying to get Coupons: no Coupons exist for company *"
					+ currentComp.getCompName() + "*;");
		}

	}

	/**
	 * this method is used to retrieve from the DB all the coupons of certain type
	 * of this company, exception if none exist.
	 * 
	 * @param type
	 * @return {@link Collection}
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public Collection<Coupon> getCouponsByType(CouponType type)
			throws ConnectionPoolException, DAOException, FacadeException {
		Collection<Coupon> list = compDao.getCouponsByType(currentComp, type);
		if (!list.isEmpty()) {
			return list;
		} else {
			throw new FacadeException("Failed while trying to get Coupons: no Coupons of type *" + type.toString()
					+ "* exist for company *" + currentComp.getCompName() + "*;");
		}

	}

	/**
	 * this method is used to retrieve from the DB all the coupons up to a certain
	 * date of this company, exception if none exist.
	 * 
	 * @param endDate
	 * @return {@link Collection}
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public Collection<Coupon> getCouponsUpToDate(Date endDate)
			throws ConnectionPoolException, DAOException, FacadeException {
		Collection<Coupon> list = compDao.getCouponsUpToDate(currentComp, endDate);
		if (!list.isEmpty()) {
			return list;
		} else {
			throw new FacadeException("Failed while trying to get Coupons: no Coupons up to date " + endDate
					+ " exist for company *" + currentComp.getCompName() + "*;");
		}

	}

	/**
	 * this method is used to retrieve from the DB all the coupons up to a certain
	 * price of this company, exception if none exist.
	 * 
	 * @param price
	 * @return {@link Collection}
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public Collection<Coupon> getCouponsByPrice(double price)
			throws ConnectionPoolException, DAOException, FacadeException {
		Collection<Coupon> list = compDao.getCouponsByPrice(currentComp, price);
		if (!list.isEmpty()) {
			return list;
		} else {
			throw new FacadeException("Failed while trying to get Coupons: no Coupons up to price " + price
					+ " exist for company *" + currentComp.getCompName() + "*;");
		}

	}

}
