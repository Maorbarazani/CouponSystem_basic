package z.utilities;

import java.io.IOException;
import java.util.Collection;

import c.javaBeans.Company;
import c.javaBeans.Coupon;
import c.javaBeans.CouponType;
import c.javaBeans.Customer;
import d.DAO.CouponDAO;
import d.DAO.CouponDbDAO;
import e.facade.AdminFacade;
import e.facade.ClientType;
import e.facade.CompanyFacade;
import e.facade.CustomerFacade;
import f.system.CouponSystem;
import x.exceptions.CouponSystemException;

public class test {

	public static void main(String[] args) {

		// re-creating DB:
		/*
		DatabaseUtil.dropAllTables();
		DatabaseUtil.createAllTables();
		*/
		CouponSystem cs = null;
		try {
			cs = CouponSystem.getInstance();
			// **
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			// **
			try {
				AdminFacade af = (AdminFacade) cs.login("admin", "56wrong", ClientType.ADMIN);
				af.createCompany(null);
			} catch (CouponSystemException | IOException e) {
				System.err.println(e.getMessage());
			}
			// **
			AdminFacade admin = (AdminFacade) cs.login("admin", "1234", ClientType.ADMIN);
			// **
			admin.createCompany(new Company("Nike", "nikepass", "support@nike.com"));
			// **
			try {
				admin.createCompany(new Company("StamComp", "passLongerThan8", "exception@facade.level"));
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			admin.createCompany(new Company("APPLE", "pass123", "steve@jobs.com"));
			// **
			try {
				admin.createCompany(new Company("appLE", "apple2", "exception@facade.level"));
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			Company orange = new Company("orange", "orng", "orange@orange");
			admin.createCompany(orange);
			// **
			Company nonExist = new Company();
			nonExist.setId(99);
			try {
				admin.removeCompany(nonExist);
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			orange.setCompName("NewOrange");
			orange.setEmail("newOrange@mail");
			orange.setPassword("newPass");
			admin.updateCompany(orange);
			// **
			Company nonExist2 = new Company();
			nonExist2.setId(959);
			nonExist2.setEmail("not@exist");
			nonExist2.setPassword("nonpass");
			try {
				admin.updateCompany(nonExist2);
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}

			// **
			System.out.println(admin.getCompany(1));
			// **
			try {
				System.out.println(admin.getCompany(888));
				;
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			listPrint(admin.getAllCompanies());
			// **
			try {
				admin.getAllCustomers();
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}

			// **
			Customer Shelly = new Customer("Shelly", "shulshul");
			admin.createCustomer(Shelly);
			admin.createCustomer(new Customer("Ricky", "kikibar"));
			admin.createCustomer(new Customer("Ziv", "zivziv"));
			// **
			try {
				admin.createCustomer(new Customer(null, "nullname"));
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			Shelly.setPassword("shelshel");
			admin.updateCustomer(Shelly);
			// **
			try {
				Shelly.setPassword("shulashelly");
				admin.updateCustomer(Shelly);
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			System.out.println(admin.getCustomer(1));
			// **
			listPrint(admin.getAllCustomers());
			// **
			try {
				cs.login("notexsit", "notexist", ClientType.COMPANY);
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			CompanyFacade nike = (CompanyFacade) cs.login("nike", "nikepass", ClientType.COMPANY);
			// **
			try {
				nike.getAllCompanyCoupons();
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			Coupon c1 = new Coupon("shoes", DateMaker.setDate(1987, 03, 01), DateMaker.setDate(2020, 05, 05), 66,
					CouponType.SPORT, "get new shoes", 289.90, "img1.jpg");
			nike.createCoupon(c1);
			// **
			try {
				nike.createCoupon(new Coupon("negativeAmount", DateMaker.setDate(2020, 04, 05),
						DateMaker.setDate(2020, 05, 05), -15, CouponType.FOOD, "illegal couponn with negative amount",
						99, "https://picsum.photos/100?random"));
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			nike.createCoupon(new Coupon("stamCoup1", DateMaker.setDate(2018, 11, 11), DateMaker.setDate(2018, 12, 11),
					13, CouponType.HOTELS, "msg01", 223, "https://picsum.photos/100?random"));
			nike.createCoupon(new Coupon("stamCoup2", DateMaker.setDate(2019, 11, 11), DateMaker.setDate(2019, 12, 11),
					23, CouponType.SPORT, "msg02", 55, "https://picsum.photos/100?random"));
			// **
			try {
				Coupon notExist = new Coupon("not", DateMaker.setDate(1987, 03, 01), DateMaker.setDate(2020, 05, 05),
						66, CouponType.SPORT, "coupon not exsit on DB", 289.90, "https://picsum.photos/100?random");
				notExist.setId(5);
				nike.removeCoupon(notExist);
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			c1.setPrice(89.50);
			nike.updateCoupon(c1);
			// **
			try {
				c1.setEndDate(DateMaker.setDate(1984, 01, 24));
				nike.updateCoupon(c1);
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			listPrint(nike.getAllCompanyCoupons());
			// **
			listPrint(nike.getCouponsByType(CouponType.SPORT));
			// **
			try {
				nike.getCouponsByType(CouponType.BOOKS);
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			listPrint(nike.getCouponsUpToDate(DateMaker.setDate(2020, 01, 01)));
			// **
			listPrint(nike.getCouponsByPrice(99));
			// **
			CompanyFacade apple = (CompanyFacade) cs.login("apple", "pass123", ClientType.COMPANY);
			// **
			apple.createCoupon(new Coupon("iPod", DateMaker.setDate(2006, 04, 01), DateMaker.setDate(2021, 11, 29),
					4466, CouponType.MUSIC, "listen to music", 159, "https://picsum.photos/100?random"));
			// **
			apple.createCoupon(new Coupon("iPhone", DateMaker.setDate(2008, 10, 10), DateMaker.setDate(2030, 05, 15),
					11000, CouponType.HEALTH, "its a phone", 249, "https://picsum.photos/100?random"));
			// **
			try {
				apple.createCoupon(new Coupon("iPod", DateMaker.setDate(2009, 10, 10), DateMaker.setDate(2019, 05, 15),
						78, CouponType.CAMPING, "another iPode?!", 12, "https://picsum.photos/100?random"));
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			CustomerFacade shelly = (CustomerFacade) cs.login("shELLy", "shelshel", ClientType.CUSTOMER);
			// **

			try {
				shelly.getAllCustomerCoupon();
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			Coupon coup4Purchase = new Coupon();
			coup4Purchase.setId(1);
			shelly.purchaseCoupon(coup4Purchase);
			// **
			coup4Purchase.setId(4);
			shelly.purchaseCoupon(coup4Purchase);
			// **
			coup4Purchase.setId(3);
			shelly.purchaseCoupon(coup4Purchase);
			// **
			try {
				coup4Purchase.setId(4);
				shelly.purchaseCoupon(coup4Purchase);
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			listPrint(shelly.getAllCustomerCoupon());
			// **
			listPrint(shelly.getCouponsByType(CouponType.SPORT));
			// **
			try {
				listPrint(shelly.getCouponsByPrice(39.90));
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			CustomerFacade ziv = (CustomerFacade) cs.login("ziv", "zivziv", ClientType.CUSTOMER);
			// **
			coup4Purchase.setId(4);
			ziv.purchaseCoupon(coup4Purchase);
			// **
			coup4Purchase.setId(5);
			ziv.purchaseCoupon(coup4Purchase);
			// **
			coup4Purchase.setId(1);
			ziv.purchaseCoupon(coup4Purchase);
			// **
			listPrint(ziv.getAllCustomerCoupon());
			// **
			admin.removeCompany(admin.getCompany(2));
			// **
			listPrint(ziv.getAllCustomerCoupon());
			// **
			CustomerFacade ricky = (CustomerFacade) cs.login("ricky", "kikibar", ClientType.CUSTOMER);
			// **
			coup4Purchase.setId(2);
			ricky.purchaseCoupon(coup4Purchase);
			// **
			coup4Purchase.setId(3);
			ricky.purchaseCoupon(coup4Purchase);
			// **
			nike.removeCoupon(nike.getSpecificCoupon(2));
			// **
			listPrint(ricky.getAllCustomerCoupon());
			// **
			admin.removeCustomer(admin.getCustomer(2));
			// **
			try {
				ricky.getAllCustomerCoupon();
			} catch (CouponSystemException e) {
				System.err.println(e.getMessage());
			}
			// **
			System.out.println("Nike company income= " + nike.getCompanyIncome() + "\n");
			// **
			nike.createCoupon(new Coupon("oldCoup", DateMaker.setDate(1995, 11, 04), DateMaker.setDate(1997, 9, 18), 45,
					CouponType.HEALTH, "this coupon is expired and will be removed soon", 120,
					"https://picsum.photos/100?random"));
			// **
			listPrint(nike.getAllCompanyCoupons());
			// **
			admin.manuallyRemoveExpiredCoupons();
			// **
			listPrint(nike.getAllCompanyCoupons());

			nike.createCoupon(new Coupon("7", DateMaker.setDate(2019, 07, 07), DateMaker.setDate(2019, 07, 07), 777,
					CouponType.BOOKS, "777", 77, "https://picsum.photos/100?random"));

			for (int i = 0; i < 5; i++) {
				admin.createCompany(new Company("cname" + i, "cpass" + i, "cemail@yahoo.com" + i));
				for (int j = 0; j < 3; j++) {
					CompanyFacade comp = (CompanyFacade) cs.login("cname" + i, "cpass" + i, ClientType.COMPANY);
					comp.createCoupon(new Coupon("comp" + i + "coup" + j, DateMaker.setDate(2019 + i, j, i),
							DateMaker.setDate(2021 + j, i, j), (i + 3) * (j + 1), CouponType.HOTELS, "coupMsg" + i + j,
							i + i + j + j, "https://picsum.photos/100?random"));
				}
			}
			CouponDAO cDao = new CouponDbDAO();
			for (int i = 0; i < 5; i++) {
				admin.createCustomer(new Customer("cust" + i * 2, "custpas" + i));
				for (int j = 0; j < 3; j++) {
					CustomerFacade cust = (CustomerFacade) cs.login("cust" + i * 2, "custpas" + i, ClientType.CUSTOMER);
					try {
						cust.purchaseCoupon(cDao.getCoupon(j + i + 7));
					} catch (Exception e) {
					}
				}
			}

			// ###################################################
			// ############## END OF TESTING SCRIPT ##############
			// ###################################################

		} catch (CouponSystemException | IOException e) {
			e.printStackTrace();
			if (e.getCause() != null) {
				System.err.println(e.getCause());
				System.err.println("*****");
			}
		} finally {
			if (cs != null) {
				try {
					cs.shutdown();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

		}
		System.out.println("\n main method 'test' has ended");
	}

	/**
	 * this utility method is used to conveniently print collections to the console
	 * in a 1-object-1-row style. not a part of the coupons system, only used for
	 * testing purposes.
	 * 
	 * @param list
	 */
	public static void listPrint(Collection<?> list) {
		System.out.println("***** List Print *****");
		for (Object obj : list) {
			System.out.println(obj);

		}
		System.out.println("**********************");
	}

}
