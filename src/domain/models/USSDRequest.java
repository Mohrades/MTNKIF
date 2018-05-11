package domain.models;

import java.util.Date;

public class USSDRequest {

	private int id, step;
	private long sessionId;
	private String msisdn, input;
	private Date last_update_time;

	public USSDRequest() {

	}

	public USSDRequest(int id, long sessionId, String msisdn, String input, int step, Date last_update_time) {
		this.id = id;
		this.sessionId = sessionId;
		this.msisdn = msisdn;
		this.input = input;
		this.step = step;
		this.last_update_time = last_update_time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public Date getLast_update_time() {
		return last_update_time;
	}

	public void setLast_update_time(Date last_update_time) {
		this.last_update_time = last_update_time;
	}

}
