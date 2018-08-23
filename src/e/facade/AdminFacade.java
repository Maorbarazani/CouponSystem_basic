package e.facade;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;

import c.javaBeans.Company;
import c.javaBeans.Customer;
import d.DAO.CompanyDAO;
import d.DAO.CompanyDbDAO;
import d.DAO.CouponDAO;
import d.DAO.CouponDbDAO;
import d.DAO.CustomerDAO;
import d.DAO.CustomerDbDAO;
import x.exceptions.CompanyAlreadyExistException;
import x.exceptions.CompanyDontExistException;
import x.exceptions.ConnectionPoolException;
import x.exceptions.CustomerAlreadyExistException;
import x.exceptions.CustomerDontExistException;
import x.exceptions.DAOException;
import x.exceptions.FacadeException;

public class AdminFacade implements CouponClientFacade {

	private CompanyDAO compDao = new CompanyDbDAO();
	private CustomerDAO custDao = new CustomerDbDAO();
	private CouponDAO couponDao = new CouponDbDAO();

	/**
	 * CTOR
	 * 
	 * @throws ConnectionPoolException
	 */
	public AdminFacade() throws ConnectionPoolException {
		super();
	}

	// Company management:

	/**
	 * this method receives a Company object, checks the DB to see if that name is
	 * available, and if so- creates this new Company on the DB.
	 * 
	 * @param comp
	 * @throws CompanyAlreadyExistException
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public void createCompany(Company comp)
			throws CompanyAlreadyExistException, ConnectionPoolException, DAOException, FacadeException {
		comp.setCompName(comp.getCompName().trim());
		comp.setEmail(comp.getEmail().trim());
		comp.setPassword(comp.getPassword().trim());
		// making sure no null values are passed
		if (comp.getCompName() == null || comp.getPassword() == null || comp.getEmail() == null) {
			throw new FacadeException("Failed to create company: 1 or more required values were null;");
		}
		// making sure the password length is correct (more string length/format
		// restrictions can be implemented this way here)
		if (comp.getPassword().length() > 8) {
			throw new FacadeException("Failed to create company: password is too long. 8 chars only;");
		}
		// making sure a company with the same name doesn't already exist, and creating
		// a new company
		if (compDao.getCompanyByName(comp.getCompName()) == null) {
			compDao.createCompany(comp);
		} else {
			throw new CompanyAlreadyExistException(
					"Failed to create company: a Company with the name *" + comp.getCompName() + "* already exists;");
		}

	}

	/**
	 * this method receives a Company object, checks the DB to see if that company
	 * actually exists. if so, first deletes all of this company's coupons from the
	 * relevant places in the DB, and then removes this company from the DB.
	 * 
	 * @param comp
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws CompanyDontExistException
	 */
	public void removeCompany(Company comp) throws ConnectionPoolException, DAOException, CompanyDontExistException {
		// trying to populate bean with an actual company, and checking to see if it
		// exists
		comp = compDao.getCompany(comp.getId());
		if (comp == null) {
			throw new CompanyDontExistException("the company you are trying to remove does not exist;");
		}
		// removing this company's coupons from the coupon table
		couponDao.removeDeletedCompanysCoupons(comp);

		// removing this company's coupons already purchased by customers, from the
		// customer_coupon table
		couponDao.removeCouponsFromCustomer_Coupon(comp);

		// removing this company and it's coupons from the company_coupon 'index'
		couponDao.removeCouponsFromCompany_Coupon(comp);

		// removing company from the company table
		compDao.removeCompany(comp);
	}

