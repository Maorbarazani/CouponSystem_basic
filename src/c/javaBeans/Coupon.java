package c.javaBeans;

import java.io.Serializable;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Coupon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1313251144621713946L;
	private long id;
	private String title;
	private Date startDate;
	private Date endDate;
	private int amount;
	private CouponType type;
	private String message;
	private double price;
	private String image;

	/**
	 * empty CTOR:
	 */
	public Coupon() {
		super();
	}

	/**
	 * create a new coupon CTOR:
	 */
	public Coupon(String title, Date date, Date date2, int amount, CouponType type, String message, double price,
			String image) {
		super();
		this.title = title;
		this.startDate = date;
		this.endDate = date2;
		this.amount = amount;
		this.type = type;
		this.message = message;
		this.price = price;
		this.image = image;
	}

	// get-set:
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date date) {
		this.startDate = date;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public CouponType getType() {
		return type;
	}

	public void setType(CouponType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Coupon [id=" + id + ", title=" + title + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", amount=" + amount + ", type=" + type + ", message=" + message + ", price=" + price + ", image="
				+ image + "]";
	}

}
