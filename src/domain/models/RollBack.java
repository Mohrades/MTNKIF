package domain.models;

import java.util.Date;

public class RollBack {
	
	private int id, step, value;
	private String Anumber, Bnumber;
	private Date error_time;

	public RollBack() {
		
	}
	
	public RollBack(int id, int step, int value, String Anumber, String Bnumber, Date error_time) {
		this.id = id;
		this.step = step;
		this.value = value;
		this.Anumber = Anumber;
		this.Bnumber = Bnumber;
		this.error_time = error_time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public long getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getAnumber() {
		return Anumber;
	}

	public void setAnumber(String anumber) {
		Anumber = anumber;
	}

	public String getBnumber() {
		return Bnumber;
	}

	public void setBnumber(String bnumber) {
		Bnumber = bnumber;
	}

	public Date getError_time() {
		return error_time;
	}

	public void setError_time(Date error_time) {
		this.error_time = error_time;
	}

}
