package z.utilities;

import java.sql.Date;
import java.util.Calendar;

public class DateMaker {

	/**
	 * this is a utility method to easily convert a year-month-day format to
	 * java.sql.Date object. used only for coupon creation and DB queries.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date setDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();

		Date date = new Date(0);

		cal.set(year, month - 1, day);

		date = new Date(cal.getTimeInMillis());

		return date;
	}
}
