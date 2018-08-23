package d.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import b.connections.ConnectionPool;
import c.javaBeans.Coupon;
import c.javaBeans.CouponType;
import c.javaBeans.Customer;
import x.exceptions.ConnectionPoolException;
import x.exceptions.DAOException;

public class CustomerDbDAO implements CustomerDAO {

	private ConnectionPool pool;

	/**
	 * CTOR:
	 * 
	 * @throws ConnectionPoolException
	 */
	public CustomerDbDAO() throws ConnectionPoolException {
		super();
		this.pool = ConnectionPool.getInstance();
	}

	@Override
	public boolean login(String custName, String password) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "SELECT * FROM customer WHERE lower(cust_name) = '" + custName.toLowerCase() + "'";
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// getting password from the DB:
			String dbPass = null;
			while (rs.next()) {
				dbPass = rs.getString(3);
			}

			// upon successful query- close resources:
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

			// checking if password is correct:
			if (password.equals(dbPass)) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw new DAOException(
					"DAO: Failed while trying to get customer *" + custName + "* login details from DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public long createCustomer(Customer cust) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "INSERT INTO customer";
		sql += "(cust_name, password) VALUES";
		sql += "(?,?)";
		long custIdInDB = 0;
		Connection con = pool.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, cust.getCustName());
			pstmt.setString(2, cust.getPassword());
			pstmt.executeUpdate();

			// getting auto-generated ID from the DB, and setting it to the java bean:
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			custIdInDB = rs.getLong(1);
			cust.setId(custIdInDB);

			// upon successful creation- close resources and print to console:
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println(
					"customer *" + cust.getCustName() + "* was created successfully. (with DB id: " + custIdInDB + ")");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to create customer *" + cust.getCustName() + "* on the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

