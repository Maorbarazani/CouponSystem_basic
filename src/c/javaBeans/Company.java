package c.javaBeans;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Company implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1067671012057361678L;
	private long id;
	private String compName;
	private String password;
	private String email;
	private Collection<Coupon> coupons;

	/**
	 * Empty CTOR:
	 */
	public Company() {
		super();
	}

	/**
	 * create a new company CTOR:
	 */
	public Company(String compName, String password, String email) {
		super();
		this.compName = compName;
		this.password = password;
		this.email = email;
	}

	/**
	 * all parameters CTOR, just for good measures, and not to be used manually.
	 * 
	 * @param id
	 * @param compName
	 * @param password
	 * @param email
	 * @param coupons
	 */
	public Company(long id, String compName, String password, String email, Collection<Coupon> coupons) {
		super();
		this.id = id;
		this.compName = compName;
		this.password = password;
		this.email = email;
		this.coupons = coupons;
	}

	// get-set:
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}

	@Override
	public String toString() {
		return "Company [id=" + id + ", compName=" + compName + ", password=" + password + ", email=" + email + "]";
	}

}
