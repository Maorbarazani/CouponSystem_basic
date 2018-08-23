package z.utilities;

import java.io.IOException;
import java.util.Collection;

import e.facade.ClientType;
import e.facade.CompanyFacade;
import f.system.CouponSystem;
import x.exceptions.CouponSystemException;

public class test2 {

	public static void main(String[] args) {

		CouponSystem cs = null;
		try {
			cs = CouponSystem.getInstance();
			// **
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			CompanyFacade nike = (CompanyFacade) cs.login("nike", "nikepass", ClientType.COMPANY);
			listPrint(nike.getAllCompanyCoupons());
			listPrint(nike.getCouponsUpToDate(DateMaker.setDate(2020, 01, 01)));

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
