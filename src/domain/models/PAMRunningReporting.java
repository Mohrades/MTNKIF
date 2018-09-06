package domain.models;

import java.util.Date;

public class PAMRunningReporting implements Comparable<PAMRunningReporting> {

	private int id, subscriber;
	private boolean flag;
	private Date created_date_time;
	private String originOperatorID;

	public PAMRunningReporting() {

	}

	public PAMRunningReporting(int id, int subscriber, boolean flag, Date created_date_time, String originOperatorID) {
		this.id = id;
		this.subscriber = subscriber;
		this.flag = flag;
		this.created_date_time = created_date_time;
		this.originOperatorID = originOperatorID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(int subscriber) {
		this.subscriber = subscriber;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public Date getCreated_date_time() {
		return created_date_time;
	}

	public void setCreated_date_time(Date created_date_time) {
		this.created_date_time = created_date_time;
	}

	public String getOriginOperatorID() {
		return originOperatorID;
	}

	public void setOriginOperatorID(String originOperatorID) {
		this.originOperatorID = originOperatorID;
	}

	public int hashCode() {
		return subscriber;
	}

	public boolean equals (Object pp) {
		try {
			PAMRunningReporting p = (PAMRunningReporting) pp;

			return this.id == p.getId();

		} catch(Throwable th) {

		}

		return false;
	}

	@Override
	public int compareTo(PAMRunningReporting pp) {
		// TODO Auto-generated method stub

		PAMRunningReporting p = (PAMRunningReporting) pp;

		if(this.created_date_time.before(p.getCreated_date_time())) return -1;
		else if(this.created_date_time.after(p.getCreated_date_time())) return 1;
		else return 0;
		// return this.created_date_time.compareTo(p.getCreated_date_time());
	}
}
