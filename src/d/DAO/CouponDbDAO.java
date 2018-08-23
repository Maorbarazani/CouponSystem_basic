package d.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import b.connections.ConnectionPool;
import c.javaBeans.Company;
import c.javaBeans.Coupon;
import c.javaBeans.CouponType;
import c.javaBeans.Customer;
import x.exceptions.ConnectionPoolException;
import x.exceptions.CouponSystemException;
import x.exceptions.DAOException;

public class CouponDbDAO implements CouponDAO {

	private ConnectionPool pool;

	/**
	 * CTOR:
	 * 
	 * @throws ConnectionPoolException
	 */
	public CouponDbDAO() throws ConnectionPoolException {
		super();
		this.pool = ConnectionPool.getInstance();

	}

	@Override
	public long createCoupon(Coupon coupon) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "INSERT INTO coupon";
		sql += "(title, start_date, end_date, amount, type, message, price, image) VALUES";
		sql += "(?,?,?,?,?,?,?,?)";
		long couponIdInDB = 0;

		Connection con = pool.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, coupon.getTitle());
			pstmt.setDate(2, new Date(coupon.getStartDate().getTime()));
			pstmt.setDate(3, new Date(coupon.getEndDate().getTime()));
			pstmt.setInt(4, coupon.getAmount());
			pstmt.setString(5, coupon.getType().toString());
			pstmt.setString(6, coupon.getMessage());
			pstmt.setFloat(7, (float) coupon.getPrice());
			pstmt.setString(8, coupon.getImage());
			pstmt.executeUpdate();

			// getting auto-generated ID from the DB, and setting it to the java bean:
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			couponIdInDB = rs.getLong(1);
			coupon.setId(couponIdInDB);

			// upon successful creation- close resources and print to console:
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println(
					"coupon *" + coupon.getTitle() + "* was created successfully. (with DB id: " + couponIdInDB + ")");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to create coupon *" + coupon.getTitle() + "* on the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

		// returning the DB coupon ID for further use if needed, and for self-checking
		// while testing the project phases.
		return couponIdInDB;

	}

	@Override
	public void removeCoupon(Coupon coupon) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM coupon WHERE id = ";
		sql += coupon.getId();
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("coupon " + coupon.getId() + " was deleted successfully");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to remove coupon " + coupon.getId() + " on the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void updateCoupon(Coupon coupon) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "UPDATE coupon SET title = ?, start_date = ?, end_date = ?, amount = ?, type = ?, message = ?, price = ?, image = ? ";
		sql += "WHERE id = ?";
		Connection con = pool.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, coupon.getTitle());
			pstmt.setDate(2, new Date(coupon.getStartDate().getTime()));
			pstmt.setDate(3, new Date(coupon.getEndDate().getTime()));
			pstmt.setInt(4, coupon.getAmount());
			pstmt.setString(5, coupon.getType().toString());
			pstmt.setString(6, coupon.getMessage());
			pstmt.setFloat(7, (float) coupon.getPrice());
			pstmt.setString(8, coupon.getImage());
			pstmt.setLong(9, coupon.getId());
			pstmt.executeUpdate();

			// upon successful update- close resources and print to console:
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println("coupon *" + coupon.getTitle() + "* was updated successfully");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to update coupon *" + coupon.getTitle() + "* on the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public Coupon getCoupon(long couponId) throws ConnectionPoolException, DAOException {
		// initializing Coupon java bean:
		Coupon coupon = null;

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon WHERE id = ";
		sql += couponId;
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating Coupon java bean if data is available:
			if (rs.next()) {
				coupon = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getDate(3));
				coupon.setEndDate(rs.getDate(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getFloat(8));
				coupon.setImage(rs.getString(9));
			}

			// upon successful query- close resources:
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to get coupon with ID *" + couponId + "* from the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}
		// returning either null or Coupon object;
		return coupon;
	}

	@Override
	public Coupon getOneCompanyCoupon(long compId, long couponId) throws ConnectionPoolException, DAOException {
		// initializing Coupon java bean:
		Coupon coupon = null;

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon WHERE id IN (SELECT coupon_id FROM company_coupon WHERE company_coupon.company_id = ";
		sql += compId;
		sql += " AND company_coupon.coupon_id = ";
		sql += couponId;
		sql += ")";
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating Coupon java bean if data is available:
			if (rs.next()) {
				coupon = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getDate(3));
				coupon.setEndDate(rs.getDate(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getFloat(8));
				coupon.setImage(rs.getString(9));
			}

			// upon successful query- close resources:
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to get coupon with ID *" + couponId + "* from the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}
		// returning either null or Coupon object;
		return coupon;
	}

	@Override
	public Collection<Coupon> getAllCoupons() throws ConnectionPoolException, DAOException {
		// creating blank list to be populated:
		List<Coupon> list = new ArrayList<>();

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon";
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating the Coupon list as long as new data is available:
			while (rs.next()) {
				Coupon coupon = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getDate(3));
				coupon.setEndDate(rs.getDate(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getFloat(8));
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
			throw new DAOException("DAO: Failed while trying to getAllCoupons; ", e);
		} finally {
			pool.returnConnection(con);
		}

		return list;
	}

	@Override
	public Collection<Coupon> getCouponsByType(CouponType couponType)
			throws ConnectionPoolException, DAOException, CouponSystemException {
		// creating blank list to be populated:
		List<Coupon> list = new ArrayList<>();

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon WHERE type = ";
		sql += "'" + couponType.toString() + "'";
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
				coupon.setPrice(rs.getFloat(8));
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
					"DAO: Failed while trying to getAllCoupons by type *" + couponType.toString() + "*; ", e);

		} finally {
			pool.returnConnection(con);
		}
		return list;
	}

	// MY UTILITY METHODS:

	@Override
	public void removeCouponWithId(long id) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM coupon WHERE id = ";
		sql += id;
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful remove- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("coupon on ID " + id + " was deleted successfully");
		} catch (SQLException e) {
			throw new DAOException("Cannot find coupon with id " + id + " on DB. REMOVE failed; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void updateCouponWithId(long id, String newTitle, Date newStartDate, Date newEndDate, int newAmount,
			CouponType newType, String newMessage, double newPrice, String newImage)
			throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "UPDATE coupon SET title = ?, start_date = ?, end_date = ?, amount = ?, type = ?, message = ?, price = ?, image = ? ";
		sql += "WHERE id = ?";
		Connection con = pool.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, newTitle);
			pstmt.setDate(2, newStartDate);
			pstmt.setDate(3, newEndDate);
			pstmt.setInt(4, newAmount);
			pstmt.setString(5, newType.toString());
			pstmt.setString(6, newMessage);
			pstmt.setFloat(7, (float) newPrice);
			pstmt.setString(8, newImage);
			pstmt.setLong(9, id);
			pstmt.executeUpdate();

			// upon successful update- close resources and print to console:
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println("coupon *" + newTitle + "* on ID " + id + " was updated successfully");
		} catch (SQLException e) {
			throw new DAOException("Cannot find coupon with id " + id + " on DB. UPDATE failed; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public Coupon getCouponByName(String title) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon WHERE lower(title) = ";
		sql += "'" + title.toLowerCase() + "'";
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				Coupon coupon = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getDate(3));
				coupon.setEndDate(rs.getDate(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getFloat(8));
				coupon.setImage(rs.getString(9));
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				return coupon;
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
			throw new DAOException("DAO: Failed while trying to getCouponByName *" + title + "* on DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	// JOIN tables management
	// ***********************

	@Override
	public void populateCompanyCoupon(long compID, long couponID) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "INSERT INTO company_coupon";
		sql += "(company_id, coupon_id) VALUES";
		sql += "(?,?)";
		Connection con = pool.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, compID);
			pstmt.setLong(2, couponID);
			pstmt.executeUpdate();

			// upon successful query- close resources and print to console:
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println(
					"successfully populated company_coupon with company " + compID + " <-> coupon " + couponID);
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed while trying to populate company_coupon, company=" + compID
					+ ", coupon=" + couponID + "; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeFromCompanyCoupon(long couponId) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM company_coupon WHERE coupon_id = ";
		sql += couponId;
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully removed from company_coupon on coupon " + couponId);
		} catch (SQLException e) {
			throw new DAOException(
					"DAO: Failed to remove company_coupon id *" + couponId + "* from the company_coupon DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void populateCustomerCoupon(long custID, long couponID) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "INSERT INTO customer_coupon";
		sql += "(customer_id, coupon_id) VALUES";
		sql += "(?,?)";
		Connection con = pool.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, custID);
			pstmt.setLong(2, couponID);
			pstmt.executeUpdate();

			// upon successful query- close resources and print to console:
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println(
					"successfully populated customer_coupon with customer " + custID + " <-> coupon " + couponID);
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed while trying to populate customer_coupon, customer=" + custID
					+ ", coupon=" + couponID + "; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeFromCustomerCoupon(long couponId) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM customer_coupon WHERE coupon_id = ";
		sql += couponId;
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully removed from customer_coupon on coupon " + couponId);
		} catch (SQLException e) {
			throw new DAOException(
					"DAO: Failed to remove customer_coupon id *" + couponId + "* from the customer_coupon DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeDeletedCustomersCoupons(Customer cust) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:

		String sql = "DELETE FROM customer_coupon WHERE customer_id = ";
		sql += cust.getId();

		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully removed from customer_coupon DB, *" + cust.getCustName() + "* coupons;");
		} catch (SQLException e) {
			throw new DAOException(
					"DAO: Failed to remove *" + cust.getCustName() + "* coupons from the customer_coupon DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeDeletedCompanysCoupons(Company comp) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:

		// not working, and no one knows why:
		// String sql = "DELETE FROM coupon INNER JOIN company_coupon ON id =
		// company_coupon.coupon_id WHERE company_coupon.company_id = ";

		String sql = "DELETE FROM coupon WHERE coupon.id IN (SELECT company_coupon.coupon_Id FROM company_coupon WHERE company_coupon.company_id = ";
		sql += comp.getId();
		sql += ")";

		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully removed from coupon DB, *" + comp.getCompName() + "* coupons;");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to remove *" + comp.getCompName() + "* coupons from the coupon DB; ",
					e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeCouponsFromCompany_Coupon(Company comp) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM company_coupon WHERE company_id = ";
		sql += comp.getId();

		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully removed from company_coupon DB, *" + comp.getCompName() + "* coupons;");
		} catch (SQLException e) {
			throw new DAOException(
					"DAO: Failed to remove *" + comp.getCompName() + "* coupons from the company_coupon DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeCouponsFromCustomer_Coupon(Company comp) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM customer_coupon WHERE coupon_id IN (SELECT company_coupon.coupon_Id FROM company_coupon WHERE company_coupon.company_id = ";
		sql += comp.getId();
		sql += ")";

		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully removed from customer_coupon DB, *" + comp.getCompName() + "* coupons;");
		} catch (SQLException e) {
			throw new DAOException(
					"DAO: Failed to remove *" + comp.getCompName() + "* coupons from the customer_coupon DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeExpiredFromCoupon(Date today) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM coupon WHERE end_date < ";
		sql += "'" + today + "'";

		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("#DailyThread# : successfully removed expired coupons from coupon DB;\n");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to remove expired coupons from the coupon DB;", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeExpiredFromCompanyCoupon(Date today) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM company_coupon WHERE company_coupon.coupon_id IN (SELECT coupon.id FROM coupon WHERE coupon.end_date < ";
		sql += "'" + today + "'";
		sql += ")";

		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("#DailyThread# : successfully removed expired coupons from company_coupon DB;");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to remove expired coupons from the company_coupon DB;", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeExpiredFromCustomerCoupon(Date today) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM customer_coupon WHERE customer_coupon.coupon_id IN (SELECT coupon.id FROM coupon WHERE coupon.end_date < ";
		sql += "'" + today + "'";
		sql += ")";

		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("#DailyThread# : successfully removed expired coupons from customer_coupon DB;");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to remove expired coupons from the customer_coupon DB;", e);
		} finally {
			pool.returnConnection(con);
		}

	}

}
