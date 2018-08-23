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
import x.exceptions.ConnectionPoolException;
import x.exceptions.DAOException;

public class CompanyDbDAO implements CompanyDAO {

	private ConnectionPool pool;

	public CompanyDbDAO() throws ConnectionPoolException {
		super();
		this.pool = ConnectionPool.getInstance();
	}

	@Override
	public boolean login(String compName, String password) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "SELECT * FROM company WHERE lower(comp_name) = '" + compName.toLowerCase() + "'";
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
			throw new DAOException("DAO: Failed while trying to get company *" + compName + "* login details from DB; ",
					e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public long createCompany(Company comp) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "INSERT INTO company";
		sql += "(comp_name, password, email) VALUES";
		sql += "(?,?,?)";
		long compIdInDB = 0;
		Connection con = pool.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, comp.getCompName());
			pstmt.setString(2, comp.getPassword());
			pstmt.setString(3, comp.getEmail());
			pstmt.executeUpdate();

			// getting auto-generated ID from the DB, and setting it to the java bean:
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			compIdInDB = rs.getLong(1);
			comp.setId(compIdInDB);

			// upon successful creation- close resources and print to console:
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println(
					"company *" + comp.getCompName() + "* was created successfully. (with DB id: " + compIdInDB + ")");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to create company *" + comp.getCompName() + "* on the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}
		// returning the DB company ID for further use if needed, and for self-checking
		// while testing the project phases.
		return compIdInDB;

	}

	@Override
	public void removeCompany(Company comp) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM company WHERE id = ";
		sql += comp.getId();
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful removal- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("company *" + comp.getCompName() + "* was deleted successfully");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to remove company *" + comp.getCompName() + "* on the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void updateCompany(Company comp) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "UPDATE company SET comp_name = ?, password = ?, email = ? ";
		sql += "WHERE id = ?";
		Connection con = pool.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, comp.getCompName());
			pstmt.setString(2, comp.getPassword());
			pstmt.setString(3, comp.getEmail());
			pstmt.setLong(4, comp.getId());
			pstmt.executeUpdate();

			// upon successful update- close resources and print to console:
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println("company *" + comp.getCompName() + "* was updated successfully");
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to update company *" + comp.getCompName() + "* on the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public Company getCompany(long compId) throws DAOException, ConnectionPoolException {
		// initializing Company java bean:
		Company comp = null;

		// building and executing SQL statement:
		String sql = "SELECT * FROM company WHERE id = ";
		sql += compId;
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating Company java bean if data is available:
			if (rs.next()) {
				comp = new Company();
				comp.setId(rs.getLong(1));
				comp.setCompName(rs.getString(2));
				comp.setPassword(rs.getString(3));
				comp.setEmail(rs.getString(4));
			}

			// upon successful query- close resources:
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to get company with ID *" + compId + "* from the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

		// returning either null or Company object;
		return comp;
	}

	@Override
	public Collection<Company> getAllCompanies() throws ConnectionPoolException, DAOException {
		// creating blank list to be populated:
		List<Company> list = new ArrayList<>();

		// building and executing SQL statement:
		String sql = "SELECT * FROM company";
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating the Company list as long as new data is available:
			while (rs.next()) {
				Company comp = new Company();
				comp.setId(rs.getLong(1));
				comp.setCompName(rs.getString(2));
				comp.setPassword(rs.getString(3));
				comp.setEmail(rs.getString(4));
				list.add(comp);
			}

			// upon successful query- close resources:
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException e) {
			throw new DAOException("DAO: Failed while trying to getAllCompanies; ", e);

		} finally {
			pool.returnConnection(con);
		}

		return list;
	}

	@Override
	public Collection<Coupon> getCoupons(Company comp) throws ConnectionPoolException, DAOException {
		// creating blank list to be populated:
		List<Coupon> list = new ArrayList<>();

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon INNER JOIN company_coupon ON id = company_coupon.coupon_id WHERE company_coupon.company_id=";
		sql += comp.getId();
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
					"DAO: Failed while trying to get Company_Coupons for company *" + comp.getCompName() + "*; ", e);
		} finally {
			pool.returnConnection(con);
		}

		return list;
	}

	// MY UTILITY METHODS:

	@Override
	public void removeCompanyWithId(long id) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "DELETE FROM company WHERE id = ";
		sql += id;
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);

			// upon successful remove- close resources and print to console:
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("company on ID " + id + " was deleted successfully");
		} catch (SQLException e) {
			throw new DAOException("Cannot find company with id " + id + " on DB. REMOVE failed; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void updateCompanyWithId(long id, String newName, String newPassword, String newEmail)
			throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "UPDATE company SET comp_name = ?, password = ?, email = ? ";
		sql += "WHERE id = ?";
		Connection con = pool.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, newName);
			pstmt.setString(2, newPassword);
			pstmt.setString(3, newEmail);
			pstmt.setLong(4, id);
			pstmt.executeUpdate();

			// upon successful update- close resources and print to console:
			if (pstmt != null) {
				pstmt.close();
			}
			System.out.println("company on ID " + id + " was updated successfully");
		} catch (SQLException e) {
			throw new DAOException("Cannot find company with id " + id + " on DB. UPDATE failed; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public Company getCompanyByName(String name) throws ConnectionPoolException, DAOException {
		// building and executing SQL statement:
		String sql = "SELECT * FROM company WHERE lower(comp_name) = ";
		sql += "'" + name.toLowerCase() + "'";
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			// populating Company java bean, or returning null if no data is available:
			if (rs.next()) {
				Company c1 = new Company();
				c1.setId(rs.getLong(1));
				c1.setCompName(rs.getString(2));
				c1.setPassword(rs.getString(3));
				c1.setEmail(rs.getString(4));
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
			throw new DAOException("DAO: Failed while trying to getCompanyByName *" + name + "* on DB; ", e);
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public Collection<Coupon> getCouponsByType(Company comp, CouponType type)
			throws ConnectionPoolException, DAOException {
		// creating blank list to be populated:
		List<Coupon> list = new ArrayList<>();

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon INNER JOIN company_coupon ON id = company_coupon.coupon_id WHERE company_coupon.company_id=";
		sql += comp.getId();
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
			throw new DAOException("DAO: Failed while trying to get *" + type.toString() + "* coupons for company *"
					+ comp.getCompName() + "*; ", e);
		} finally {
			pool.returnConnection(con);
		}

		return list;
	}

	@Override
	public Collection<Coupon> getCouponsByPrice(Company comp, double price)
			throws ConnectionPoolException, DAOException {
		// creating blank list to be populated:
		List<Coupon> list = new ArrayList<>();

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon INNER JOIN company_coupon ON id = company_coupon.coupon_id WHERE company_coupon.company_id=";
		sql += comp.getId();
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
			throw new DAOException("DAO: Failed while trying to get price:" + price + " coupons for company *"
					+ comp.getCompName() + "*; ", e);
		} finally {
			pool.returnConnection(con);
		}

		return list;
	}

	@Override
	public Collection<Coupon> getCouponsUpToDate(Company comp, Date endDate)
			throws ConnectionPoolException, DAOException {
		// creating blank list to be populated:
		List<Coupon> list = new ArrayList<>();

		// building and executing SQL statement:
		String sql = "SELECT * FROM coupon INNER JOIN company_coupon ON id = company_coupon.coupon_id WHERE company_coupon.company_id=";
		sql += comp.getId();
		sql += " AND end_date <= ";
		sql += "'" + endDate + "'";
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
			throw new DAOException("DAO: Failed while trying to get coupons for company *" + comp.getCompName()
					+ "* up to date " + endDate + "; ", e);
		} finally {
			pool.returnConnection(con);
		}

		return list;
	}

	@Override
	public void updateCompanyIncome(Coupon coupon) throws ConnectionPoolException, DAOException {
		String sql = "SELECT * FROM company WHERE company.id IN (SELECT company_coupon.company_id FROM company_coupon WHERE company_coupon.coupon_id = ";
		sql += coupon.getId();
		sql += ")";
		Company comp = null;
		Connection con = pool.getConnection();
		// get the coupon's company:
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				comp = new Company();
				comp.setId(rs.getLong(1));
				comp.setCompName(rs.getString(2));
				comp.setPassword(rs.getString(3));
				comp.setEmail(rs.getString(4));
			}
			// check if the company already has an income listing:
			sql = "SELECT * FROM company_income WHERE id = ";
			sql += comp.getId();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				// a company already has an income listing- get values and update
				long alreadyPurcased = rs.getLong(3);
				double existingIncome = rs.getFloat(4);
				sql = "UPDATE company_income SET coupons_sold = ";
				sql += alreadyPurcased + 1;
				sql += ", overall_income = ";
				sql += existingIncome + coupon.getPrice();
				sql += " WHERE id = ";
				sql += comp.getId();
				stmt.executeUpdate(sql);
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				System.out.println("Succesffuly updated income listing for company *" + comp.getCompName() + "*;");

			} else {
				// a company DOES NOT have an income listing, insert
				sql = "INSERT INTO company_income VALUES (";
				sql += comp.getId() + ", '" + comp.getCompName() + "', " + 1 + ", " + coupon.getPrice();
				sql += ")";
				stmt.executeUpdate(sql);
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				System.out.println("Succesffuly created an income listing for company *" + comp.getCompName() + "*;");
			}
		} catch (SQLException e) {
			throw new DAOException("DAO: Failed while trying to updateCompanyIncome on the DB for coupon id = "
					+ coupon.getId() + "; ");
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public double getCompanyIncome(Company comp) throws ConnectionPoolException, DAOException {
		double income = 0;
		// building and executing SQL statement:
		String sql = "SELECT * FROM company_income WHERE id = ";
		sql += comp.getId();
		Connection con = pool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// populating Company java bean if data is available:
			if (rs.next()) {
				income = rs.getDouble(4);
			}

			// upon successful query- close resources:
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException e) {
			throw new DAOException("DAO: Failed to get company income with ID *" + comp.getId() + "* from the DB; ", e);
		} finally {
			pool.returnConnection(con);
		}
		return income;
	}
}
