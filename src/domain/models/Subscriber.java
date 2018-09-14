package domain.models;

import java.io.Serializable;
import java.util.Date;

public class Subscriber extends MSISDN implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8739679135572490569L;

	private Date last_update_time, crbtNextRenewalDate;
	private boolean flag, crbt, locked;

	public Subscriber() {
		super();
	}

	public Subscriber(int id, String msisdn, boolean flag, boolean crbt, Date last_update_time, Date crbtNextRenewalDate, boolean locked) {
		super(id, msisdn);
		this.flag = flag;
		this.crbt = crbt;
		this.last_update_time = last_update_time;
		this.crbtNextRenewalDate = crbtNextRenewalDate;
		this.locked = locked;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean isCrbt() {
		return crbt;
	}

	public void setCrbt(boolean crbt) {
		this.crbt = crbt;
	}

	public Date getLast_update_time() {
		return last_update_time;
	}

	public void setLast_update_time(Date last_update_time) {
		this.last_update_time = last_update_time;
	}

	public Date getCrbtNextRenewalDate() {
		return crbtNextRenewalDate;
	}

	public void setCrbtNextRenewalDate(Date crbtNextRenewalDate) {
		this.crbtNextRenewalDate = crbtNextRenewalDate;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public int hashCode() {
		return getValue().hashCode();
	}

	public boolean equals (Object pp) {
		try {
			Subscriber p = (Subscriber) pp;

			return this.getValue().equals(p.getValue());

		} catch(Throwable th) {

		}

		return false;
	}

}
