package domain.models;

import java.io.Serializable;

public class MSISDN implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6045691419276623261L;

	private int id;
	private String value;

	public MSISDN() {

	}

	public MSISDN(int id, String value) {
		this.id = id;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
