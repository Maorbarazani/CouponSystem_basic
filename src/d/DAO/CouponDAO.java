package d.DAO;

import java.sql.Date;
import java.util.Collection;

import c.javaBeans.Company;
import c.javaBeans.Coupon;
import c.javaBeans.CouponType;
import c.javaBeans.Customer;
import e.facade.CompanyFacade;
import f.system.DailyCouponExpirationTask;
import x.exceptions.ConnectionPoolException;
import x.exceptions.CouponSystemException;
import x.exceptions.DAOException;

public interface CouponDAO {

	/**
	 * create a coupon on the DB. this is also used to get the automatically
	 * generated coupon ID from the SQL DB, and assign it to this coupon Java bean.
	 * 
	 * @param coupon
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public long createCoupon(Coupon coupon) throws ConnectionPoolException, DAOException;

	/**
	 * remove a coupon from the DB using it's DB id.
	 * 
	 * @param coupon
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public void removeCoupon(Coupon coupon) throws ConnectionPoolException, DAOException;

	/**
	 * update a coupon (everything BUT Id) on the DB
	 * 
	 * @param coupon
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public void updateCoupon(Coupon coupon) throws ConnectionPoolException, DAOException;

	/**
	 * this method returns from the DB 1 coupon details with the specified couponID,
	 * creates a new coupon java bean, sets all of the parameters according to the
	 * DB information retrieved, and returns that coupon.
	 * 
	 * @param couponId
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public Coupon getCoupon(long couponId) throws ConnectionPoolException, DAOException;

	/**
	 * this method returns from the DB 1 coupon which belongs to a specified
	 * company. used from the {@link CompanyFacade} to get one specific coupon of
	 * that company.
	 * 
	 * @param compId
	 * @param couponId
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	Coupon getOneCompanyCoupon(long compId, long couponId) throws ConnectionPoolException, DAOException;

	/**
	 * this method gets all the coupons from the DB, creates a java bean for each
	 * one and adds them to an ArrayList collection. returns that collection.
	 * 
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public Collection<Coupon> getAllCoupons() throws ConnectionPoolException, DAOException;

	/**
	 * this method gets all the coupons OF SPECIFIED TYPE from the DB, creates a
	 * java bean for each one and adds them to an ArrayList collection. returns that
	 * collection.
	 * 
	 * 
	 * @param couponType
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws CouponSystemException
	 */
	public Collection<Coupon> getCouponsByType(CouponType couponType)
			throws ConnectionPoolException, DAOException, CouponSystemException;

	/**
	 * ## This is a UTILITY method ## remove a coupon from the DB using it's DB id.
	 * 
	 * @param id
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeCouponWithId(long id) throws ConnectionPoolException, DAOException;

	/**
	 * ## This is a UTILITY method ## update a coupon on the DB based on it's DB Id.
	 *
	 * 
	 * @param id
	 * @param newTitle
	 * @param newStartDate
	 * @param newEndDate
	 * @param newAmount
	 * @param newType
	 * @param newMessage
	 * @param newPrice
	 * @param newImage
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void updateCouponWithId(long id, String newTitle, Date newStartDate, Date newEndDate, int newAmount,
			CouponType newType, String newMessage, double newPrice, String newImage)
			throws ConnectionPoolException, DAOException;

	/**
	 * this method returns one coupon by it's title. returns null if coupon with the
	 * specified title does not exist. used mainly to check name availability for
	 * new coupons.
	 * 
	 * @param title
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	Coupon getCouponByName(String title) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to insert newly created coupons to the company_coupon
	 * JOIN table, listing the id of the company that created the coupon, and the id
	 * of the coupon created.
	 * 
	 * @param compID
	 * @param couponID
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void populateCompanyCoupon(long compID, long couponID) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to remove a coupon listing from the company_coupon JOIN
	 * table, upon coupon removal.
	 * 
	 * @param couponId
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeFromCompanyCoupon(long couponId) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to insert newly purchased coupons to the customer_coupon
	 * JOIN table, listing the id of the customer that purchased the coupon, and the
	 * id of the coupon purchased.
	 * 
	 * @param custID
	 * @param couponID
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void populateCustomerCoupon(long custID, long couponID) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to remove a coupon listing from the customer_coupon JOIN
	 * table, upon coupon removal.
	 * 
	 * @param couponId
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeFromCustomerCoupon(long couponId) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to remove all coupon listings from the customer_coupon
	 * JOIN table, once the specified customer is being removed.
	 * 
	 * @param comp
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeDeletedCustomersCoupons(Customer cust) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to delete the coupons of a deleted company from the
	 * COUPON TABLE only.
	 * 
	 * @param comp
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeDeletedCompanysCoupons(Company comp) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to remove coupon listings from the company_coupon JOIN
	 * table, when the specified company is being deleted.
	 * 
	 * @param comp
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeCouponsFromCompany_Coupon(Company comp) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to remove purchased coupon listings from the
	 * customer_coupon JOIN table, when the specified company is being deleted.
	 * 
	 * @param comp
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeCouponsFromCustomer_Coupon(Company comp) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to remove expired coupons from the coupon table. gets
	 * today's date as a parameter and builds the SQL query around it. mainly used
	 * from the {@link DailyCouponExpirationTask}.
	 * 
	 * @param today
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeExpiredFromCoupon(Date today) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to remove expired coupons from the company_coupon JOIN
	 * table. gets today's date as a parameter and builds the SQL query around it.
	 * mainly used from the {@link DailyCouponExpirationTask}.
	 * 
	 * @param today
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeExpiredFromCompanyCoupon(Date today) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to remove expired coupons from the customer_coupon JOIN
	 * table. gets today's date as a parameter and builds the SQL query around it.
	 * mainly used from the {@link DailyCouponExpirationTask}.
	 * 
	 * @param today
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeExpiredFromCustomerCoupon(Date today) throws ConnectionPoolException, DAOException;

}
