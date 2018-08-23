package f.system;

import java.io.IOException;

import b.connections.ConnectionPool;
import d.DAO.CompanyDAO;
import d.DAO.CompanyDbDAO;
import d.DAO.CustomerDAO;
import d.DAO.CustomerDbDAO;
import e.facade.AdminFacade;
import e.facade.ClientType;
import e.facade.CompanyFacade;
import e.facade.CouponClientFacade;
import e.facade.CustomerFacade;
import x.exceptions.ConnectionPoolException;
import x.exceptions.CouponSystemException;
import x.exceptions.DAOException;
import x.exceptions.LoginException;
import z.utilities.ResourceFetcher;

public class CouponSystem {

	private CompanyDAO compDao = null;
	private CustomerDAO custDao = null;
	Thread dailyThread;

	/**
	 * private singleton CTOR. initializes the DAO attributes, creates and starts a
	 * new thread for the DailyCouponExpirationTask().
	 * 
	 * @throws ConnectionPoolException
	 */
	private CouponSystem() throws ConnectionPoolException {
		super();
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		compDao = new CompanyDbDAO();
		custDao = new CustomerDbDAO();
		DailyCouponExpirationTask expiredTask = new DailyCouponExpirationTask();
		dailyThread = new Thread(expiredTask);
		dailyThread.start();
	}

	/**
	 * static singleton instance initializer. initializes to null, not to handle
	 * exceptions.
	 */
	private static CouponSystem instance = null;

	/**
	 * singleton instance getter. constructs the CouponSystem singleton and returns
	 * it.
	 * 
	 * @return
	 * @throws ConnectionPoolException
	 */
	public static CouponSystem getInstance() throws ConnectionPoolException {
		if (instance == null) {
			instance = new CouponSystem();
		}
		return instance;
	}

	/**
	 * this method is meant to allow the logging in to the system of 3 client types,
	 * checking the username and password specified against the DB, and returning an
	 * appropriate new ClientFacade implementation upon a successful match.
	 * 
	 * @param name
	 * @param password
	 * @param type
	 * @return
	 * @throws ConnectionPoolException
	 * @throws DAOException
	 * @throws LoginException
	 * @throws IOException
	 */
	public CouponClientFacade login(String name, String password, ClientType type)
			throws ConnectionPoolException, DAOException, LoginException, IOException {
		switch (type) {

		case ADMIN:
			String adminName = ResourceFetcher.getAdminName();
			String adminPass = ResourceFetcher.getaAdminPassword();
			if (name.equalsIgnoreCase(adminName) && password.equals(adminPass)) {
				System.out.println("## Successfully logged in as ADMIN");
				return new AdminFacade();
			} else {
				throw new LoginException(
						"Login Failed: wrong username OR password while trying to login as " + type + ";");
			}

		case COMPANY:
			if (compDao.login(name, password)) {
				System.out.println("## Successfully logged in as COMPANY: " + name);
				return new CompanyFacade(name);
			} else {
				throw new LoginException(
						"Login Failed: wrong username OR password while trying to login as " + type + ";");
			}
		case CUSTOMER:
			if (custDao.login(name, password)) {
				System.out.println("## Successfully logged in as CUSTOMER: " + name);
				return new CustomerFacade(name);
			} else {
				throw new LoginException(
						"Login Failed: wrong username OR password while trying to login as " + type + ";");
			}

		default:
			throw new LoginException("Login Failed: trying to login with an unknown login type: " + type);
		}

	}

	/**
	 * this method is used to completely shutdown the CouponSystem. it interrupts
	 * the daily thread and wait's for it to join this CouponSystem thread, and then
	 * closes the connection pool.
	 * 
	 * @throws CouponSystemException
	 */
	public void shutdown() throws CouponSystemException {
		dailyThread.interrupt();
		try {
			dailyThread.join();
		} catch (InterruptedException e) {
			throw new CouponSystemException(
					"Interrupted while waiting for the dailyThread to terminate and join couponSystem", e);
		}
		ConnectionPool.getInstance().closeAllConnections();
		System.out.println("## SYSTEM SHUTDOWN COMPLETED ##");
	}

}
