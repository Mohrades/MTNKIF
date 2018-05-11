package domain.models;

import java.util.Date;

public class USSDService {

	private int id, code, requests_count;
	private Date start_date, stop_date;
	private String redirection;

	public USSDService(int id, int code, Date start_date, Date stop_date, int requests_count, String redirection) {
		this.id = id;
		this.code = code;
		this.start_date = start_date;
		this.stop_date = stop_date;
		this.requests_count = requests_count;
		this.redirection = redirection;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getStop_date() {
		return stop_date;
	}

	public void setStop_date(Date stop_date) {
		this.stop_date = stop_date;
	}

	public int getRequests_count() {
		return requests_count;
	}

	public void setRequests_count(int requests_count) {
		this.requests_count = requests_count;
	}

	public String getRedirection() {
		return redirection;
	}

	public void setRedirection(String redirection) {
		this.redirection = redirection;
	}

}