	/**
	 * this method receives a Company object, checks to ensure it's updateable
	 * attributes are valid, and then updates that company on the DB by retrieving
	 * the existing company, setting's its updatable attributes, and sending it to
	 * an update.
	 * 
	 * @param newComp
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 * @throws CompanyDontExistException
	 */
	public void updateCompany(Company newComp)
			throws ConnectionPoolException, DAOException, FacadeException, CompanyDontExistException {
		// checking the received company doesn't have null values
		if (newComp.getPassword() == null || newComp.getEmail() == null) {
			throw new FacadeException("Failed to update company: email and password values cannot be null;");
		}
		if (newComp.getPassword().length() > 8) {
			throw new FacadeException("Failed to update company: password is too long. 8 chars only;");
		}

		// getting the same company to be updated from the DB, and making it a java
		// bean. updating it's attributes according to the company received.
		Company oldComp = compDao.getCompany(newComp.getId());
		if (oldComp == null) {
			throw new CompanyDontExistException(
					"Failed to update company: company with id " + newComp.getId() + " was not found;");
		}
		oldComp.setPassword(newComp.getPassword());
		oldComp.setEmail(newComp.getEmail());

		// executing update with an updated password and email only.
		compDao.updateCompany(oldComp);

	}

	/**
	 * this method is used to get an existing Company object from the DB, using its
	 * id.
	 * 
	 * @param id
	 * @return {@link Company}
	 * @throws DAOException
	 * @throws ConnectionPoolException
	 * @throws CompanyDontExistException
	 */
	public Company getCompany(long id) throws DAOException, ConnectionPoolException, CompanyDontExistException {

		Company comp = compDao.getCompany(id);
		if (comp != null) {
			return comp;
		} else {
			throw new CompanyDontExistException("Company on id " + id + " does not exist;");
		}

	}

	/**
	 * this method is used to get an existing Company object from the DB, using its
	 * name.
	 * 
	 * @param name
	 * @return {@link Company}
	 * @throws DAOException
	 * @throws ConnectionPoolException
	 * @throws CompanyDontExistException
	 */
	public Company getCompanyByName(String name)
			throws DAOException, ConnectionPoolException, CompanyDontExistException {

		Company comp = compDao.getCompanyByName(name);
		if (comp != null) {
			return comp;
		} else {
			throw new CompanyDontExistException("Company with name *" + name + "* does not exist;");
		}

	}

	/**
	 * this method is used to get all existing companies, or an exception if no
	 * companies exist.
	 * 
	 * @return {@link Collection}
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public Collection<Company> getAllCompanies() throws ConnectionPoolException, DAOException, FacadeException {
		Collection<Company> list = compDao.getAllCompanies();
		if (!list.isEmpty()) {
			return list;
		} else {
			throw new FacadeException("Failed while trying to getAllCompanies: no companies exist;");
		}
	}

	// Customer management:

	/**
	 * this method receives a Customer object, checks the DB to see if that name is
	 * available, and if so- creates this new Customer on the DB.
	 * 
	 * @param cust
	 * @throws CustomerAlreadyExistException
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public void createCustomer(Customer cust)
			throws CustomerAlreadyExistException, ConnectionPoolException, DAOException, FacadeException {
		// making sure no null values are passed
		if (cust.getCustName() == null || cust.getPassword() == null) {
			throw new FacadeException("Failed to create customer: 1 or more required values were null;");
		}
		// making sure the password length is correct (more string length/format
		// restrictions can be implemented this way here)
		if (cust.getPassword().length() > 8) {
			throw new FacadeException("Failed to create customer: password is too long. 8 chars only;");
		}
		cust.setCustName(cust.getCustName().trim());
		cust.setPassword(cust.getPassword().trim());
		// making sure a customer with the same name doesn't already exist, and creating
		// a new customer
		if (custDao.getCustomerByName(cust.getCustName()) == null) {
			custDao.createCustomer(cust);
		} else {
			throw new CustomerAlreadyExistException(
					"Failed to create customer: a Customer with the name *" + cust.getCustName() + "* already exists;");
		}

	}

	/**
	 * this method receives a Customer object, checks the DB to see if that customer
	 * actually exists. if so, first deletes all of this customers purchased coupons
	 * listings from the relevant places in the DB, and then removes this customer
	 * from the DB.
	 * 
	 * @param cust
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws CustomerDontExistException
	 */
	public void removeCustomer(Customer cust) throws ConnectionPoolException, DAOException, CustomerDontExistException {
		// trying to populate bean with an actual customer, and checking to see if it
		// exists
		cust = custDao.getCustomer(cust.getId());
		if (cust == null) {
			throw new CustomerDontExistException("the customer you are trying to remove does not exist;");
		}

		// removing this clients 'records' from the customer_coupon table
		couponDao.removeDeletedCustomersCoupons(cust);

		// removing customer from the customer table
		custDao.removeCustomer(cust);
	}

