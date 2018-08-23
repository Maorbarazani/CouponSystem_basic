package d.DAO;

import java.sql.Date;
import java.util.Collection;

import c.javaBeans.Company;
import c.javaBeans.Coupon;
import c.javaBeans.CouponType;
import x.exceptions.ConnectionPoolException;
import x.exceptions.DAOException;

public interface CompanyDAO {

	/**
	 * this method is called from the CouponSystem level and gets a name and
	 * password. checks the DB to see if it exists and fits, and returns true to
	 * allow a successful login and acquiring a facade.
	 * 
	 * @param compName
	 * @param password
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public boolean login(String compName, String password) throws ConnectionPoolException, DAOException;

	/**
	 * create a company on the DB. this is also used to get the automatically
	 * generated company ID from the SQL DB, and assign it to the COMPANY Java bean
	 * associated with the facade.
	 * 
	 * @param comp
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public long createCompany(Company comp) throws ConnectionPoolException, DAOException;

	/**
	 * remove a company from the DB using using its company.id as identifier
	 * 
	 * @param comp
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public void removeCompany(Company comp) throws ConnectionPoolException, DAOException;

	/**
	 * update a company (password, email) on the DB using its company.id as
	 * identifier
	 *
	 * @param comp
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public void updateCompany(Company comp) throws ConnectionPoolException, DAOException;

	/**
	 * this method returns from the DB 1 company details with the specified
	 * companyID, creates a new Company java bean, sets all of the parameters
	 * according to the DB information retrieved, and returns that Company object.
	 * 
	 * @param compId
	 * @return
	 * @throws DAOException
	 * @throws ConnectionPoolException
	 */
	public Company getCompany(long compId) throws DAOException, ConnectionPoolException;

	/**
	 * this method gets all the companies from the DB, creates a java bean for each
	 * one and adds them to an ArrayList collection. returns that collection.
	 * 
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public Collection<Company> getAllCompanies() throws ConnectionPoolException, DAOException;

	/**
	 * this method gets all the coupons of a specified company from the DB, creates
	 * a java bean for each one and adds them to an ArrayList collection. returns
	 * that collection.
	 * 
	 * @param comp
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public Collection<Coupon> getCoupons(Company comp) throws ConnectionPoolException, DAOException;

	/**
	 * this method removes a company from the DB using it's DB id passed in as a
	 * parameter.
	 * 
	 * @param id
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void removeCompanyWithId(long id) throws ConnectionPoolException, DAOException;

	/**
	 * ## This is a UTILITY method ## update a company on the DB based on it's DB Id
	 * passed in as a parameter, along with the attributes to change.
	 * 
	 * @param id
	 * @param newName
	 * @param newPassword
	 * @param newEmail
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void updateCompanyWithId(long id, String newName, String newPassword, String newEmail)
			throws ConnectionPoolException, DAOException;

	/**
	 * this method uses a company name as a received parameter and returns that
	 * company as an object from the DB, if exists. it is also used to check name
	 * availability before creating a new company on the DB.
	 * 
	 * @param name
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	Company getCompanyByName(String name) throws ConnectionPoolException, DAOException;

	/**
	 * this method gets all the coupons of a specified type, of a specified company
	 * from the DB, creates a java bean for each one and adds them to an ArrayList
	 * collection. returns that collection.
	 * 
	 * @param comp
	 * @param type
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	Collection<Coupon> getCouponsByType(Company comp, CouponType type) throws ConnectionPoolException, DAOException;

	/**
	 * this method gets all the coupons of a specified company from the DB up to a
	 * specified price, creates a java bean for each one and adds them to an
	 * ArrayList collection. returns that collection.
	 * 
	 * @param comp
	 * @param price
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	Collection<Coupon> getCouponsByPrice(Company comp, double price) throws ConnectionPoolException, DAOException;

	/**
	 * this method gets all the coupons of a specified company from the DB up to a
	 * specified date, creates a java bean for each one and adds them to an
	 * ArrayList collection. returns that collection.
	 * 
	 * @param comp
	 * @param date
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	Collection<Coupon> getCouponsUpToDate(Company comp, Date date) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used in order to keep track of companies' income generated
	 * from selling coupons. each time a customer purchases a coupon, this method
	 * gets the coupon as a parameter, finds the Company associated with it on the
	 * DB, check to see whether this Company has former income documentation and
	 * then creates/updates that income documentation accordingly.
	 * 
	 * @param coupon
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	void updateCompanyIncome(Coupon coupon) throws ConnectionPoolException, DAOException;

	/**
	 * this method is used to get a specific Company's income listing from the DB.
	 * returns 0 if no income has been generated yet or the company does not exist.
	 * 
	 * @param comp
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	double getCompanyIncome(Company comp) throws ConnectionPoolException, DAOException;

}