		// returning the DB company ID for further use if needed, and for self-checking
		// while testing the project phases.
		return custIdInDB;

	}

	@Override
	public void removeCustomer(Customer cust) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM customer WHERE id = ";
		sql += cust.getId();
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("customer *" + cust.getCustName() + "* was deleted successfully");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to remove customer *" + cust.getCustName() + "* on the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void updateCustomer(Customer cust) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "UPDATE customer SET cust_name = ?, password = ? ";
		sql += "WHERE id = ?";
		Connection con = pool.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, cust.getCustName());
			pstmt.setString(2, cust.getPassword());
			pstmt.setLong(3, cust.getId());
			pstmt.executeUpdate();

			// upon successful update- close resources and print to console:
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println("customer *" + cust.getCustName() + "* was updated successfully");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to update customer *" + cust.getCustName() + "* on the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public Customer getCustomer(long custId) throws ConnectionPoolException, DAOException {
		// initializing Customer java bean:
		Customer cust = null;

		// building and executing SQL statement:
		String sql = "SELECT * FROM customer WHERE id = ";
		sql += custId;
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating Customer java bean if data is available:
			if (rs.next()) {
				cust = new Customer();
				cust.setId(rs.getLong(1));
				cust.setCustName(rs.getString(2));
				cust.setPassword(rs.getString(3));
			}

			// upon successful query- close resources:
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to get customer with ID *" + custId + "* from the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

		return cust;
	}

	@Override
	public Collection<Customer> getAllCustomers() throws ConnectionPoolException, DAOException {
		// creating blank list to be populated:
		List<Customer> list = new ArrayList<>();

		// building and executing SQL statement:
		String sql = "SELECT * FROM customer";
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating the Company list as long as new data is available:
			while (rs.next()) {
				Customer cust = new Customer();
				cust.setId(rs.getLong(1));
				cust.setCustName(rs.getString(2));
				cust.setPassword(rs.getString(3));
				list.add(cust);
			}

			// upon successful query- close resources:
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException e) {
			throw new DAOException("DAO: Failed while trying to getAllCustomers; ", e);

		} finally {
			pool.returnConnection(con);
		}

		return list;
	}

	@Override
	public Collection<Coupon> getCoupons(Customer cust) throws ConnectionPoolException, DAOException {
		// creating blank list to be populated:
		List<Coupon> list = new ArrayList<>();

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon INNER JOIN customer_coupon ON id = customer_coupon.coupon_id WHERE customer_coupon.customer_id=";
		sql += cust.getId();
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating the Coupons list as long as new data is available:
			while (rs.next()) {
				Coupon coupon = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getDate(3));
				coupon.setEndDate(rs.getDate(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getDouble(8));
				coupon.setImage(rs.getString(9));
				list.add(coupon);
			}

			// upon successful query- close resources:
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new DAOException(
					"DAO: Failed while trying to get Customer_Coupons for customer *" + cust.getCustName() + "*; ", e);
		} finally {
			pool.returnConnection(con);
		}

		return list;
	}

	@Override
	public Coupon getOnePurchasedCoupon(Customer cust, Coupon coupon) throws ConnectionPoolException, DAOException {
		// creating blank list to be populated:

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon INNER JOIN customer_coupon ON id = customer_coupon.coupon_id WHERE customer_coupon.customer_id=";
		sql += cust.getId();
		sql += " AND id = ";
		sql += coupon.getId();

		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating the Coupons list as long as new data is available:
			if (rs.next()) {
				Coupon c1 = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getDate(3));
				coupon.setEndDate(rs.getDate(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getDouble(8));
				coupon.setImage(rs.getString(9));
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				return c1;
			} else {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				return null;
			}

		} catch (SQLException e) {
			throw new DAOException(
					"DAO: Failed while trying to get Customer_Coupons for customer *" + cust.getCustName() + "*; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	// MY UTILITY METHODS:

	@Override
	public void removeCustomerWithId(long id) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM customer WHERE id = ";
		sql += id;
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful remove- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("customer on ID " + id + " was deleted successfully");
		} catch (SQLException e) {
			throw new DAOException("Cannot find customer with id " + id + " on DB. REMOVE failed; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void updateCustomerWithId(long id, String newName, String newPassword)
			throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "UPDATE customer SET cust_name = ?, password = ? ";
		sql += "WHERE id = ?";
		Connection con = pool.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, newName);
			pstmt.setString(2, newPassword);
			pstmt.setLong(3, id);
			pstmt.executeUpdate();

			// upon successful update- close resources and print to console:
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println("customer on ID " + id + " was updated successfully");
		} catch (SQLException e) {
			throw new DAOException("Cannot find customer with id " + id + " on DB. UPDATE failed; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public Customer getCustomerByName(String name) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "SELECT * FROM customer WHERE lower(cust_name) = ";
		sql += "'" + name.toLowerCase() + "'";
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				Customer c1 = new Customer();
				c1.setId(rs.getLong(1));
				c1.setCustName(rs.getString(2));
				c1.setPassword(rs.getString(3));
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				return c1;
			} else {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				return null;
			}
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed while trying to check CustomerNameAvailable on DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public Collection<Coupon> getCouponsByType(Customer cust, CouponType type)
			throws ConnectionPoolException, DAOException {
		// creating blank list to be populated:
		List<Coupon> list = new ArrayList<>();

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon INNER JOIN customer_coupon ON id = customer_coupon.coupon_id WHERE customer_coupon.customer_id=";
		sql += cust.getId();
		sql += " AND type = ";
		sql += "'" + type.toString() + "'";
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating the Coupons list as long as new data is available:
			while (rs.next()) {
				Coupon coupon = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getDate(3));
				coupon.setEndDate(rs.getDate(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getDouble(8));
				coupon.setImage(rs.getString(9));
				list.add(coupon);

			}

			// upon successful query- close resources:
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed while trying to get *" + type.toString() + "* coupons for customer *"
					+ cust.getCustName() + "*; ", e);
		} finally {
			pool.returnConnection(con);
		}

		return list;
	}

	@Override
	public Collection<Coupon> getCouponsByPrice(Customer cust, double price)
			throws ConnectionPoolException, DAOException {
		// creating blank list to be populated:
		List<Coupon> list = new ArrayList<>();

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon INNER JOIN customer_coupon ON id = customer_coupon.coupon_id WHERE customer_coupon.customer_id=";
		sql += cust.getId();
		sql += " AND price <= ";
		sql += price;
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating the Coupons list as long as new data is available:
			while (rs.next()) {
				Coupon coupon = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getDate(3));
				coupon.setEndDate(rs.getDate(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getDouble(8));
				coupon.setImage(rs.getString(9));
				list.add(coupon);

			}

			// upon successful query- close resources:
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed while trying to get price:" + price + " coupons for customer *"
					+ cust.getCustName() + "*; ", e);
		} finally {
			pool.returnConnection(con);
		}

		return list;
	}

}
