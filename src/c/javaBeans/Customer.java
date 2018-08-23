package c.javaBeans;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Customer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 274875414281204110L;
	private long id;
	private String custName;
	private String password;
	private Collection<Coupon> coupons;

	/**
	 * Empty CTOR
	 */
	public Customer() {
		super();
	}

	/**
	 * create a new Customer CTOR
	 * 
	 * @param custName
	 * @param password
	 */
	public Customer(String custName, String password) {
		super();
		this.custName = custName;
		this.password = password;
	}

	/**
	 * all parameters CTOR, just for good measures, and not to be used manually.
	 * 
	 * @param id
	 * @param custName
	 * @param password
	 * @param coupons
	 */
	public Customer(long id, String custName, String password, Collection<Coupon> coupons) {
		super();
		this.id = id;
		this.custName = custName;
		this.password = password;
		this.coupons = coupons;
	}

	// get-set:
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", custName=" + custName + ", password=" + password + "]";
	}

}