	/**
	 * this method receives a Customer object, checks to ensure it's updateable
	 * attributes are valid, and then updates that customer on the DB by retrieving
	 * the existing company, setting's its updatable attributes, and sending it to
	 * an update.
	 * 
	 * @param newCust
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 * @throws CustomerDontExistException
	 */
	public void updateCustomer(Customer newCust)
			throws ConnectionPoolException, DAOException, FacadeException, CustomerDontExistException {
		// checking the received customer doesn't have null values
		if (newCust.getPassword() == null) {
			throw new FacadeException("Failed to update customer: new password cannot be null;");
		}

		if (newCust.getPassword().length() > 8) {
			throw new FacadeException("Failed to update customer: password is too long. 8 chars only;");
		}

		// getting the same customer to be updated from the DB, and making it a java
		// bean. updating it's attributes according to the customer received.
		Customer oldCust = custDao.getCustomer(newCust.getId());
		if (oldCust == null) {
			throw new CustomerDontExistException(
					"Failed to update customer: customer with id " + newCust.getId() + " was not found;");
		}

		oldCust.setPassword(newCust.getPassword());
		// executing update with an updated password and email only.
		custDao.updateCustomer(oldCust);

	}

	/**
	 * this method is used to get an existing Customer object from the DB, using its
	 * id.
	 * 
	 * @param id
	 * @return {@link Customer}
	 * @throws DAOException
	 * @throws ConnectionPoolException
	 * @throws CustomerDontExistException
	 */
	public Customer getCustomer(long id) throws DAOException, ConnectionPoolException, CustomerDontExistException {

		Customer cust = custDao.getCustomer(id);
		if (cust != null) {
			return cust;
		} else {
			throw new CustomerDontExistException("Customer on id " + id + " does not exist;");
		}

	}

	/**
	 * this method is used to get an existing Customer object from the DB, using its
	 * name.
	 * 
	 * @param name
	 * @return {@link Customer}
	 * @throws DAOException
	 * @throws ConnectionPoolException
	 * @throws CustomerDontExistException
	 */
	public Customer getCustomerByName(String name)
			throws DAOException, ConnectionPoolException, CustomerDontExistException {
		Customer cust = custDao.getCustomerByName(name);
		if (cust != null) {
			return cust;
		} else {
			throw new CustomerDontExistException("Customer with name *" + name + "* does not exist;");
		}

	}

	/**
	 * this method is used to get all existing customers, or an exception if no
	 * customers exist.
	 * 
	 * @return {@link Collection}
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws FacadeException
	 */
	public Collection<Customer> getAllCustomers() throws ConnectionPoolException, DAOException, FacadeException {
		Collection<Customer> list = custDao.getAllCustomers();
		if (!list.isEmpty()) {
			return list;
		} else {
			throw new FacadeException("Failed while trying to getAllCustomers: no customers exist;");
		}

	}

	/**
	 * this method is used to manually remove expired coupons from the DB. mainly
	 * done for testing purposes. it determines a 'today' date, and then
	 * individually invokes the 3 DAO methods used for removing expired coupons.
	 * 
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 */
	public void manuallyRemoveExpiredCoupons() throws ConnectionPoolException, DAOException {

		Date today = Date.valueOf(LocalDate.now());
		couponDao.removeExpiredFromCompanyCoupon(today);
		couponDao.removeExpiredFromCustomerCoupon(today);
		couponDao.removeExpiredFromCoupon(today);
		System.out.println("## Successfully MANUALLY removed expired coupons ##");
	}

}
