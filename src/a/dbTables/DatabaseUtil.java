package a.dbTables;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import z.utilities.ResourceFetcher;

public class DatabaseUtil {

	/**
	 * this method is used to quickly create all DB tables. mainly done in the
	 * beginning of a test.
	 */
	public static void createAllTables() {
		// CREATE TABLES:
		createCompanyTable();
		createCustomerTable();
		createCouponTable();
		createCompany_CouponTable();
		createCustomer_CouponTable();
		createCompanyIncomeTable();

	}

	/**
	 * this method is used to quickly delete all DB tables. mainly done in the
	 * beginning of a test.
	 */
	public static void dropAllTables() {
		// DROP TABLES:
		dropCompanyTable();
		dropCustomerTable();
		dropCouponTable();
		dropCompany_CouponTable();
		dropCustomer_CouponTable();
		dropCompanyIncomeTable();

	}

	/**
	 * this main method is used to create or completely delete the different DB
	 * tables *INDIVIDUALLY*. all code lines are commented out- enable only what you
	 * need to use. make sure to comment out everything after you finished.
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		// CREATE TABLES:
		// createCompanyTable();
		// createCustomerTable();
		// createCouponTable();
		// createCompany_CouponTable();
		// createCustomer_CouponTable();
		// createCompanyIncomeTable();

		// DROP TABLES:
		// dropCompanyTable();
		// dropCustomerTable();
		// dropCouponTable();
		// dropCompany_CouponTable();
		// dropCustomer_CouponTable();
		// dropCompanyIncomeTable();

		System.out.println("main method finished");
	}

	private static String url = getUrl();

	/*
	 * static initializer to 'populate' the url String with the DB address from the
	 * properties file. allows for easy changing of the url address, even for
	 * non-programmers.
	 */
	static String getUrl() {
		String url1 = null;
		try {
			url1 = ResourceFetcher.getUrl();
			url1 += ";create=true";
		} catch (IOException e) {
			System.err.println("FAILED while trying to get url string from properties file; " + e.getMessage());
		}
		return url1;
	}

	public static void createCompanyTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "CREATE TABLE company(";
			sql += "Id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1 , INCREMENT BY 1), ";
			sql += "comp_name varchar(30) NOT NULL UNIQUE, ";
			sql += "password varchar(8) NOT NULL, ";
			sql += "email varchar(30) NOT NULL";
			sql += ")";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully created COMPANY table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void dropCompanyTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "DROP TABLE company";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully deleted COMPANY table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void createCustomerTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "CREATE TABLE customer(";
			sql += "Id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1 , INCREMENT BY 1), ";
			sql += "cust_name varchar(30) NOT NULL UNIQUE,";
			sql += "password varchar(8) NOT NULL";
			sql += ")";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully created CUSTOMER table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void dropCustomerTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "DROP TABLE customer";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully deleted CUSTOMER table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void createCouponTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "CREATE TABLE coupon(";
			sql += "Id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1 , INCREMENT BY 1), ";
			sql += "title varchar(30) NOT NULL UNIQUE, ";
			sql += "start_date DATE, ";
			sql += "end_date DATE, ";
			sql += "amount INTEGER NOT NULL, ";
			sql += "type VARCHAR(20), ";
			sql += "message VARCHAR(50), ";
			sql += "price FLOAT NOT NULL, ";
			sql += "image VARCHAR(100)";
			sql += ")";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully created COUPON table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void dropCouponTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "DROP TABLE coupon";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully deleted COUPON table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void createCompany_CouponTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "CREATE TABLE company_coupon(";
			sql += "company_id BIGINT, coupon_id BIGINT, ";
			sql += "PRIMARY KEY(company_id,coupon_id)";
			sql += ")";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully created COMPANY_COUPON table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void dropCompany_CouponTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "DROP TABLE company_coupon";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully deleted COMPANY_COUPON table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void createCustomer_CouponTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "CREATE TABLE customer_coupon(";
			sql += "customer_id BIGINT, coupon_id BIGINT, ";
			sql += "PRIMARY KEY(customer_id,coupon_id)";
			sql += ")";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully created CUSTOMER_COUPON table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void dropCustomer_CouponTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "DROP TABLE customer_coupon";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully deleted CUSTOMER_COUPON table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	// table definitions, build and drop methods:
	// **************
	public static void createCompanyIncomeTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "CREATE TABLE company_income(";
			sql += "Id BIGINT NOT NULL PRIMARY KEY, ";
			sql += "comp_name varchar(30) NOT NULL UNIQUE, ";
			sql += "coupons_sold BIGINT NOT NULL, ";
			sql += "overall_income FLOAT NOT NULL";
			sql += ")";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully created COMPANY_INCOME table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void dropCompanyIncomeTable() {

		try (Connection con = DriverManager.getConnection(url);) {
			String sql = "DROP TABLE company_income";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			if (stmt != null) {
				stmt.close();
			}
			System.out.println("successfully deleted COMPANY table.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

}
