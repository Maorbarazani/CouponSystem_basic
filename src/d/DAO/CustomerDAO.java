package d.DAO;

import java.util.Collection;

import c.javaBeans.Coupon;
import c.javaBeans.CouponType;
import c.javaBeans.Customer;
import x.exceptions.ConnectionPoolException;
import x.exceptions.DAOException;

public interface CustomerDAO {

	/**
	 * this method checks the DB for the specified customer's password. returns true
	 * if the password matches (case sensitive) to the password passed as a
	 * parameter. returns false otherwise.
	 * 
	 * @param custName
	 * @param password
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public boolean login(String custName, String password) throws ConnectionPoolException, DAOException;

	/**
	 * create a customer on the DB. this is also used to get the automatically
	 * generated customer ID from the SQL DB, and assign it to this CUSTOMER Java
	 * bean.
	 * 
	 * @param cust
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public long createCustomer(Customer cust) throws ConnectionPoolException, DAOException;

	/**
	 * * remove a customer from the DB using it's DB id.
	 * 
	 * @param cust
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public void removeCustomer(Customer cust) throws ConnectionPoolException, DAOException;

	/**
	 * update a customer (password only) on the DB.
	 * 
	 * @param cust
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public void updateCustomer(Customer cust) throws ConnectionPoolException, DAOException;

	/**
	 * this method returns from the DB 1 customer details with the specified
	 * customerID, creates a new Customer java bean, sets all of the parameters
	 * according to the DB information retrieved, and returns that Customer.
	 * 
	 * @param custId
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public Customer getCustomer(long custId) throws ConnectionPoolException, DAOException;

	/**
	 * this method gets all the customers from the DB, creates a java bean for each
	 * one and adds them to an ArrayList collection. returns that collection.
	 * 
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public Collection<Customer> getAllCustomers() throws ConnectionPoolException, DAOException;

	/**
	 * this method gets all the coupons of a specified customer from the DB, creates
	 * a java bean for each one and adds them to an ArrayList collection. returns
	 * that collection.
	 * 
	 * @param cust
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public Collection<Coupon> getCoupons(Customer cust) throws ConnectionPoolException, DAOException;

	/**
	 * this method exists to establish whether a customer has bought a specific
	 * coupon in the past, or not. it attempts to bring back an existing coupon
	 * based on Id, from the customer_coupon JOIN table, on the specific customer
	 * Id. if not null, the customer has already bought this coupon and will not be
	 * allowed to purchase it again.
	 * 
	 * @param cust
	 * @param coupon
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	Coupon getOnePurchasedCoupon(Customer cust, Coupon coupon) throws ConnectionPoolException, DAOException;

	/**
	 * ## This is a UTILITY method ## remove a customer from the DB using it's DB
	 * id.
	 * 
	 * @param id
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeCustomerWithId(long id) throws ConnectionPoolException, DAOException;

	/**
	 * ## This is a UTILITY method ## update a customer on the DB based on it's DB
	 * Id.
	 * 
	 * @param id
	 * @param newName
	 * @param newPassword
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void updateCustomerWithId(long id, String newName, String newPassword) throws ConnectionPoolException, DAOException;

	/**
	 * this method uses a customer name as a received parameter and returns that
	 * customer as an object from the DB, if exists. it is also used to check name
	 * availability before creating a new customer on the DB.
	 * 
	 * @param name
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	Customer getCustomerByName(String name) throws ConnectionPoolException, DAOException;

	/**
	 * this method gets all the coupons of a specified type, already purchased by a
	 * specific customer.
	 * 
	 * @param cust
	 * @param type
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	Collection<Coupon> getCouponsByType(Customer cust, CouponType type) throws ConnectionPoolException, DAOException;

	/**
	 * this method gets all the coupons up to a specified price, already purchased
	 * by a specific customer.
	 * 
	 * @param cust
	 * @param price
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	Collection<Coupon> getCouponsByPrice(Customer cust, double price) throws ConnectionPoolException, DAOException;
}
